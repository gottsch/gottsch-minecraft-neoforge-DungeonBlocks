/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * DungeonBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DungeonBlocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.blockentity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * The banner's geometry: a rod across the top with a cloth hanging beneath it.
 *
 * <p>Two heights, one model — see {@link Shape}. A pennant is the tall banner's cloth cut short,
 * not a different thing: same width, same rod, same hang plane, same rules below.
 *
 * <p><b>The cloth is a stack of flat, zero-thickness quads, and the taper is painted, not
 * modelled.</b> Both are deliberate, and between them they removed every problem the first version
 * had:
 *
 * <ul>
 * <li><b>Zero thickness makes the seams exact.</b> Each slice hinges on its own top edge, and with
 *     no depth that edge lies <em>exactly</em> on the rotation axis — so it cannot move at all, and
 *     two neighbouring slices stay joined however far apart their angles drift. A 1px-deep slice has
 *     its top edge half a pixel off the axis, which is enough to open a hairline.</li>
 * <li><b>The taper is alpha in the texture.</b> Slices are all the same width, so the art is a plain
 *     rectangle laid out contiguously in the atlas rather than one column-inset strip per slice. The
 *     silhouette costs no geometry, is not quantised to the slice height, and can be reshaped — a
 *     swallowtail, a ragged hem — by editing the PNG alone.</li>
 * <li><b>A handful of slices is enough.</b> The wave is a few degrees; subdividing past this buys
 *     curve smoothness nobody can see, at a quad each.</li>
 * </ul>
 *
 * <p><b>Zero thickness has exactly one requirement</b>, and the renderer has to honour it: the front
 * and back quads are coincident, so they <em>must</em> be drawn with backface culling on
 * ({@code entityCutout}, never {@code entityCutoutNoCull}) or they z-fight. With culling, only one
 * of the two is ever rasterised at a given pixel and the surface is clean.
 *
 * <p><b>Coordinates.</b> {@code DungeonBannerRenderer} sets up the usual entity-model frame, so in
 * here <b>+y is down</b> and <b>+z is into the wall</b>, with the origin at the centre of the block
 * that owns the BlockEntity — for a tall banner that is its upper half. The wall plane is z=8, y=-8
 * is the top of that block, and the geometry is 2px of rod plus the shape's cloth: a pennant ends at
 * y=+8, the bottom of its own block, and a tall banner at y=+24, the bottom of the one below.
 * Neither draws outside the blocks it occupies.
 *
 * <p><b>Texture layout.</b> A zero-depth box unwraps to just two quads, the front at {@code (u, v)}
 * and the back at {@code (u + w, v)}. Stacking the slices at {@code texOffs(0, s * sliceHeight)}
 * therefore makes the fronts one contiguous {@code CLOTH_WIDTH} × {@code clothHeight} block at the
 * atlas origin, with the mirrored backs directly beside it.
 *
 * @author Mark Gottschling on Sep 12, 2026
 */
@OnlyIn(Dist.CLIENT)
public class DungeonBannerModel {

	/**
	 * Everything that differs between the two banner heights. The geometry, the texture layout and
	 * the wave are otherwise identical, so a pennant is a second set of numbers rather than a second
	 * model — the cloth width, the rod, the hang plane and the seam rule are shared.
	 *
	 * <p>The wave constants are not simply copied across. Both the amplitude and the lag are scaled
	 * so the two shapes <em>look</em> alike: deflection accumulates per slice, so the same
	 * per-slice angle on a pennant's seven short slices would bend it far harder than the tall
	 * banner's five long ones, and the same lag would run more than a full wave down a shorter
	 * cloth. What is held constant is the result — a hem excursion of about a tenth of the drop,
	 * and a wave crest that travels a bit under half a cycle from rod to point.
	 */
	public enum Shape {
		/** Two blocks: 30px of cloth in five 6px slices. */
		TALL(30, 5, 0.012F, 0.09F, 0.025F, "main"),
		/**
		 * One block: 14px of cloth in seven 2px slices. Seven is more slices than the tall banner
		 * despite being half the drop, because 14 does not divide by five — and the slices are quads,
		 * so two extra costs nothing worth counting.
		 */
		PENNANT(14, 7, 0.0086F, 0.065F, 0.040F, "pennant");

		public final int clothHeight;
		public final int sliceCount;
		public final int sliceHeight;
		/** Per-slice billow, in radians, and the standing outward bow, which is equal to it. */
		public final float amplitude;
		/** Cycles of delay per slice down the cloth — this is what makes the wave travel. */
		public final float sliceLag;
		/** Whole-cloth roll. Bigger on a pennant: the same angle over half the drop barely shows. */
		public final float leanAmplitude;
		public final ModelLayerLocation layer;

		Shape(int clothHeight, int sliceCount, float amplitude, float sliceLag, float leanAmplitude,
				String layerName) {
			this.clothHeight = clothHeight;
			this.sliceCount = sliceCount;
			this.sliceHeight = clothHeight / sliceCount;
			this.amplitude = amplitude;
			this.sliceLag = sliceLag;
			this.leanAmplitude = leanAmplitude;
			this.layer = new ModelLayerLocation(
					ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "dungeon_banner"), layerName);
		}
	}

	/**
	 * 10px wide, in both shapes — a pennant is the same bolt of cloth cut short, not a different
	 * banner. Against 30px of drop that is roughly 1:3, narrower than vanilla's. Where the cloth
	 * actually ends inside the rectangle is up to the texture's alpha.
	 */
	public static final int CLOTH_WIDTH = 10;

	/** y of the cloth's top edge: just under the rod. */
	private static final float CLOTH_TOP = -6.0F;
	/**
	 * The plane the cloth hangs in: straight down from the rod's centreline, 1px off the wall. It is
	 * the ROD that must sit on the wall (it once stood 1px clear and read as floating); the cloth
	 * hangs a little out from it, and was never the problem. The billow only ever moves the cloth
	 * away from the wall (see {@link #billowBias}), so it never sinks into it.
	 */
	private static final float CLOTH_Z = 7.0F;

	/** The rod's back face lies on the wall plane, z=8: it rests against the wall. */
	private static final float ROD_Z = 6.0F;
	private static final int ROD_WIDTH = 12;
	/** The cloth's two unwraps occupy the top-left of the atlas, so the rod goes below them. */
	private static final int ROD_TEX_U = 0;
	private static final int ROD_TEX_V = 32;

	/** One full turn in radians. */
	private static final float TAU = (float) (Math.PI * 2.0D);

	/**
	 * A standing outward bow, equal to the shape's amplitude, so the billow swings between "flat"
	 * and "bowed out" and <b>never crosses zero into the wall</b>. Without it the wave would drive
	 * the hem backwards on every half-cycle, and the cloth hangs only 1px clear of the wall — it
	 * would sink into the stone twice a cycle. Vanilla's banner biases its sway the same way and for
	 * the same reason. It is also what keeps a motionless banner looking like cloth: see
	 * {@link #still}.
	 */
	private static float billowBias(Shape shape) {
		return shape.amplitude;
	}

	/** Cycles per tick: a 5-second period, the same unhurried rate vanilla banners use. */
	private static final float SPEED = 0.01F;
	/** Detuned against SPEED (~7s) so the lean drifts in and out of phase with the billow. */
	private static final float LEAN_SPEED = 0.0071F;

	private final ModelPart root;
	/** Index 0 is the slice at the rod; each is the previous one's child. */
	private final ModelPart[] slices;

	private final Shape shape;

	public DungeonBannerModel(ModelPart root, Shape shape) {
		this.root = root;
		this.shape = shape;
		this.slices = new ModelPart[shape.sliceCount];
		ModelPart parent = root;
		for (int i = 0; i < shape.sliceCount; i++) {
			parent = parent.getChild(sliceName(i));
			this.slices[i] = parent;
		}
	}

	private static String sliceName(int index) {
		return "slice" + index;
	}

	public static LayerDefinition createBodyLayer(Shape shape) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("rod",
				CubeListBuilder.create()
						.texOffs(ROD_TEX_U, ROD_TEX_V)
						.addBox(-ROD_WIDTH / 2.0F, -8.0F, ROD_Z, ROD_WIDTH, 2.0F, 2.0F, CubeDeformation.NONE),
				PartPose.ZERO);

		// each slice hinges on its own top edge, so its pivot sits there and its quad hangs below it.
		// depth is 0, which puts that whole edge on the rotation axis - the seams cannot open.
		PartDefinition parent = parts;
		for (int i = 0; i < shape.sliceCount; i++) {
			PartPose pose = i == 0
					? PartPose.offset(0.0F, CLOTH_TOP, CLOTH_Z)
					: PartPose.offset(0.0F, shape.sliceHeight, 0.0F);
			parent = parent.addOrReplaceChild(sliceName(i),
					CubeListBuilder.create()
							.texOffs(0, i * shape.sliceHeight)
							.addBox(-CLOTH_WIDTH / 2.0F, 0.0F, 0.0F,
									CLOTH_WIDTH, shape.sliceHeight, 0.0F, CubeDeformation.NONE),
					pose);
		}

		return LayerDefinition.create(mesh, 64, 64);
	}

	/**
	 * Poses the cloth for this frame.
	 *
	 * <p>Every angle is a cosine of the clock — nothing is integrated, so there is no state to keep
	 * and no way for two clients to drift apart.
	 *
	 * <p><b>The rule that matters here: a per-slice hinge may only turn about the seam it shares with
	 * its neighbour.</b> {@code xRot}'s axis <em>is</em> that horizontal seam line, so however far two
	 * neighbouring slices differ, their shared edge stays shared and the cloth reads as one sheet.
	 * {@code zRot} and {@code yRot} turn about axes that cross the seam instead of lying along it, so
	 * applying either per slice tilts each slice's top edge away from the bottom edge of the one above
	 * it — opening a wedge that is invisible at the centre and widest at the cloth's edges.
	 *
	 * <p>So the billow is per-slice and lagged, which is what makes the wave crest travel down to the
	 * hem; the sideways lean is set once, on the top slice, and every slice below inherits it through
	 * the hierarchy — the whole cloth rolls as one rigid sheet and no seam can open.
	 *
	 * @param now   game time in ticks, including partial ticks
	 * @param phase 0..1 offset from the block position, so neighbouring banners aren't in lockstep
	 */
	public void wave(float now, float phase) {
		for (int i = 0; i < this.slices.length; i++) {
			// negative is away from the wall, since +z points into it
			this.slices[i].xRot = -billowBias(this.shape)
					- this.shape.amplitude * Mth.cos(TAU * (now * SPEED + phase + i * this.shape.sliceLag));
		}
		this.slices[0].zRot = this.shape.leanAmplitude * Mth.cos(TAU * (now * LEAN_SPEED + phase));
	}

	/**
	 * Poses the cloth as a still banner, for players who turn the animation off.
	 *
	 * <p>It keeps the standing outward bow rather than going flat. A dead-flat stack of quads pinned
	 * against a wall reads as a painted board; a couple of degrees of bow still reads as hanging
	 * cloth, and costs nothing.
	 */
	public void still() {
		for (ModelPart slice : this.slices) {
			slice.xRot = -billowBias(this.shape);
			slice.zRot = 0.0F;
		}
	}

	public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
		this.root.render(poseStack, consumer, packedLight, packedOverlay);
	}
}
