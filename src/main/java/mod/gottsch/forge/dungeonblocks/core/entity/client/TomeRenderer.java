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
import mod.gottsch.forge.dungeonblocks.core.entity.TomeEntity;
import mod.gottsch.forge.dungeonblocks.core.entity.TomeVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;

/**
 * Draws a {@link TomeModel} at 32 texels to the block - the covers are 16x16 icons' worth of
 * detail, at the tapestries' density - with its foot toward whoever laid it down.
 */
public class TomeRenderer extends EntityRenderer<TomeEntity> {
	/** Texels to model units (16 to the block) to the block: 32 texels a block. */
	private static final float SCALE = 0.5F;
	/** Lifts the back cover off the surface it lies on, which it would otherwise fight. */
	private static final double FLOOR_CLEARANCE = 0.002D;
	private static final Map<TomeVariant, ResourceLocation> TEXTURES = new EnumMap<>(TomeVariant.class);

	static {
		for (TomeVariant variant : TomeVariant.values()) {
			TEXTURES.put(variant, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "textures/entity/tome/" + variant.id() + ".png"));
		}
	}

	private final Map<TomeVariant.Shape, TomeModel> models = new EnumMap<>(TomeVariant.Shape.class);

	public TomeRenderer(EntityRendererProvider.Context context) {
		super(context);
		for (TomeVariant.Shape shape : TomeVariant.Shape.values()) {
			this.models.put(shape, new TomeModel(context.bakeLayer(TomeModel.layer(shape)), shape));
		}
		this.shadowRadius = 0.2F;
	}

	@Override
	public ResourceLocation getTextureLocation(TomeEntity entity) {
		return TEXTURES.get(entity.getVariant());
	}

	@Override
	public void render(TomeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0.0D, FLOOR_CLEARANCE, 0.0D);
		// the model's foot is +z; turned to face back along the placer's look, it faces the placer
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
		poseStack.scale(SCALE, SCALE, SCALE);
		TomeVariant.Shape shape = entity.getVariant().shape();
		TomeModel model = this.models.get(shape);
		if (entity.isUpright()) {
			// read from the bottom up: stood on its foot (+z down), lifted half its height and centred
			// on its thickness, then turned so the spine (-x) faces the placer (+z). In model units,
			// 16 to a 1/16-block pose step - one texel each, at this scale.
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			poseStack.translate(0.0D, shape.height / 2.0D / 16.0D, -shape.thickness() / 2.0D / 16.0D);
			poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			model.setupAnim(0.0F, -1.0F, -1.0F);
		} else {
			model.setupAnim(entity.getOpenness(partialTicks),
					entity.getPageTurn(0, partialTicks), entity.getPageTurn(1, partialTicks));
		}
		model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(this.getTextureLocation(entity))),
				packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
