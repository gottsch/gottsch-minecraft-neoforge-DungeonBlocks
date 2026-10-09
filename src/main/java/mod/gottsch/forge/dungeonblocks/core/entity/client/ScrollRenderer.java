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
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.ScrollEntity;
import mod.gottsch.forge.dungeonblocks.core.entity.ScrollVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;

/** Draws a {@link ScrollModel} at 32 texels to the block, as the tomes are drawn, unrolling toward whoever laid it. */
public class ScrollRenderer extends EntityRenderer<ScrollEntity> {
	private static final float SCALE = 0.5F;
	/** Lifts it off the surface it lies on, which it would otherwise fight. */
	private static final double FLOOR_CLEARANCE = 0.002D;
	private static final Map<ScrollVariant, ResourceLocation> TEXTURES = new EnumMap<>(ScrollVariant.class);

	static {
		for (ScrollVariant variant : ScrollVariant.values()) {
			TEXTURES.put(variant, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "textures/entity/scroll/" + variant.id() + ".png"));
		}
	}

	private final ScrollModel model;

	public ScrollRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new ScrollModel(context.bakeLayer(ScrollModel.LAYER_LOCATION));
		this.shadowRadius = 0.15F;
	}

	@Override
	public ResourceLocation getTextureLocation(ScrollEntity entity) {
		return TEXTURES.get(entity.getVariant());
	}

	@Override
	public void render(ScrollEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0.0D, FLOOR_CLEARANCE, 0.0D);
		// the model unrolls toward +z; turned back along the placer's look, it unrolls toward them
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
		poseStack.scale(SCALE, SCALE, SCALE);
		this.model.setupAnim(entity.getOpenness(partialTicks));
		this.model.renderToBuffer(poseStack, buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))),
				packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
