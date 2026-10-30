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
package mod.gottsch.forge.dungeonblocks.core.block;

import java.util.ArrayList;
import java.util.List;

import mod.gottsch.forge.dungeonblocks.core.state.properties.FacadeShape;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Shared behaviour for the facade family - blocks that run along a wall and fold
 * around its corners (facade, fluted facade, quarter facade, cornice, crown
 * molding, ledge).
 *
 * <p>LEFT and RIGHT in {@link FacadeShape} are <b>relative to FACING</b>, the way
 * vanilla {@code StairsShape} defines them: RIGHT is the clockwise side of the
 * block's facing direction, LEFT the counter-clockwise side. That is what lets a
 * single rule cover all four horizontal facings, and what makes a rotated
 * structure placement come out right without touching SHAPE at all.
 *
 * @author Mark Gottschling on Mar 24, 2020
 */
public interface IFacadeShapeBlock extends IFacingBlock {
	public static final EnumProperty<FacadeShape> SHAPE = EnumProperty.create("shape", FacadeShape.class);

	/** offsets of the straight / inner / outer runs in a table built by {@link #buildShapeTable} */
	static final int STRAIGHT_OFFSET = 0;
	static final int INNER_OFFSET = 4;
	static final int OUTER_OFFSET = 8;

	public boolean isBlockInstanceOf(Block block);

	/**
	 * The number of clockwise quarter-turns that carry the canonical north-facing
	 * geometry onto the geometry for {@code facing}. Equals the model's y-rotation
	 * divided by 90, which is what keeps collision and rendering in step.
	 */
	static int rotationSteps(Direction facing) {
		// FACING is a full 6-way property; a vertical facing has no 2D value, so treat it as north
		return facing.getAxis().isVertical() ? 0 : facing.getOpposite().get2DDataValue();
	}

	/**
	 * Rotates a shape a quarter-turn clockwise about the block's vertical centre
	 * line, the same direction a blockstate {@code "y"} rotation turns a model.
	 */
	static VoxelShape rotateY90(VoxelShape shape) {
		List<VoxelShape> boxes = new ArrayList<>();
		shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> boxes.add(Shapes.box(1.0D - z2, y1, x1, 1.0D - z1, y2, x2)));
		return boxes.stream().reduce(Shapes.empty(), Shapes::or).optimize();
	}

	/**
	 * Builds the 12-entry shape table a facade block indexes with
	 * {@link #getBlockShapeIndex}, by turning three canonical shapes through all
	 * four facings. Every canonical shape is authored for a <b>north-facing</b>
	 * block - the orientation its model is drawn in at y-rotation 0 - so the table
	 * cannot drift out of step with the models the way a hand-written one can.
	 *
	 * @param straight a straight piece
	 * @param inner    an inner corner turning to the block's right (clockwise) side
	 * @param outer    an outer corner turning to the block's right (clockwise) side
	 */
	static VoxelShape[] buildShapeTable(VoxelShape straight, VoxelShape inner, VoxelShape outer) {
		VoxelShape[] shapes = new VoxelShape[12];
		VoxelShape rotatedStraight = straight;
		VoxelShape rotatedInner = inner;
		VoxelShape rotatedOuter = outer;
		for (int steps = 0; steps < 4; steps++) {
			shapes[STRAIGHT_OFFSET + steps] = rotatedStraight;
			shapes[INNER_OFFSET + steps] = rotatedInner;
			shapes[OUTER_OFFSET + steps] = rotatedOuter;
			rotatedStraight = rotateY90(rotatedStraight);
			rotatedInner = rotateY90(rotatedInner);
			rotatedOuter = rotateY90(rotatedOuter);
		}
		return shapes;
	}

	/**
	 * Returns the index into the block's shape table for the given state. A LEFT
	 * corner is the RIGHT corner turned one more quarter-turn clockwise - the same
	 * extra 90 degrees the blockstate gives the model.
	 */
	default public int getBlockShapeIndex(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		int steps = rotationSteps(state.getValue(FACING));

		return switch (state.getValue(SHAPE)) {
		case STRAIGHT -> STRAIGHT_OFFSET + steps;
		case INNER_RIGHT -> INNER_OFFSET + steps;
		case INNER_LEFT -> INNER_OFFSET + (steps + 1) % 4;
		case OUTER_RIGHT -> OUTER_OFFSET + steps;
		case OUTER_LEFT -> OUTER_OFFSET + (steps + 1) % 4;
		};
	}

	/**
	 * Picks the corner shape for a block from its neighbours. Works off the block's
	 * own facing, so all four horizontal facings are handled by the one rule.
	 *
	 * <p>Takes a {@link LevelAccessor}, not a {@code Level}, because {@code updateShape} is handed
	 * one and during <strong>worldgen</strong> it is a {@code WorldGenRegion}, which is not a
	 * {@code Level}. Casting instead of widening threw {@code ClassCastException} from inside
	 * vanilla's {@code Block.updateFromNeighbourShapes}, surfacing as a "Feature placement"
	 * ReportedException that killed chunk generation for any structure placing these blocks.
	 * Nothing here needs more than block reads, which {@code LevelAccessor} already provides.</p>
	 */
	default public BlockState getBlockStateForPlacement(LevelAccessor level, BlockState blockState, BlockPos blockPos) {
		Direction facing = blockState.getValue(FACING);
		if (facing.getAxis().isVertical()) {
			return blockState.setValue(SHAPE, FacadeShape.STRAIGHT);
		}

		Direction clockwise = facing.getClockWise();
		Direction counterClockwise = facing.getCounterClockWise();

		/*
		 * inner test - the run continues in front of this block and turns away there,
		 * so this block has to fill both arms of the corner.
		 */
		BlockState frontState = level.getBlockState(blockPos.relative(facing));
		if (isBlockInstanceOf(frontState.getBlock())) {
			Direction frontFacing = frontState.getValue(FACING);
			if (frontFacing == counterClockwise && !isSameBasic(level, blockPos.relative(clockwise), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_RIGHT);
			} else if (frontFacing == clockwise && !isSameBasic(level, blockPos.relative(counterClockwise), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.INNER_LEFT);
			}
			return blockState;
		}

		// outer test - the run turns away behind this block, leaving only the corner nub
		BlockState backState = level.getBlockState(blockPos.relative(facing.getOpposite()));
		if (isBlockInstanceOf(backState.getBlock())) {
			Direction backFacing = backState.getValue(FACING);
			if (backFacing == counterClockwise && !isSameBasic(level, blockPos.relative(counterClockwise), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_RIGHT);
			} else if (backFacing == clockwise && !isSameBasic(level, blockPos.relative(clockwise), blockState)) {
				blockState = blockState.setValue(SHAPE, FacadeShape.OUTER_LEFT);
			}
		}

		return blockState;
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
}
