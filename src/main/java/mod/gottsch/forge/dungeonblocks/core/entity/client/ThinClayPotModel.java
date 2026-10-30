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
import mod.gottsch.forge.dungeonblocks.core.entity.PotEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Geometry exported from Blockbench ({@code ThinClayPot.bbmodel} / {@code ThinClayPot.java}) — the
 * narrow, tapered member of the pot family: a 6x8x6 body with a 3x3x3 neck and a 4x1x4 lip, 12px
 * (0.75 blocks) tall overall.
 *
 * <p>Note this one is mapped against a <b>32x32</b> sheet, not the 64x64 the other two pots use, so
 * {@code LayerDefinition.create} is given 32/32 here. Getting that wrong silently skews every UV.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
public class ThinClayPotModel extends EntityModel<PotEntity> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "thin_clay_pot"), "main");

	private final ModelPart bbMain;

	public ThinClayPotModel(ModelPart root) {
		this.bbMain = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-3.0F, -8.0F, -2.0F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE)
						.texOffs(16, 14).addBox(-1.5F, -11.0F, -0.5F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE)
						.texOffs(0, 14).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE),
				// z -1 corrects the export: all three boxes above are centred on z=+1 rather than 0
				// (body z -2..4, neck -0.5..2.5, lip -1..3), so without this the pot renders a pixel
				// out of its own hitbox and leans off the block centre. Fix it in ThinClayPot.bbmodel
				// to drop this, otherwise a re-export reintroduces it.
				PartPose.offset(0.0F, 24.0F, -1.0F));

		return LayerDefinition.create(mesh, 32, 32);
	}

	@Override
	public void setupAnim(PotEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// no animation — decorative prop, not a mob
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
