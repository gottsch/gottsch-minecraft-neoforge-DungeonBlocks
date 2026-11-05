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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * A candle a block and a half tall and half a block across, in two halves placed and broken like
 * the skull pike's: LOWER where clicked, UPPER above, losing either destroys the other, and only
 * LOWER drops the item. The wick and flame are on the UPPER half, which alone gives light.
 *
 * <p>Both halves carry LIT, kept equal from the partner's state in {@link #updateShape} - which
 * reads only the neighbour state it is handed, never the level, so it is safe while worldgen
 * places the halves. Lighting either half (flint and steel lights the half clicked) lights the
 * candle; it will not stay lit while either half is waterlogged. The models are
 * blockbench/huge_candle_lower.bbmodel and huge_candle_upper.bbmodel.
 *
 * @author Mark Gottschling on 10/22/2026
 */
public class HugeCandleBlock extends CandlePropBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            state -> state.getValue(LIT) && state.getValue(HALF) == DoubleBlockHalf.UPPER ? 15 : 0;

    private static final VoxelShape LOWER = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape UPPER = Block.box(4, 0, 4, 12, 8, 12);
    /** A pixel above the tip of the 2px wick on the 8px upper half. */
    private static final List<Vec3> FLAME = List.of(new Vec3(0.5, 11 / 16.0, 0.5));

    public static final MapCodec<HugeCandleBlock> CODEC = simpleCodec(HugeCandleBlock::new);

    public HugeCandleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, LIT, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER : UPPER;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos above = context.getClickedPos().above();
        Level level = context.getLevel();
        return above.getY() < level.getMaxBuildHeight() && level.getBlockState(above).canBeReplaced(context)
                ? placed(defaultBlockState(), context)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos above = pos.above();
        level.setBlock(above, state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, level.getFluidState(above).getType() == Fluids.WATER), Block.UPDATE_ALL);
    }

    /** Put out from either half: the smoke rises from the wick, on the upper one. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && player.getAbilities().mayBuild
                && player.getMainHandItem().isEmpty() && state.getValue(LIT)) {
            BlockPos above = pos.above();
            BlockState upper = level.getBlockState(above);
            if (upper.is(this)) {
                extinguish(player, upper, level, above);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    /** The lower half stands on the floor; the upper half on the lower. */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER || super.canSurvive(state, level, pos);
    }

    /**
     * Losing the other half destroys this one, so the candle breaks as a unit; and this half takes
     * its partner's flame, so the two are lit together. Neither is lit while either is in water.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        if (direction == (lower ? Direction.UP : Direction.DOWN)) {
            if (!neighbour.is(this) || neighbour.getValue(HALF) == state.getValue(HALF)) {
                return Blocks.AIR.defaultBlockState();
            }
            boolean lit = neighbour.getValue(LIT) && !neighbour.getValue(WATERLOGGED) && !state.getValue(WATERLOGGED);
            Waterlogging.tickWater(state, level, pos);
            return state.setValue(LIT, lit);
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? FLAME : List.of();
    }

    /**
     * In creative, breaking the upper half would cascade to the lower one with drops on and hand
     * the player a free candle: clear it with the no-drop flag instead, as TallPropBlock does.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
