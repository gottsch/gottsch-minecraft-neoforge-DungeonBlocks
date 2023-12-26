/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Has the shape behavior similar to FacadeShapeBlock but slightly different.
 * @author Mark Gottschling on Oct 27, 2023
 */
public class
SewerBlock extends NonCubeFacingBlock {
    public static final EnumProperty<SewerShape> SHAPE = EnumProperty.create("shape", SewerShape.class);

    /*
     * set the boundaries of the block slightly smaller than full
     * so that it is not a full block and other block faces around it
     * will render. i'm sure there is an actual property for this
     * but i haven't found it yet. forceSolidOff() doesn't seem to work.
     */
    private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 15.99D, 15.99D, 15.99D);

    private static final VoxelShape BOTTOM = Block.box(0D, 0D, 0D, 16D, 11D, 16D);
    private static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(
            BOTTOM,
            Block.box(13, 0, 0, 16, 16, 16), //east
            Block.box(0, 0, 0, 3, 16, 16) // west
    );
    private static final VoxelShape EAST_WEST_SHAPE = Shapes.or(
            BOTTOM,
            Block.box(0, 0, 13, 16, 16, 16), //east
            Block.box(0, 0, 0, 16, 16, 3) // west
    );

    // corners
    private static final VoxelShape TOP_LEFT_SHAPE = Shapes.or(
            Block.box(0, 11, 0, 16, 16, 3),
            Block.box(0, 11, 0, 3, 16, 16),
            Block.box(13, 11, 13, 16, 16, 16),
            BOTTOM
    );
    private static final VoxelShape TOP_RIGHT_SHAPE = Shapes.or(
            Block.box(0, 11, 0, 16, 16, 3),
            Block.box(13, 11, 0, 16, 16, 16),
            Block.box(0, 11, 13, 3, 16, 16),
            BOTTOM
    );
    private static final VoxelShape BOTTOM_LEFT_SHAPE = Shapes.or(
            Block.box(0, 11, 13, 16, 16, 16),
            Block.box(0, 11, 0, 3, 16, 16),
            Block.box(13, 11, 0, 16, 16, 3),
            BOTTOM
    );
    private static final VoxelShape BOTTOM_RIGHT_SHAPE = Shapes.or(
            Block.box(0, 11, 13, 16, 16, 16),
            Block.box(13, 11, 0, 16, 16, 16),
            Block.box(0, 11, 0, 3, 16, 3),
            BOTTOM
    );

    // NS = 0, EW = 1
    private VoxelShape voxelShapes[] = {
            // straight
            NORTH_SOUTH_SHAPE, EAST_WEST_SHAPE,

            TOP_LEFT_SHAPE,  // 2
            BOTTOM_LEFT_SHAPE, // 3

            // outer right
            TOP_RIGHT_SHAPE, // 4
            BOTTOM_RIGHT_SHAPE}; //5

    /**
     *
     * @param properties
     */
    public SewerBlock(Properties properties) {
        super(properties);
    }

    /**
     *
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
//        Direction direction = state.getValue(FACING);
//
//        return switch (direction) {
//            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
//            case EAST, WEST -> EAST_WEST_SHAPE;
//            default -> AABB;
//        };
//    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int shapeIndex = getBlockShapeIndex(state, getter, pos, context);
        return voxelShapes[shapeIndex];
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos_, CollisionContext context) {
        return Shapes.empty();
    }

    /**
     * Returns the VoxelShape (ie bounding box) of the block in the correct position.
     * NOTE if shape != STRAIGHT, then facing index can only == North || South
     */
    public int getBlockShapeIndex(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int facingIndex = 0;
        int shapeIndex = 0;

        SewerShape shape = state.getValue(SHAPE);
        Direction direction = state.getValue(FACING);

        shapeIndex = switch (shape) {
            case STRAIGHT -> switch (direction) {
                case NORTH, SOUTH -> 0;
                case EAST, WEST -> 1;
                default -> 0;
            };
            case TOP_LEFT -> 2;
            case BOTTOM_LEFT -> 3;
            case TOP_RIGHT -> 4;
            case BOTTOM_RIGHT -> 5;
        };
        return shapeIndex;
    }

    /**
     * This method returns the state of the block so that the correct entry in the
     * blockstate.json file can be selected and the corresponding block model
     * rendered.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        // custom method to get block state
        BlockState placementBlockState = getBlockStateForPlacement(context.getLevel(), blockState, context.getClickedPos());

        return placementBlockState;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor level, BlockPos blockPos, BlockPos blockPos2) {
        BlockState placementBlockState = super.updateShape(blockState, direction, blockState2, level, blockPos, blockPos2);
        // custom method to get block state
        placementBlockState = getBlockStateForPlacement((Level)level, placementBlockState, blockPos);
        return placementBlockState;
    }

    /**
     *
     * @param level
     * @param blockState
     * @param blockPos
     * @return
     */
    public BlockState getBlockStateForPlacement(Level level, BlockState blockState, BlockPos blockPos) {
        Direction direction = blockState.getValue(FACING);
        BlockState newState = blockState;

        // test the direction the block is facing
//        newState = switch (direction) {
//            case SOUTH -> getStateForSouthDirection(level, blockPos, blockState);
//            case NORTH -> getStateForNorthDirection(level, blockPos, blockState);
//            case EAST -> getStateForEastDirection(level, blockPos, blockState);
//            case WEST -> getStateForWestDirection(level, blockPos, blockState);
//            default -> blockState.setValue(SHAPE, FacadeShape.STRAIGHT);
//        };
        newState = getStateForSouthDirection(level, blockPos, blockState);
        return newState;
    }

    /**
     * Currently, with only STRAIGHT, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT shapes,
     * it doesn't matter which direction the block is facing. It only matter if it has ajoining blocks in the
     * correct directions
     * @param level
     * @param blockPos
     * @param blockState
     * @return
     */
    BlockState getStateForSouthDirection(Level level, BlockPos blockPos, BlockState blockState) {
        BlockState neighborState;
        Block neighborBlock;
        Direction neighborFacing;

        BlockState neighborState2;
        Block neighborBlock2;
        Direction neighborFacing2;

        // default value
        blockState = blockState.setValue(SHAPE, SewerShape.STRAIGHT);

        neighborState = level.getBlockState(blockPos.south());
        neighborBlock = neighborState.getBlock();

        // check to the south
        if (isBlockInstanceOf(neighborBlock)) {
            neighborFacing = neighborState.getValue(FACING);
            if (neighborFacing == Direction.NORTH || neighborFacing == Direction.SOUTH) {
                neighborState2 = level.getBlockState(blockPos.east());
                neighborBlock2 = neighborState2.getBlock();
                if (isBlockInstanceOf(neighborBlock2)) {
                    neighborFacing2 = neighborState2.getValue(FACING);
                    if (neighborFacing2 == Direction.EAST || neighborFacing2 == Direction.WEST) {
                        blockState = blockState.setValue(SHAPE, SewerShape.TOP_LEFT);
                        return blockState;
                    }
                }
                neighborState2 = level.getBlockState(blockPos.west());
                neighborBlock2 = neighborState2.getBlock();
                if (isBlockInstanceOf(neighborBlock2)) {
                    neighborFacing2 = neighborState2.getValue(FACING);
                    if (neighborFacing2 == Direction.EAST || neighborFacing2 == Direction.WEST) {
                        blockState = blockState.setValue(SHAPE, SewerShape.TOP_RIGHT);
                        return blockState;
                    }
                }
            }
        }

        // check to the north
        neighborState = level.getBlockState(blockPos.north());
        neighborBlock = neighborState.getBlock();

        if (isBlockInstanceOf(neighborBlock)) {
            neighborFacing = neighborState.getValue(FACING);
            if (neighborFacing == Direction.NORTH || neighborFacing == Direction.SOUTH) {
                neighborState2 = level.getBlockState(blockPos.east());
                neighborBlock2 = neighborState2.getBlock();
                if (isBlockInstanceOf(neighborBlock2)) {
                    neighborFacing2 = neighborState2.getValue(FACING);
                    if (neighborFacing2 == Direction.EAST || neighborFacing2 == Direction.WEST) {
                        blockState = blockState.setValue(SHAPE, SewerShape.BOTTOM_LEFT);
                        return blockState;
                    }
                }
                neighborState2 = level.getBlockState(blockPos.west());
                neighborBlock2 = neighborState2.getBlock();
                if (isBlockInstanceOf(neighborBlock2)) {
                    neighborFacing2 = neighborState2.getValue(FACING);
                    if (neighborFacing2 == Direction.EAST || neighborFacing2 == Direction.WEST) {
                        blockState = blockState.setValue(SHAPE, SewerShape.BOTTOM_RIGHT);
                        return blockState;
                    }
                }
            }
        }

        return blockState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state2, boolean flag) {
        level.neighborChanged(pos, state.getBlock(), pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean flag) {
        level.neighborChanged(pos, state.getBlock(), pos);
    }

    /**
     * Checks if a block is same as SewerBlock
     */
    public boolean isBlockInstanceOf(Block block) {
        return block instanceof SewerBlock;
    }

    public enum SewerShape implements StringRepresentable {
        STRAIGHT("straight"),
        TOP_LEFT("top_left"),
        BOTTOM_LEFT("bottom_left"),
        TOP_RIGHT("outer_right"),
        BOTTOM_RIGHT("bottom_right");

        private final String name;

        private SewerShape(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return getName();
        }
    }
}
