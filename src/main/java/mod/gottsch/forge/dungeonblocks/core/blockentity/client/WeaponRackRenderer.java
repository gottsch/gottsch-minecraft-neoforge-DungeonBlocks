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
import mod.gottsch.forge.dungeonblocks.core.block.WeaponRackBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.WeaponRackBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the weapons on a weapon rack, as items - the way an item frame draws its item, so
 * enchantment glint and modded models come for free. The rack's frame is the block's own baked
 * model; only what hangs in it is drawn here.
 *
 * <p>Each weapon is drawn in the FIXED display context (a flat sprite facing the rack's front),
 * turned within the rack's plane so the sprite's diagonal stands upright, then turned with the
 * rack the way the blockstate turns the north-facing frame.
 */
public class WeaponRackRenderer implements BlockEntityRenderer<WeaponRackBlockEntity> {

	@Override
	public AABB getRenderBoundingBox(WeaponRackBlockEntity blockEntity) {
		return blockEntity.getRenderBoundingBox();
	}
    /** Each slot's depth in the north-facing model: staggered, so two weapons never share a plane. */
    private static final float[] SLOT_Z = {7.6F / 16F, 8.4F / 16F};

    /**
     * How a kind of weapon hangs: its turn in the rack's plane (positive is clockwise, seen from
     * the front), its scale, where its centre sits, and a sideways shift in blocks. Vanilla's
     * sprites run diagonally from the grip, bottom left, to the point or head, top right; the
     * measurements below are texels along that diagonal from the sprite's centre.
     */
    private record Hang(float angle, float scale, float y, float shift) {}

    /** Point-down, with its guard's lower edge (1.3 texels from centre) on the top rail at 10px. */
    private static final Hang SWORD = new Hang(135F, 0.66F, (10F - 1.3F * 0.66F) / 16F, 0F);
    /**
     * Head-up, with the head's lower edge (at the centre) on the top rail and the haft's end on
     * the tray. The haft runs 0.7 texels off the sprite's centre line, so it is shifted back over
     * the slot.
     */
    private static final Hang AXE = new Hang(-45F, 0.85F, 10F / 16F, 0.7F * 0.85F / 16F);

    private final ItemRenderer itemRenderer;

    public WeaponRackRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(WeaponRackBlockEntity rack, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        BlockState state = rack.getBlockState();
        if (!(state.getBlock() instanceof WeaponRackBlock)) {
            return;
        }
        Direction facing = state.getValue(WeaponRackBlock.FACING);
        int seed = (int) rack.getBlockPos().asLong();
        for (int slot = 0; slot < WeaponRackBlock.SLOTS; slot++) {
            ItemStack stack = rack.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Hang hang = WeaponRackBlock.isAxe(stack) ? AXE : SWORD;
            pose.pushPose();
            // turn about the block's centre the way the blockstate turns the north-facing frame
            // (y = yaw + 180, applied as a negative rotation, as vanilla's BlockModelRotation does)
            pose.translate(0.5F, 0F, 0.5F);
            pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180F) % 360F)));
            pose.translate(-0.5F, 0F, -0.5F);
            pose.translate(WeaponRackBlock.SLOT_X[slot] + hang.shift(), hang.y(), SLOT_Z[slot]);
            pose.mulPose(Axis.ZP.rotationDegrees(hang.angle()));
            pose.scale(hang.scale(), hang.scale(), hang.scale());
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffers,
                    rack.getLevel(), seed + slot);
            pose.popPose();
        }
    }
}
