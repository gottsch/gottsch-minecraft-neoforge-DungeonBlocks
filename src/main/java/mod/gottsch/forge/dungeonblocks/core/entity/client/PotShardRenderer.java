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
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.PotMaterial;
import mod.gottsch.forge.dungeonblocks.core.entity.PotShardEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Renders shard debris as a small ceramic chip tumbling through the air, its
 * spin freezing once it settles onto a surface (same idiom as {@code BoneShard}
 * in GMM). Picks the model matching the shard's shape variant.
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotShardRenderer extends EntityRenderer<PotShardEntity> {

	// one texture per pot material, indexed by ordinal — a new pot colour is a PotMaterial constant
	// and its shard PNG, nothing here.
	private static final ResourceLocation[] TEXTURES = buildTextures();

	private final PotShardModel[] models;

	public PotShardRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.models = new PotShardModel[PotShardModel.LAYERS.length];
		for (int i = 0; i < this.models.length; i++) {
			this.models[i] = new PotShardModel(context.bakeLayer(PotShardModel.LAYERS[i]));
		}
	}

	private static ResourceLocation[] buildTextures() {
		PotMaterial[] materials = PotMaterial.values();
		ResourceLocation[] textures = new ResourceLocation[materials.length];
		for (int i = 0; i < materials.length; i++) {
			textures[i] = ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID,
					"textures/entity/" + materials[i].getShardTexture() + ".png");
		}
		return textures;
	}

	@Override
	public ResourceLocation getTextureLocation(PotShardEntity entity) {
		return TEXTURES[entity.getMaterial().ordinal()];
	}

	@Override
	public void render(PotShardEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		// spinTicks stops advancing once the shard settles, and dropping partialTicks at that point
		// avoids a last sub-tick nudge — so the tumble freezes cleanly instead of creeping.
		float spin = entity.getSpinTicks() + (entity.isLanded() ? 0.0F : partialTicks);
		poseStack.mulPose(Axis.XP.rotationDegrees(spin * 27.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(spin * 19.0F));

		PotShardModel model = this.models[Math.floorMod(entity.getVariant(), this.models.length)];
		VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(this.getTextureLocation(entity)));
		model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
				0xFFFFFFFF);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
