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
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.world.ItemInteractionResult;
import com.mojang.serialization.MapCodec;
import mod.gottsch.forge.dungeonblocks.core.blockentity.WeaponRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A dark-iron weapon rack in the brazier's ironwork, with a slot for each of two weapons. Right-click
 * it holding a sword or an axe and the weapon is racked; right-click it empty-handed and the
 * weapon is taken down, into that hand. The slot nearest where the rack was clicked is used, or
 * the next nearest when that one is taken (full, or empty when taking). Breaking the rack drops
 * what it holds.
 *
 * <p>The weapons live in {@link WeaponRackBlockEntity} and are drawn by WeaponRackRenderer, as
 * real items - enchantment glint, modded swords and all. A sword hangs point-down by its guard
 * from the top rail; an axe stands on its haft with its head above the rail. The block model is
 * the frame alone (blockbench/weapon_rack.bbmodel, authored facing north); the item model shows
 * a sword and an axe racked, since an item has no renderer.
 *
 * <p>FACING is the way its front looks - placed facing the player - and orders the slots: slot
 * 0 is on the left seen from the front.
 */
public class WeaponRackBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
	public static final MapCodec<WeaponRackBlock> CODEC = simpleCodec(WeaponRackBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int SLOTS = 2;
    /**
     * Each slot's centre along the rack, as x in the north-facing model, in blocks. Shared with
     * WeaponRackRenderer, so a click lands on the weapon drawn there.
     */
    public static final float[] SLOT_X = {11F / 16F, 5F / 16F};

    // Facing north or south the rack runs along x; east or west, along z. Collision is the frame
    // alone. The outline also takes in what stands above the top rail once something is racked -
    // swords' hilts and axes' heads - so a click on a hilt reaches the rack, not the wall behind.
    private static final VoxelShape FRAME_ALONG_X = Block.box(1, 0, 2, 15, 11, 14);
    private static final VoxelShape FRAME_ALONG_Z = Block.box(2, 0, 1, 14, 11, 15);
    private static final VoxelShape LOADED_ALONG_X = Shapes.or(FRAME_ALONG_X, Block.box(1, 11, 6, 15, 16, 10));
    private static final VoxelShape LOADED_ALONG_Z = Shapes.or(FRAME_ALONG_Z, Block.box(6, 11, 1, 10, 16, 15));

    public WeaponRackBlock(Properties properties) {
        super(properties);
        // WATERLOGGED set explicitly: stateDefinition.any() would leave it true
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    /** Swords and axes: anything tagged as one, or built on vanilla's sword or axe item. */
    public static boolean canRack(ItemStack stack) {
        return !stack.isEmpty() && (isAxe(stack) || stack.is(ItemTags.SWORDS) || stack.getItem() instanceof SwordItem);
    }

    /** An axe is racked head-up; anything else hangs point-down by its guard, as a sword. */
    public static boolean isAxe(ItemStack stack) {
        return stack.is(ItemTags.AXES) || stack.getItem() instanceof AxeItem;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WeaponRackBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean alongX = state.getValue(FACING).getAxis() == Direction.Axis.Z;
        boolean loaded = level.getBlockEntity(pos) instanceof WeaponRackBlockEntity rack && !rack.isEmpty();
        return alongX ? (loaded ? LOADED_ALONG_X : FRAME_ALONG_X) : (loaded ? LOADED_ALONG_Z : FRAME_ALONG_Z);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? FRAME_ALONG_X : FRAME_ALONG_Z;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, Waterlogging.placedInWater(context));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!canRack(held) || !(level.getBlockEntity(pos) instanceof WeaponRackBlockEntity rack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int slot = nearestSlot(rack, aimedSlot(state, pos, hit.getLocation()), true);
        if (slot < 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            rack.setItem(slot, held.split(1));
            // creative keeps its weapon, as it keeps a book put in a chiseled bookshelf
            if (player.isCreative()) {
                held.grow(1);
            }
            level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Taking a weapon down: main hand only, and empty, so the off hand never takes one. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !(level.getBlockEntity(pos) instanceof WeaponRackBlockEntity rack)) {
            return InteractionResult.PASS;
        }
        int slot = nearestSlot(rack, aimedSlot(state, pos, hit.getLocation()), false);
        if (slot < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.setItemInHand(InteractionHand.MAIN_HAND, rack.getItem(slot));
            rack.setItem(slot, ItemStack.EMPTY);
            level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_CHAIN.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * The slot nearest the click, measured along the rack as x in the north-facing model - the
     * inverse of the turn the blockstate gives the model.
     */
    private static int aimedSlot(BlockState state, BlockPos pos, Vec3 hit) {
        double x = hit.x - pos.getX();
        double z = hit.z - pos.getZ();
        double along = switch (state.getValue(FACING)) {
            case SOUTH -> 1 - x;
            case EAST -> z;
            case WEST -> 1 - z;
            default -> x;
        };
        int aimed = 0;
        for (int slot = 1; slot < SLOTS; slot++) {
            if (Math.abs(along - SLOT_X[slot]) < Math.abs(along - SLOT_X[aimed])) {
                aimed = slot;
            }
        }
        return aimed;
    }

    /** The slot nearest `aimed` that is empty (or, for `empty` false, holds a weapon); -1 if none. */
    private static int nearestSlot(WeaponRackBlockEntity rack, int aimed, boolean empty) {
        int best = -1;
        for (int slot = 0; slot < SLOTS; slot++) {
            if (rack.getItem(slot).isEmpty() == empty && (best < 0
                    || Math.abs(SLOT_X[slot] - SLOT_X[aimed]) < Math.abs(SLOT_X[best] - SLOT_X[aimed]))) {
                best = slot;
            }
        }
        return best;
    }

    /** Breaking the rack drops what it holds, as a campfire drops what it is cooking. */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof WeaponRackBlockEntity rack) {
                Containers.dropContents(level, pos, rack.getItems());
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        Waterlogging.tickWater(state, level, pos);
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
