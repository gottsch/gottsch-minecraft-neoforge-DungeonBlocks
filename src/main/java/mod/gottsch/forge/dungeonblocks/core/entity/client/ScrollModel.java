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
package mod.gottsch.forge.dungeonblocks.core.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A scroll lying on a surface, in texels of its 64x32 texture - {@link ScrollRenderer} draws it at
 * 32 to the block, the icons' pixels one for one. Built y-UP, as the tome model is, and drawn
 * without the usual flip (see {@link TomeModel} for what that does to side faces).
 *
 * <p>Rolled, it is the roll alone, 14 texels across x, tied round the middle with a wax seal on top.
 * Unrolling, the roll travels toward +z, turning as it goes, and the sheet (12 x 9) stretches out
 * behind it from its top edge, where a curled lip lies once it is open. The top of the sheet's art
 * is at the -z edge, so a scroll laid with +z toward the player reads the right way up to them.
 */
public class ScrollModel extends Model {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "scroll"), "main");

	private static final float SHEET = 9.0F;
	private static final float ROLL_RADIUS = 1.5F;

	private final ModelPart scroll;
	private final ModelPart sheet;
	private final ModelPart lip;
	private final ModelPart roll;
	private final ModelPart tie;

	public ScrollModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.scroll = root.getChild("scroll");
		this.sheet = this.scroll.getChild("sheet");
		this.lip = this.scroll.getChild("lip");
		this.roll = this.scroll.getChild("roll");
		this.tie = this.roll.getChild("tie");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition scroll = mesh.getRoot().addOrReplaceChild("scroll", CubeListBuilder.create(), PartPose.ZERO);
		// texture regions (tools/gen_scroll_textures.py): sheet (0,0), roll (0,10), tie (36,10),
		// seal (48,10), lip (0,17). The sheet lies a hair above the ground, not on it.
		scroll.addOrReplaceChild("sheet", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-6.0F, 0.05F, 0.0F, 12.0F, 0.0F, SHEET), PartPose.ZERO);
		scroll.addOrReplaceChild("lip", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-6.0F, 0.0F, -2.0F, 12.0F, 2.0F, 2.0F), PartPose.ZERO);
		PartDefinition roll = scroll.addOrReplaceChild("roll", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-7.0F, -ROLL_RADIUS, -ROLL_RADIUS, 14.0F, 3.0F, 3.0F),
				PartPose.offset(0.0F, ROLL_RADIUS, 0.0F));
		// a ribbon round the middle of the roll, standing a little proud of it, and the seal on top
		roll.addOrReplaceChild("tie", CubeListBuilder.create()
				.texOffs(36, 10).addBox(-1.0F, -ROLL_RADIUS, -ROLL_RADIUS, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.2F))
				.texOffs(48, 10).addBox(-1.0F, ROLL_RADIUS + 0.2F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 32);
	}

	/** @param openness 0 rolled up, 1 lying open */
	public void setupAnim(float openness) {
		// the top edge stays put while it unrolls; rolled, the roll is centred, open, the sheet
		this.scroll.z = -SHEET / 2.0F * openness;
		this.sheet.visible = openness > 0.0F;
		this.sheet.zScale = openness;
		this.lip.visible = openness > 0.15F;
		this.roll.z = SHEET * openness;
		// rolling along the table rather than sliding: the distance over the radius
		this.roll.xRot = SHEET * openness / ROLL_RADIUS;
		this.tie.visible = openness <= 0.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.scroll.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
