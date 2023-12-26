/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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

import mod.gottsch.neo.dungeonblocks.core.state.properties.FacadeShape;
import mod.gottsch.neo.gottschcore.block.IFacingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;


/**
 * @author Mark Gottschling on Mar 24, 2020
 *
 */
public interface IFacadeShapeBlock extends IFacingBlock {
	public static final EnumProperty<FacadeShape> SHAPE = EnumProperty.create("shape", FacadeShape.class);

	public boolean isBlockInstanceOf(Block block);

	/**
	 * Returns the VoxelShape (ie bounding box) of the block in the correct position.
	 * NOTE if shape != STRAIGHT, then facing index can only == North || South
	 */
	default public int getBlockShapeIndex(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		int facingIndex = 0;
		int shapeIndex = 0;

		Direction direction = state.getValue(FACING);
		FacadeShape shape = state.getValue(SHAPE);

		facingIndex = switch (direction) {
		case NORTH -> Direction.NORTH.get2DDataValue();
		case SOUTH ->  Direction.SOUTH.get2DDataValue();
		case EAST -> Direction.EAST.get2DDataValue();
		case WEST -> Direction.WEST.get2DDataValue();
		default -> Direction.NORTH.get2DDataValue();
		};

		// TODO make consts for all the indexes or an enum with int values
		shapeIndex = switch (shape) {
		case STRAIGHT -> facingIndex;
		case INNER_LEFT -> {
			int i = 4;
			if (facingIndex == 0) i = 4;
			else if (facingIndex == 2)	i = 5;
			yield i;
		}
		case INNER_RIGHT -> {
			int i = 6;
			if (facingIndex == 0) i = 6;
			else if (facingIndex == 2) i = 7;
			yield i;
		}
		case OUTER_LEFT -> {
			int i = 9;
			if (facingIndex == 0) i = 9;
			else if (facingIndex == 2) i = 8;
			yield i;
		}
		case OUTER_RIGHT -> {
			int i = 11;
			if (facingIndex == 0) i = 11;
			else if (facingIndex == 2) i = 10;
			yield i;
		}
		default -> 0;
		};
		return shapeIndex;
	}

	/**
	 * 
	 * @param level
	 * @param blockState
	 * @param blockPos
	 * @return
	 */
	default public BlockState getBlockStateForPlacement(Level level, BlockState blockState, BlockPos blockPos) {
		Direction direction = blockState.getValue(FACING);
		BlockState newState = blockState;
		
		// test the direction the block is facing
		newState = switch (direction) {
		case SOUTH -> getStateForSouthDirection(level, blockPos, blockState);
		case NORTH -> getStateForNorthDirection(level, blockPos, blockState);
		case EAST -> getStateForEastDirection(level, blockPos, blockState);
		case WEST -> getStateForWestDirection(level, blockPos, blockState);
		default -> blockState.setValue(SHAPE, FacadeShape.STRAIGHT);
		};
		return newState;
	}

	/**
	 * Check whether there is a same block at the given position and it has the same
	 * properties as the given BlockState
	 */
	default public boolean isSameBasic(LevelAccessor level, BlockPos pos, BlockState stateIn) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		/**
		 * Checks if a block is an instance of this class
		 */
		return isBlockInstanceOf(block) && state.getValue(FACING) == stateIn.getValue(FACING);
	}

	default BlockState getStateForSouthDirection(Level level, BlockPos blockPos, BlockState blockState) {
		BlockState neighborState;
		Block neighborBlock;
		Direction neighborFacing;

		neighborState = level.getBlockState(blockPos.south());
		neighborBlock = neighborState.getBlock();

		// inner test
		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.WEST && !isSameBasic(level, blockPos.east(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_RIGHT);
			} else if (neighborFacing == Direction.EAST && !isSameBasic(level, blockPos.west(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_LEFT);
			}
			return blockState;
		}

		// outer test
		neighborState = level.getBlockState(blockPos.north());
		neighborBlock = neighborState.getBlock();

		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.WEST && !isSameBasic(level, blockPos.west(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_LEFT);
			} else if (neighborFacing == Direction.EAST && !isSameBasic(level, blockPos.east(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_RIGHT);
			}
		}

		return blockState;
	}

	default public BlockState getStateForNorthDirection(Level level, BlockPos blockPos, BlockState blockState) {
		BlockState neighborState;
		Block neighborBlock;
		Direction neighborFacing;

		neighborState = level.getBlockState(blockPos.north());
		neighborBlock = neighborState.getBlock();

		// inner test
		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.WEST && !isSameBasic(level, blockPos.east(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_RIGHT);
			} else if (neighborFacing == Direction.EAST && !isSameBasic(level, blockPos.west(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_LEFT);
			}
			return blockState;
		}

		// outer test
		neighborState = level.getBlockState(blockPos.south());
		neighborBlock = neighborState.getBlock();

		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.WEST && !isSameBasic(level, blockPos.west(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_LEFT);
			} else if (neighborFacing == Direction.EAST && !isSameBasic(level, blockPos.east(), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_RIGHT);
			}
		}
		return blockState;
	}

	default public BlockState getStateForEastDirection(Level level, BlockPos blockPos, BlockState blockState) {
		BlockState neighborState;
		Block neighborBlock;
		Direction neighborFacing;

		neighborState = level.getBlockState(blockPos.east());
		neighborBlock = neighborState.getBlock();

		// inner test
		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.NORTH && !isSameBasic(level, blockPos.south(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.NORTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_LEFT);
			} else if (neighborFacing == Direction.SOUTH && !isSameBasic(level, blockPos.north(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.SOUTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_LEFT);
			}
			return blockState;
		}

		// outer test
		neighborState = level.getBlockState(blockPos.west());
		neighborBlock = neighborState.getBlock();

		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.NORTH && !isSameBasic(level, blockPos.north(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.NORTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_RIGHT);
			} else if (neighborFacing == Direction.SOUTH && !isSameBasic(level, blockPos.south(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.SOUTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_RIGHT);
			}
		}
		return blockState;
	}

	default public BlockState getStateForWestDirection(Level level, BlockPos blockPos, BlockState blockState) {
		BlockState neighborState;
		Block neighborBlock;
		Direction neighborFacing;

		neighborState = level.getBlockState(blockPos.west());
		neighborBlock = neighborState.getBlock();

		// inner test
		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.NORTH && !isSameBasic(level, blockPos.south(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.NORTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_RIGHT);
			} else if (neighborFacing == Direction.SOUTH && !isSameBasic(level, blockPos.north(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.SOUTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_RIGHT);
			}
			return blockState;
		}

		// outer test
		neighborState = level.getBlockState(blockPos.east());
		neighborBlock = neighborState.getBlock();

		if (isBlockInstanceOf(neighborBlock)) {
			neighborFacing = neighborState.getValue(FACING);
			if (neighborFacing == Direction.NORTH && !isSameBasic(level, blockPos.north(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.NORTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_LEFT);
			} else if (neighborFacing == Direction.SOUTH && !isSameBasic(level, blockPos.south(), blockState)) {
				blockState = blockState.setValue(FACING, Direction.SOUTH);
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_LEFT);
			}
		}
		return blockState;
	}
}
