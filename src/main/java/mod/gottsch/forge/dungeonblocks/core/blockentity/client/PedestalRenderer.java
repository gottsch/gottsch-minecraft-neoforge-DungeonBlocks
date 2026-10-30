/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
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
package mod.gottsch.forge.dungeonblocks.core.blockentity.client;

import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.core.blockentity.PedestalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws what stands on a pedestal: the item itself, as a dropped one is drawn - so glint and modded
 * models come for free - hovering over the top, turning slowly and bobbing a little. The motion is
 * a function of the clock alone, so nothing ticks, and each pedestal starts at its own point in
 * the turn so a row of them does not move in step.
 */
public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

	@Override
	public AABB getRenderBoundingBox(PedestalBlockEntity blockEntity) {
		return blockEntity.getRenderBoundingBox();
	}
    /** Degrees a tick: a full turn in about eight seconds. */
    private static final float SPIN = 2.2F;
    private static final float HOVER = 17.5F / 16F;
    private static final float BOB = 0.04F;

    private final ItemRenderer itemRenderer;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        ItemStack stack = pedestal.getItem();
        if (stack.isEmpty() || pedestal.getLevel() == null) {
            return;
        }
        long seed = pedestal.getBlockPos().asLong();
        float time = pedestal.getLevel().getGameTime() + partialTick + (seed & 0xFF);
        pose.pushPose();
        pose.translate(0.5F, HOVER + Mth.sin(time * 0.05F) * BOB, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(time * SPIN));
        pose.scale(1.4F, 1.4F, 1.4F);
        // lit by the air above the pedestal, where it floats, not by the pedestal's own cell
        int above = LevelRenderer.getLightColor(pedestal.getLevel(), pedestal.getBlockPos().above());
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, above, overlay, pose, buffers,
                pedestal.getLevel(), (int) seed);
        pose.popPose();
    }
}
