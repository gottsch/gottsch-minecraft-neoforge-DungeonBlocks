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
import mod.gottsch.forge.dungeonblocks.core.entity.PotEntity;
import mod.gottsch.forge.dungeonblocks.core.item.PotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the pot props in the inventory (and in hand, on the ground, in item frames) as real 3D
 * geometry, the way a block item renders, instead of as a flat sprite.
 *
 * <p>How this hooks up: the pot item models are declared with {@code "parent": "builtin/entity"},
 * which bakes to a vanilla {@code BuiltInModel} whose {@code isCustomRenderer()} is {@code true}.
 * That makes {@code ItemRenderer} skip quad rendering and call
 * {@code IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(...)} instead — see
 * {@link mod.gottsch.forge.dungeonblocks.core.setup.ClientSetup#registerClientExtensions}. The per-context placement (the 30/225 GUI tilt, hand and ground
 * transforms) comes from the {@code display} block those models copy from vanilla
 * {@code block/block.json}, so the pots sit exactly where a block item would.
 *
 * <p>One shared instance serves every pot: the shape is looked up from the stack's {@link PotItem},
 * so adding a pot needs no change here — only a new {@link PotVariant} in {@code ClientSetup}.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
@OnlyIn(Dist.CLIENT)
public class PotItemRenderer extends BlockEntityWithoutLevelRenderer {

	/**
	 * The tallest pot is 0.875 blocks, so scaling by ~1.08 makes it fill its slot like a full block
	 * would. It is applied uniformly rather than per-shape so the pots stay proportional to each
	 * other — a squat pot should still look shorter than a tall one in the inventory.
	 */
	private static final float INVENTORY_SCALE = 1.08F;

	private static PotItemRenderer instance;

	private final Map<EntityType<?>, PotVariant> variants = new HashMap<>();
	private final Map<EntityType<?>, EntityModel<PotEntity>> baked = new HashMap<>();

	private PotItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
				Minecraft.getInstance().getEntityModels());
	}

	/**
	 * Lazily created: the client extensions are registered during mod loading, long
	 * before the model set is usable, so nothing here may be touched until the first actual render.
	 */
	public static PotItemRenderer getInstance() {
		if (instance == null) {
			instance = new PotItemRenderer();
		}
		return instance;
	}

	public static void setVariants(List<PotVariant> variants) {
		PotItemRenderer renderer = getInstance();
		renderer.variants.clear();
		renderer.baked.clear();
		variants.forEach(variant -> renderer.variants.put(variant.entityType().get(), variant));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!(stack.getItem() instanceof PotItem potItem)) {
			return;
		}
		EntityType<PotEntity> type = potItem.getEntityType();
		PotVariant variant = this.variants.get(type);
		if (variant == null) {
			return;
		}

		// baked on first use, not at construction — see getInstance()
		EntityModel<PotEntity> model = this.baked.computeIfAbsent(type, t -> variant.modelFactory()
				.apply(Minecraft.getInstance().getEntityModels().bakeLayer(variant.layer())));

		poseStack.pushPose();
		// ItemRenderer has already applied this context's display transform and shifted the origin
		// to the corner of the item's unit cube, so start by moving to its centre.
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.scale(INVENTORY_SCALE, INVENTORY_SCALE, INVENTORY_SCALE);
		// drop to the bottom of the pot's own height so it ends up vertically centred in the slot
		poseStack.translate(0.0D, -variant.halfHeight(), 0.0D);
		// the standard entity-model transform: mirror, then lift the 24px root pivot to the floor.
		// After this the geometry stands upward from the current origin (same as PotRenderer).
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0D, -1.501D, 0.0D);

		VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(variant.texture()));
		model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay,
				0xFFFFFFFF);
		poseStack.popPose();
	}
}
