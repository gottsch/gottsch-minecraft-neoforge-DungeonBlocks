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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A dark-iron firewood rack - the brazier's fuel store, in its ironwork: two U-shaped hoops on
 * little feet, their posts flaring outward at the top like the brazier's wings.
 *
 * <p>It fills in stages. A player places it empty; each log or planks used on it is stacked as
 * another pair of logs, until it is full at {@link #FULL}. The wood is not given back: like a
 * composter's contents, it is not stored as items, and breaking the rack drops the rack alone.
 *
 * <p>The logs run front to back, so the rack shows their cut ends. FACING is the way that front
 * looks, and it is placed facing the player - though front and back are alike. The models are
 * built from blockbench/firewood_rack.bbmodel, authored facing north, one per FIREWOOD value.
 *
 * <p>Not flammable, although it is full of wood: the frame is iron, and a dungeon's racks should
 * not burn away beside its braziers and lava.
 */
public class FirewoodRackBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<FirewoodRackBlock> CODEC = simpleCodec(FirewoodRackBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int FULL = 4;
    /**
     * How much wood is stacked: 0 is an empty rack, and each step adds a pair of logs, to FULL.
     * The default is FULL, so a rack a structure places without naming the property - and every
     * rack saved before the property existed - is a stocked one. A player's placed rack starts
     * empty (see getStateForPlacement).
     */
    public static final IntegerProperty FIREWOOD = IntegerProperty.create("firewood", 0, FULL);

    /** Facing north or south, the logs run along z; east or west, along x. */
    private static final VoxelShape ALONG_Z = Block.box(1, 0, 0, 15, 14, 16);
    private static final VoxelShape ALONG_X = Block.box(0, 0, 1, 16, 14, 15);

    public FirewoodRackBlock(Properties properties) {
        super(properties);
        // WATERLOGGED set explicitly: stateDefinition.any() would leave it true
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(FIREWOOD, FULL).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FIREWOOD, WATERLOGGED);
    }

    /** Stack another pair of logs: any log or planks will do, one item per stage. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = stack;
        int firewood = state.getValue(FIREWOOD);
        // a full rack passes, so a log can still be placed against it
        if (firewood >= FULL || !(held.is(ItemTags.LOGS) || held.is(ItemTags.PLANKS))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(FIREWOOD, firewood + 1), Block.UPDATE_ALL);
            player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? ALONG_Z : ALONG_X;
    }

    /** Placed empty, to be stocked a log or planks at a time. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(FIREWOOD, 0)
                .setValue(WATERLOGGED, Waterlogging.placedInWater(context));
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
