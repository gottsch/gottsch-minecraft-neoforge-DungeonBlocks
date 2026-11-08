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
 * A lidded urn, as opposed to the open pots: a narrow foot, a body that widens to its shoulder, then
 * a neck, lid and knob, 12px (0.75 blocks) tall and 8px wide overall. Every box is an even width, so
 * they all stay on whole pixels around the centre.
 *
 * <p>The UV layout is shared with {@code tools/gen_cinerary_urn_textures.py}, which paints the
 * texture box by box: move a {@code texOffs} here and it has to move there too.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class CineraryUrnModel extends EntityModel<PotEntity> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "cinerary_urn"), "main");

	private final ModelPart bbMain;

	public CineraryUrnModel(ModelPart root) {
		this.bbMain = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						.texOffs(24, 13).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE)
						.texOffs(0, 13).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 3.0F, 6.0F, CubeDeformation.NONE)
						.texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 5.0F, 8.0F, CubeDeformation.NONE)
						.texOffs(40, 13).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE)
						.texOffs(0, 22).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 1.0F, 6.0F, CubeDeformation.NONE)
						.texOffs(24, 22).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE),
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
