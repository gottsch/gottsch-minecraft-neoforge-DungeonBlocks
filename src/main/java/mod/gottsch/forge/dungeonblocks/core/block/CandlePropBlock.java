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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A candle prop that stands on the floor and burns as a vanilla candle does: the Large Candle, the
 * Candle Cluster and the Huge Candle. Lit with flint and steel, a fire charge, a burning arrow or a
 * torch, put out with an empty hand or by water.
 *
 * <p>The lighting is vanilla's own: these blocks are in {@code #minecraft:candles}, which is what
 * FlintAndSteelItem and FireChargeItem test (CandleBlock.canLight wants that tag and both LIT and
 * WATERLOGGED), and AbstractCandleBlock gives the burning-arrow lighting and the flames and smoke
 * at {@link #getParticleOffsets}. They are not in the candles ITEM tag, so they do not go on a cake.
 * A torch (or soul torch) lights them too, which vanilla candles do not allow: a candle this size
 * is something you would light from the torch in your hand. The torch is not used up.
 *
 * <p>They are blocks, not entities, so they cannot be knocked over, and they give light.
 *
 * @author Mark Gottschling on 10/22/2026
 */
public abstract class CandlePropBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected CandlePropBlock(Properties properties) {
        super(properties);
    }

    /** An empty hand puts it out, as vanilla's candle. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (player.getAbilities().mayBuild && player.getMainHandItem().isEmpty() && state.getValue(LIT)) {
            extinguish(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    /** A torch lights it. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if ((held.is(Items.TORCH) || held.is(Items.SOUL_TORCH)) && this.canBeLit(state)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL_IMMEDIATE);
                level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                        0.6F, 1.0F + level.getRandom().nextFloat() * 0.2F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /** Placed dry, or waterlogged in a water source. */
    protected BlockState placed(BlockState state, BlockPlaceContext context) {
        return state.setValue(LIT, false).setValue(WATERLOGGED, Waterlogging.placedInWater(context));
    }

    @Override
    protected boolean canBeLit(BlockState state) {
        return !state.getValue(WATERLOGGED) && super.canBeLit(state);
    }

    /** It has to stand on something, as a candle does. */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        Waterlogging.tickWater(state, level, pos);
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    /** Water floods in: the flame goes out, as vanilla's candle. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (!state.getValue(WATERLOGGED) && fluid.getType() == Fluids.WATER) {
            BlockState flooded = state.setValue(WATERLOGGED, true);
            if (state.getValue(LIT)) {
                extinguish(null, flooded, level, pos);
            } else {
                level.setBlock(pos, flooded, Block.UPDATE_ALL);
            }
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
            return true;
        }
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }
}
