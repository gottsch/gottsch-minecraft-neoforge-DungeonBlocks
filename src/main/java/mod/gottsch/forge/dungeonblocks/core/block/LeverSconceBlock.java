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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A torch sconce that is a lever: pull the torch down and it powers the wall it hangs on, as a
 * lever does - so it opens a Hidden Door beside it, the oldest secret in the dungeon. Until it is
 * pulled it is the torch sconce to the pixel; pulled, the torch tips out from the wall.
 *
 * <p>The signal is a lever's: 15 to its neighbours, and strong into the block it is fixed to,
 * which then powers what touches that block. Everything else - where it can hang, its flame, its
 * light - is the torch sconce's.
 */
public class LeverSconceBlock extends TorchSconceBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LeverSconceBlock(Properties properties) {
        super(properties);
        // POWERED explicitly: stateDefinition.any() would leave it true
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockState pulled = state.cycle(POWERED);
        level.setBlock(pos, pulled, Block.UPDATE_ALL);
        updateNeighbours(pulled, level, pos);
        boolean on = pulled.getValue(POWERED);
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, on ? 0.6F : 0.5F);
        level.gameEvent(player, on ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        return InteractionResult.CONSUME;
    }

    /** Wake the neighbours, and the wall it hangs on - that block's neighbours read it as powered. */
    private void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    /** Strong power into the wall it is fixed to only, as a lever gives. */
    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
    }

    /**
     * A pulled torch leans 22.5 degrees further out and down (lever_sconce_pulled.json turns the
     * sconce's torch about where its arm meets the plate), so its flame moves with it.
     */
    @Override
    protected double flameHeight(BlockState state) {
        return state.getValue(POWERED) ? 0.24D : super.flameHeight(state);
    }

    @Override
    protected double flameInset(BlockState state) {
        return state.getValue(POWERED) ? -0.025D : super.flameInset(state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!moving && !state.is(newState.getBlock())) {
            if (state.getValue(POWERED)) {
                updateNeighbours(state, level, pos);
            }
            super.onRemove(state, level, pos, newState, moving);
        }
    }
}
