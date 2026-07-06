/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import mod.gottsch.neo.gottschcore.block.WaterloggedFacingHalfBlock;

/**
 * @author Mark Gottschling on May 7, 2025
 *
 */
public class AnglePlateBracketBlock extends WaterloggedFacingHalfBlock {
    private static final VoxelShape TOP_SHAPE = Block.box(0, 14, 0, 16, 16, 16);
    private static final VoxelShape TOP_NORTH_SHAPE = Shapes.or(TOP_SHAPE, Block.box(0, 0, 14, 16, 14, 16));
    private static final VoxelShape TOP_EAST_SHAPE = Shapes.or(TOP_SHAPE, Block.box(0, 0D, 0D, 2, 14, 16));
    private static final VoxelShape TOP_SOUTH_SHAPE = Shapes.or(TOP_SHAPE, Block.box(0D, 0D, 0D, 16D, 14, 2));
    private static final VoxelShape TOP_WEST_SHAPE = Shapes.or(TOP_SHAPE, Block.box(14D, 0D, 0D, 16, 14, 16));

    private static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape BOTTOM_NORTH_SHAPE = Shapes.or(BOTTOM_SHAPE, Block.box(0, 2, 14, 16, 16, 16));
    private static final VoxelShape BOTTOM_EAST_SHAPE = Shapes.or(BOTTOM_SHAPE, Block.box(0, 2, 0D, 2, 16, 16));
    private static final VoxelShape BOTTOM_SOUTH_SHAPE = Shapes.or(BOTTOM_SHAPE, Block.box(0D, 2, 0D, 16D, 16, 2));
    private static final VoxelShape BOTTOM_WEST_SHAPE = Shapes.or(BOTTOM_SHAPE, Block.box(14D, 2, 0D, 16, 16, 16));

    /**
     * @param properties
     */
    public AnglePlateBracketBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Half half = getHalf(state);
        Direction direction = getFacing(state);

        return switch(half) {
            case TOP -> switch (direction) {
                case UP, DOWN, NORTH -> TOP_NORTH_SHAPE;
                case EAST -> TOP_EAST_SHAPE;
                case SOUTH -> TOP_SOUTH_SHAPE;
                case WEST -> TOP_WEST_SHAPE;
            };
            case BOTTOM -> switch (direction) {
                case UP, DOWN, NORTH -> BOTTOM_NORTH_SHAPE;
                case EAST -> BOTTOM_EAST_SHAPE;
                case SOUTH -> BOTTOM_SOUTH_SHAPE;
                case WEST -> BOTTOM_WEST_SHAPE;
            };
        };
    }
}
