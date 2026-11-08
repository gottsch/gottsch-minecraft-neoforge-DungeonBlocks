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

import mod.gottsch.forge.dungeonblocks.core.blockentity.TombDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A tomb door: a two-block carved stone slab that grinds sideways into the wall to open, by hand or
 * by redstone. It stands on the wall's middle plane, 4px thick; open, it slides 14px toward its
 * {@link #slideDirection}, into the wall beside the doorway, leaving the way clear.
 *
 * <p>Two halves placed and broken as the iron maiden's are (LOWER where clicked, UPPER above, OPEN
 * on both, only LOWER drops). The slab is never baked into the chunk: the LOWER half's block entity
 * is drawn by {@code TombDoorRenderer} whether shut, open or moving (a slide split between the baked
 * mesh and a renderer flickers at the hand-offs, as the sarcophagus showed), so the blockstate
 * model is empty. Models and textures: tools/gen_tomb_door.py.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class TombDoorBlock extends IronMaidenBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    /** How long the slide takes, in ticks. */
    public static final int MOVE_TICKS = 24;
    /** How far the slab slides when open, in blocks: all but a 2px lip, which stays in the doorway. */
    public static final float OPEN_SLIDE = 14F / 16F;

    private static final VoxelShape SHUT_X = Block.box(6, 0, 0, 10, 16, 16);
    private static final VoxelShape SHUT_Z = Block.box(0, 0, 6, 16, 16, 10);

    public TombDoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    /** The way the slab slides to open: to its right, looking at its front. */
    public static Direction slideDirection(Direction facing) {
        return facing.getClockWise();
    }

    /** Shut: the slab across the doorway. Open: the 2px lip left at the slide side's edge. */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (!state.getValue(OPEN)) {
            return facing.getAxis() == Direction.Axis.Z ? SHUT_Z : SHUT_X;
        }
        return switch (slideDirection(facing)) {
            case EAST -> Block.box(14, 0, 6, 16, 16, 10);
            case WEST -> Block.box(0, 0, 6, 2, 16, 10);
            case SOUTH -> Block.box(6, 0, 14, 10, 16, 16);
            default -> Block.box(6, 0, 0, 10, 16, 2);
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(OPEN);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        return state.setValue(POWERED, powered).setValue(OPEN, powered);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            setOpen(level, pos, state, !state.getValue(OPEN));
            level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** A redstone signal at either half opens it; losing it shuts it, as a door does. */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(other);
        if (powered == state.getValue(POWERED)) {
            return;
        }
        state = state.setValue(POWERED, powered);
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        BlockState otherState = level.getBlockState(other);
        if (otherState.is(this)) {
            level.setBlock(other, otherState.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        }
        if (powered != state.getValue(OPEN)) {
            setOpen(level, pos, state, powered);
        }
    }

    /** Sets OPEN on both halves and grinds. */
    private void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        BlockState otherState = level.getBlockState(other);
        if (otherState.is(this)) {
            level.setBlock(other, otherState.setValue(OPEN, open), Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        }
        playToggleSound(level, pos, open);
    }

    /** Stone grinding on stone, low and slow. */
    @Override
    protected void playToggleSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.8F, open ? 0.5F : 0.45F);
    }

    /** Only the LOWER half carries the block entity, and so the slab. */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new TombDoorBlockEntity(pos, state) : null;
    }
}
