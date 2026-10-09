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
 * Geometry exported from Blockbench ({@code BigRedPotion.bbmodel} / {@code BigRedPotion.java}): an
 * 8x10x8 bottle body with a 6x5x6 liquid band around it, a 4x3x4 neck and a 2x2x2 cork, for a total
 * modeled height of 14px (0.875 blocks) against {@code textures/entity/big_red_potion.png}.
 *
 * <p>Same modeled extents as {@link PotModel}, but it is rendered at half scale — see the
 * {@code scale} on its {@code PotVariant} — so in the world it stands 7px tall.
 *
 * @author Mark Gottschling on Aug 10, 2026
 */
public class BigRedPotionModel extends EntityModel<PotEntity> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "big_red_potion"), "main");

	private final ModelPart bbMain;

	public BigRedPotionModel(ModelPart root) {
		this.bbMain = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, CubeDeformation.NONE)
						// neck dropped half a pixel from the exported -13.0: as exported, its bottom face
						// sat exactly on the body's top face (both at y=-10) and the two coplanar quads
						// z-fought. Barely visible upright, where that plane is edge-on, but a tipped
						// potion turns it to face the camera. Half a pixel buries the neck's bottom
						// inside the body; the box KEEPS ITS 4x3x4 SIZE so the UV mapping is untouched.
						//
						// Note the sign: this axis is INVERTED relative to Blockbench, so the same edit
						// is y 10 -> 9.5 in the .bbmodel but -13.0 -> -12.5 here. Going to -13.5 lifts
						// the neck off the body and leaves a visible gap.
						.texOffs(21, 19).addBox(-2.0F, -12.5F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE)
						.texOffs(0, 27).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE)
						.texOffs(0, 0).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
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
