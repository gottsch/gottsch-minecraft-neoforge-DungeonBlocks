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
 * Geometry exported from Blockbench ({@code RedFlask.bbmodel} / {@code RedFlask.java}): a squat
 * 7x9x6 flask body with a 5x4x4 liquid band, a 4x3x3 neck and a 2x2x2 cork, for a total modeled
 * height of 13px (0.8125 blocks) against {@code textures/entity/red_flask.png}.
 *
 * <p>Rendered at half scale like {@link BigRedPotionModel} — see the {@code scale} on its
 * {@code PotVariant} — so in the world it stands about 6.5px tall.
 *
 * <p>Unlike the potion, nothing here needed nudging for z-fighting: the neck already starts inside
 * the body and the cork inside the neck, so no two faces share a plane.
 *
 * @author Mark Gottschling on Aug 11, 2026
 */
public class RedFlaskModel extends EntityModel<PotEntity> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "red_flask"), "main");

	private final ModelPart bbMain;

	public RedFlaskModel(ModelPart root) {
		this.bbMain = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-3.5F, -9.0F, -3.0F, 7.0F, 9.0F, 6.0F, CubeDeformation.NONE)
						.texOffs(21, 16).addBox(-2.0F, -11.5F, -1.5F, 4.0F, 3.0F, 3.0F, CubeDeformation.NONE)
						.texOffs(1, 16).addBox(-2.5F, -5.0F, -2.0F, 5.0F, 4.0F, 4.0F, CubeDeformation.NONE)
						.texOffs(21, 23).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
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
