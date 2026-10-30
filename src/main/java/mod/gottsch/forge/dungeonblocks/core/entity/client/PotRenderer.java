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
import mod.gottsch.forge.dungeonblocks.core.entity.PotEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared renderer for every pot-shaped prop: the tumble pivot and the
 * Blockbench root-pivot transform are identical across shapes, only the model,
 * texture and modeled height differ — so each registered pot {@code EntityType}
 * supplies those three and reuses this class (see {@code ClientSetup}).
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotRenderer extends EntityRenderer<PotEntity> {

	/**
	 * Render-only lift off the floor, in blocks. A prop rests with its outermost wall exactly on the
	 * surface it stands on — upright it is the base of the model, tipped over it is whichever side
	 * the tumble pivot parks on the ground — and that wall then sits in the same plane as the
	 * supporting block's top face, with nothing for the depth buffer to break the tie on. On an
	 * opaque pot the touching wall is hidden anyway, but the potion's glass is cutout, so you see
	 * straight through it to the inner surface of that wall and the two flicker against each other.
	 *
	 * <p>Two thousandths of a block clears it at any view distance and is about a thirtieth of a
	 * texture pixel — far below what any camera angle can show. Purely visual: collision, physics and
	 * the entity's real position are untouched.
	 */
	private static final double FLOOR_CLEARANCE = 0.002D;

	private final EntityModel<PotEntity> model;
	private final ResourceLocation texture;
	/**
	 * Height of the pivot the tumble rotates about — half the pot's <em>width</em>, so a fully
	 * tipped pot comes to rest with its body on the floor. See {@link PotVariant#tumblePivot()}.
	 */
	private final double tumblePivot;
	/** Uniform world render scale — see {@link PotVariant#scale()}. */
	private final float scale;

	public PotRenderer(EntityRendererProvider.Context context, EntityModel<PotEntity> model,
			ResourceLocation texture, double tumblePivot) {
		this(context, model, texture, tumblePivot, 1.0F);
	}

	public PotRenderer(EntityRendererProvider.Context context, EntityModel<PotEntity> model,
			ResourceLocation texture, double tumblePivot, float scale) {
		super(context);
		this.model = model;
		this.texture = texture;
		this.tumblePivot = tumblePivot;
		this.scale = scale;
	}

	@Override
	public ResourceLocation getTextureLocation(PotEntity entity) {
		return this.texture;
	}

	@Override
	public void render(PotEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		// before everything else, so it lifts the prop the same amount whichever way it is lying
		poseStack.translate(0.0D, FLOOR_CLEARANCE, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

		float tumbleProgress = entity.getTumbleProgress(partialTicks);
		if (tumbleProgress > 0.0F) {
			// the pivot is a world-space height, so it takes the render scale too — otherwise a
			// shrunken prop would tip about a point well above itself and swing into the air.
			double pivot = this.tumblePivot * this.scale;
			float tipSign = (entity.getId() % 2 == 0) ? 1.0F : -1.0F;
			poseStack.translate(0.0D, pivot, 0.0D);
			poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F * tipSign * tumbleProgress));
			poseStack.translate(0.0D, -pivot, 0.0D);
		}

		// applied before the root transform below so the 24px pivot offset scales with the geometry
		// and the prop still stands on the entity's feet rather than floating or sinking.
		if (this.scale != 1.0F) {
			poseStack.scale(this.scale, this.scale, this.scale);
		}

		// mirror + drop to match the Blockbench-exported PartPose.offset(0, 24, 0) root pivot
		// convention (the same transform LivingEntityRenderer applies for vanilla mob models).
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0D, -1.501D, 0.0D);

		RenderType renderType = this.model.renderType(this.getTextureLocation(entity));
		VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
				0xFFFFFFFF);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
