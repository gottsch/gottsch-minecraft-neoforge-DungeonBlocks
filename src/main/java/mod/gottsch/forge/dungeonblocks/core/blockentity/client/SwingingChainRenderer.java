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

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.DungeonLanternBlock;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.SwingingChainBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.SwingingChainBlockEntity;
import mod.gottsch.forge.dungeonblocks.core.state.properties.ChainFixture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

/**
 * Draws a swinging chain, one 1-block segment at a time.
 *
 * <p><b>No physics, no state.</b> Every angle here is a closed-form function of the current time,
 * computed by {@link SwingingChainBlockEntity#jointAngles}, so there is nothing to integrate,
 * nothing to desync and nothing to persist. Two motions are summed there: an idle sway, always
 * running, and a damped pendulum driven off the three numbers the BlockEntity synced - the bell
 * does the same thing.
 *
 * <p><b>The whip.</b> Each joint gets a share of the total deflection, tapering downward, evaluated
 * at a progressively later lag. That's what makes the chain trail and curve instead of swinging like
 * a rigid stick — for a fraction of the cost of an actual per-joint verlet solve, and with no
 * possibility of the simulation blowing up.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
@OnlyIn(Dist.CLIENT)
public class SwingingChainRenderer implements BlockEntityRenderer<SwingingChainBlockEntity> {

	@Override
	public AABB getRenderBoundingBox(SwingingChainBlockEntity blockEntity) {
		return blockEntity.getRenderBoundingBox();
	}

	/**
	 * Each link is vanilla's own chain block model, drawn per segment.
	 *
	 * <p>A hand-built {@code ModelPart} was tried first and looked wrong: two crossed zero-depth
	 * boxes produce <em>coincident</em> north/south quads, which z-fight unless backface culling
	 * removes one — and the box UV layout hands each plane a different half of the texture per side,
	 * where vanilla puts strip 0-3 on both faces of one plane and 3-6 on both faces of the other.
	 * Borrowing the real model sidesteps both and guarantees the chain matches vanilla exactly,
	 * resource packs included.
	 */
	private static final BlockState CHAIN_LINK = Blocks.CHAIN.defaultBlockState();

	private final BlockRenderDispatcher blockRenderer;

	public SwingingChainRenderer(BlockEntityRendererProvider.Context context) {
		this.blockRenderer = context.getBlockRenderDispatcher();
	}

	/**
	 * The block model a lantern fixture is drawn from - an existing block, so it needs no geometry
	 * of its own and follows the player's resource pack. The mod's own fixtures have models instead
	 * ({@link ChainFixture#model}).
	 */
	@Nullable
	private static BlockState fixtureState(ChainFixture fixture, boolean lit) {
		return switch (fixture) {
			case LANTERN -> Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
			case SOUL_LANTERN -> Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
			case DUNGEON_LANTERN -> ModBlocks.DUNGEON_LANTERN.get().defaultBlockState()
					.setValue(LanternBlock.HANGING, true)
					.setValue(DungeonLanternBlock.LIT, lit);
			default -> null;
		};
	}

	@Override
	public void render(SwingingChainBlockEntity chain, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Level level = chain.getLevel();
		if (level == null) {
			return;
		}

		int length = chain.getChainLength();
		float now = (float) level.getGameTime() + partialTicks;

		// a fixture only ever sits on the bottom segment, and its weight changes how the chain moves
		BlockState bottom = level.getBlockState(chain.getBlockPos().below(length - 1));
		ChainFixture fixture = bottom.hasProperty(SwingingChainBlock.FIXTURE)
				? bottom.getValue(SwingingChainBlock.FIXTURE)
				: ChainFixture.NONE;
		boolean lit = bottom.hasProperty(SwingingChainBlock.LIT) && bottom.getValue(SwingingChainBlock.LIT);
		BlockState fixtureState = fixtureState(fixture, lit);
		String fixtureModel = fixture.model(lit);
		float[][] angles = chain.jointAngles(now, length, fixture.isWeighted());

		BlockPos pos = chain.getBlockPos();

		poseStack.pushPose();
		// start at the top centre of the anchor block: the chain hangs from the ceiling, not the floor
		poseStack.translate(0.5D, 1.0D, 0.5D);

		for (int i = 0; i < length; i++) {
			poseStack.mulPose(Axis.ZP.rotationDegrees(angles[i][0]));
			poseStack.mulPose(Axis.XP.rotationDegrees(angles[i][1]));

			// The fixture takes the place of the bottom segment's chain link rather than hanging in the
			// air below it. That block is real, so the lantern you see is the block you can click —
			// an air block has nothing to ray-trace against, and a VoxelShape spilling downward would
			// not help, since block selection walks the voxel grid cell by cell. It also puts the
			// light source exactly where the lantern appears.
			//
			// Net effect matches vanilla: N stacked blocks read as (N-1) links plus a lantern, the
			// same as placing N-1 chains and a lantern.
			boolean last = i == length - 1;
			// sample light per segment: the bottom of a long chain can hang into much darker air
			int light = LevelRenderer.getLightColor(level, pos.below(i));

			poseStack.pushPose();
			// the origin is this segment's top joint, while models draw into the unit cube 0..1
			// upward — so drop a block to land it in this segment's own space. Vanilla's hanging
			// lantern model reaches y=16, and so do the mod's fixture models, so a fixture's
			// connector meets the link above with no gap.
			poseStack.translate(-0.5D, -1.0D, -0.5D);
			if (last && fixtureModel != null) {
				BakedModel model = Minecraft.getInstance().getModelManager().getModel(
						ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/" + fixtureModel)));
				// Drawn into the same buffer renderSingleBlock gives the links and lanterns: the
				// entity cutout sheet, whose shader lights each face by its normal. The chunk cutout
				// type this used before has no such lighting in a block entity's pass, so every face
				// came out at full brightness and the dark iron read lighter than the chain above it
				// or the same iron on a chandelier.
				this.blockRenderer.getModelRenderer().renderModel(poseStack.last(),
						buffer.getBuffer(Sheets.cutoutBlockSheet()), null, model, 1.0F, 1.0F, 1.0F, light,
						OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
			} else {
				this.blockRenderer.renderSingleBlock(last && fixtureState != null ? fixtureState : CHAIN_LINK,
						poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
			}
			poseStack.popPose();

			// step down to the next joint, in this segment's rotated frame so the bend accumulates
			poseStack.translate(0.0D, -1.0D, 0.0D);
		}

		poseStack.popPose();
	}
}
