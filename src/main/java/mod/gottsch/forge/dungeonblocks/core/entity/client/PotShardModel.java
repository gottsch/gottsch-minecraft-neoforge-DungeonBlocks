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
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Ceramic-chip models for {@link mod.gottsch.forge.dungeonblocks.core.entity.PotShardEntity} —
 * small, stubby chunks (unlike bone splinters, these shouldn't read as long
 * pieces). One baked layer per shape variant; the shard's synced variant picks
 * one, the renderer applies the tumble.
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotShardModel extends EntityModel<Entity> {

	/** One baked layer per shard shape variant (order matches PotShardEntity variant indices). */
	public static final ModelLayerLocation[] LAYERS = {
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "pot_shard_0"), "main"),
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "pot_shard_1"), "main"),
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "pot_shard_2"), "main"),
	};

	// per-variant {width, height, length} in pixels (16px = 1 block) — small, chunky chips
	private static final float[][] DIMS = {
			{2.0F, 1.5F, 2.0F},
			{2.5F, 1.0F, 3.0F},
			{1.5F, 1.5F, 2.5F},
	};

	private final ModelPart root;

	public PotShardModel(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static LayerDefinition createBodyLayer(int variant) {
		float w = DIMS[variant][0];
		float h = DIMS[variant][1];
		float d = DIMS[variant][2];
		MeshDefinition mesh = new MeshDefinition();
		mesh.getRoot().addOrReplaceChild("root",
				CubeListBuilder.create().texOffs(0, 0).addBox(-w / 2.0F, -h / 2.0F, -d / 2.0F, w, h, d),
				PartPose.ZERO);
		return LayerDefinition.create(mesh, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		// static geometry — the renderer applies the tumble
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
