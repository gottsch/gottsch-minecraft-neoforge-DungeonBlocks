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
import mod.gottsch.forge.dungeonblocks.core.entity.BucketEntity;
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
 * A wooden tub bucket in six boxes, against {@code textures/entity/wooden_bucket.png}
 * (tools/gen_wooden_bucket_texture.py): a 10x4x8 foot, four 1-thick stave walls rising 6px from it
 * to enclose 12x10, and the water. The walls sit outside the foot all round, which is the step at
 * the lower hoop; 10px tall in all.
 *
 * <p>The water is its own part, a 10x4x8 box on the foot whose top lies 2px below the rim, so
 * {@link #setupAnim} can hide it whenever the bucket is empty - emptied by hand, or tipped over.
 * The inventory renderer never calls {@code setupAnim}, so the item always shows it full.
 *
 * @author Mark Gottschling on 10/25/2026
 */
public class WoodenBucketModel extends EntityModel<PotEntity> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "wooden_bucket"), "main");

	private final ModelPart bbMain;
	private final ModelPart water;

	public WoodenBucketModel(ModelPart root) {
		this.bbMain = root.getChild("bb_main");
		this.water = root.getChild("water");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();

		parts.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						// foot
						.texOffs(0, 0).addBox(-5.0F, -4.0F, -4.0F, 10.0F, 4.0F, 8.0F, CubeDeformation.NONE)
						// north and south walls, the full 12 across
						.texOffs(0, 12).addBox(-6.0F, -10.0F, -5.0F, 12.0F, 6.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(0, 19).addBox(-6.0F, -10.0F, 4.0F, 12.0F, 6.0F, 1.0F, CubeDeformation.NONE)
						// west and east walls, between them
						.texOffs(36, 0).addBox(-6.0F, -10.0F, -4.0F, 1.0F, 6.0F, 8.0F, CubeDeformation.NONE)
						.texOffs(36, 14).addBox(5.0F, -10.0F, -4.0F, 1.0F, 6.0F, 8.0F, CubeDeformation.NONE),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		parts.addOrReplaceChild("water",
				CubeListBuilder.create()
						.texOffs(0, 28).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 4.0F, 8.0F, CubeDeformation.NONE),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	/** Tipping empties it at once, so the water goes as soon as it starts over, not when it lands. */
	@Override
	public void setupAnim(PotEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		this.water.visible = entity instanceof BucketEntity bucket ? bucket.isFilled() : !entity.isTumbled();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			int color) {
		this.bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		this.water.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
