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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * Four pillar candles of mixed heights in one cell, burnt down unevenly, the tallest at the back. A
 * group of them in one block, for where a scatter of Large Candles would take a whole floor.
 * FACING is the cluster's front, where its shortest candles stand; it is placed facing the player,
 * so the short ones are nearest and none hides behind a taller one.
 *
 * <p>The model is blockbench/candle_cluster.bbmodel, authored facing north; CANDLES must match
 * CANDLE_CLUSTER in tools/gen_bbmodels.py. Shapes and flames are those numbers turned to each
 * FACING, as the blockstate turns the model.
 *
 * @author Mark Gottschling on 10/22/2026
 */
public class CandleClusterBlock extends CandlePropBlock {
    /** Each candle: centre x, centre z, width, height, wick height, in pixels. */
    private static final int[][] CANDLES = {{4, 10, 4, 12, 2}, {11, 11, 4, 8, 2}, {8, 5, 4, 5, 1}, {12, 4, 2, 3, 1}};
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIT) ? 13 : 0;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    private static final Map<Direction, List<Vec3>> FLAMES = new EnumMap<>(Direction.class);

    static {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            VoxelShape shape = Shapes.empty();
            List<Vec3> flames = new ArrayList<>();
            for (int[] c : CANDLES) {
                double half = c[2] / 2.0;
                double[] a = turn(c[0] - half, c[1] - half, facing);
                double[] b = turn(c[0] + half, c[1] + half, facing);
                shape = Shapes.or(shape, Block.box(Math.min(a[0], b[0]), 0, Math.min(a[1], b[1]),
                        Math.max(a[0], b[0]), c[3], Math.max(a[1], b[1])));
                double[] wick = turn(c[0], c[1], facing);
                flames.add(new Vec3(wick[0] / 16.0, (c[3] + c[4] + 1) / 16.0, wick[1] / 16.0));
            }
            SHAPES.put(facing, shape);
            FLAMES.put(facing, flames);
        }
    }

    /**
     * A point of the north-authored model, in pixels, where it lies once the model is turned to
     * `facing`: a quarter turn clockwise (seen from above) per step from north, as a blockstate's
     * y rotation turns it.
     */
    private static double[] turn(double x, double z, Direction facing) {
        for (int i = 0; i < ((facing.get2DDataValue() + 2) & 3); i++) {
            double t = x;
            x = 16 - z;
            z = t;
        }
        return new double[]{x, z};
    }

    public static final MapCodec<CandleClusterBlock> CODEC = simpleCodec(CandleClusterBlock::new);

    public CandleClusterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return placed(defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()), context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return FLAMES.get(state.getValue(FACING));
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
