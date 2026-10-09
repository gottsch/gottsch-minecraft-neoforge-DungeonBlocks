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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * A block that points away from the face it was placed against: the sharpened logs and capstones
 * (a true square pyramid, models/block/pyramid.obj via Forge's OBJ loader), and the spikes.
 *
 * <p>Placed like a log - by the face clicked - but it keeps a full direction rather than a log's
 * axis, because a point has a sign: on the end of a sideways log, an axis would leave half of all
 * placements pointing back into the log.
 *
 * <p>Outline and collision are the default full cube. The occlusion shape is only a 1px slab across
 * the base: enough for the base face to cull against the block it sits on, while every other
 * neighbour keeps rendering its face - a full-cube occlusion shape would cut holes in them.
 */
public class PyramidBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final Map<Direction, VoxelShape> BASE = slabs(1);

    public PyramidBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    /**
     * For each FACING, a slab `depth` px thick against the base - the side opposite the point.
     */
    protected static Map<Direction, VoxelShape> slabs(double depth) {
        double d = 16 - depth;
        return new EnumMap<>(Map.of(
                Direction.UP, Block.box(0, 0, 0, 16, depth, 16),
                Direction.DOWN, Block.box(0, d, 0, 16, 16, 16),
                Direction.NORTH, Block.box(0, 0, d, 16, 16, 16),
                Direction.SOUTH, Block.box(0, 0, 0, 16, 16, depth),
                Direction.EAST, Block.box(0, 0, 0, depth, 16, 16),
                Direction.WEST, Block.box(d, 0, 0, 16, 16, 16)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return BASE.get(state.getValue(FACING));
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
