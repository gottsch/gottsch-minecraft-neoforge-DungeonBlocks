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
package mod.gottsch.forge.dungeonblocks.core.item;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ChainSize;
import mod.gottsch.forge.dungeonblocks.core.block.ChainAnchorBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.ChainAnchorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * A giant or medium chain, before it is hung. Use it on one chain anchor, then on a second, and a chain
 * runs between their centres; one item makes one chain of any length up to
 * {@link ChainAnchorBlock#MAX_LENGTH}. Using it on the first anchor again lets go of it.
 *
 * <p>The first anchor is kept on the stack, with its dimension, and the stack glints while it is
 * held. An anchor can start only one chain, but any number may end at it.
 *
 * <p>One class for every {@link ChainSize}: the Giant Chain and the Medium Chain are two items of it,
 * and hang chains of their own size on the same anchors.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class ChainItem extends Item {
    private static final String ANCHOR = "Anchor";
    private static final String DIMENSION = "AnchorDimension";

    private final ChainSize size;

    public ChainItem(Properties properties, ChainSize size) {
        super(properties);
        this.size = size;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof ChainAnchorBlockEntity end)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String dimension = level.dimension().location().toString();

        if (!tag.contains(ANCHOR, Tag.TAG_LONG) || !dimension.equals(tag.getString(DIMENSION))) {
            if (end.getTarget() != null) {
                tell(player, "already_chained");
                return InteractionResult.CONSUME;
            }
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.putLong(ANCHOR, pos.asLong());
                t.putString(DIMENSION, dimension);
            });
            tell(player, "started");
            return InteractionResult.CONSUME;
        }

        BlockPos start = BlockPos.of(tag.getLong(ANCHOR));
        if (start.equals(pos)) {
            letGo(stack);
            tell(player, "let_go");
            return InteractionResult.CONSUME;
        }
        if (!level.isLoaded(start) || !(level.getBlockEntity(start) instanceof ChainAnchorBlockEntity anchor)
                || anchor.getTarget() != null) {
            letGo(stack);
            tell(player, "lost");
            return InteractionResult.CONSUME;
        }
        if (start.getCenter().distanceTo(pos.getCenter()) > ChainAnchorBlock.MAX_LENGTH) {
            tell(player, "too_far");
            return InteractionResult.CONSUME;
        }
        if (start.equals(end.getTarget())) {
            tell(player, "already_joined");
            return InteractionResult.CONSUME;
        }

        anchor.setTarget(pos, size);
        end.addSource(start, size);
        letGo(stack);
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 0.6F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return InteractionResult.CONSUME;
    }

    private static void letGo(ItemStack stack) {
        // an emptied custom data is removed from the stack, so a let-go chain stacks with fresh ones
        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.remove(ANCHOR);
            t.remove(DIMENSION);
        });
    }

    private static void tell(Player player, String message) {
        if (player != null) {
            player.displayClientMessage(Component.translatable("message." + DungeonBlocks.MOD_ID + ".chain." + message), true);
        }
    }

    /** Glints while it holds a first anchor. */
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(ANCHOR);
    }
}
