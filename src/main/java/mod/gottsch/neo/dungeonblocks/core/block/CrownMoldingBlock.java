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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mark Gottschling on Jan 18, 2020
 *
 */
public class CrownMoldingBlock extends FacadeShapeBlock {
	// Voxels are like the bounding boxes (AABBs) NF= North Facing, SF = South
	// Facing, etc
	private static final VoxelShape NORTH_FACING_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 12.0D, 16.0D, 3.0D, 16.0D), // bottom (16x3x4)
			Block.box(0.0D, 9.0D, 10.0D, 16.0D, 16.0D, 16.0D), // top (16x7x6)
			Block.box(0.0D, 8.0D, 12.0D, 16.0D, 9.0D, 14.0D), // notch (16x1x2)
			Block.box(0.0D, 3.0D, 14.0D, 16.0D, 9.0D, 16.0D)); // middle (16x6x2)

	private static final VoxelShape EAST_FACING_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 4.0D, 3.0D, 16.0D),
			Block.box(0.0D, 9.0D, 0.0D, 6.0D, 16.0D, 16.0D),
			Block.box(2.0D, 8.0D, 0.0D, 4.0D, 9.0D, 16.0D),
			Block.box(0.0D, 3.0D, 0.0D, 2.0D, 9.0D, 16.0D));

	private static final VoxelShape SOUTH_FACING_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 3, 4),
			Block.box(0, 9, 0, 16, 16, 6), Block.box(0, 8, 2, 16, 9, 4),
			Block.box(0, 3, 0, 16, 9, 2));

	private static final VoxelShape WEST_FACING_SHAPE = Shapes.or(Block.box(12, 0, 0, 16, 3, 16),
			Block.box(10, 9, 0, 16, 16, 16), Block.box(12, 8, 0, 14, 9, 16),
			Block.box(14, 3, 0, 16, 9, 16));

	// outer corners
	private static final VoxelShape TOP_LEFT_OUTER_SHAPE = Shapes.or(Block.box(10, 9, 10, 16, 16, 16), // top
			Block.box(14, 3, 14, 16, 9, 16), // middle
			Block.box(12, 8, 12, 16, 9, 14), // notch
			Block.box(12, 8, 14, 14, 9, 16), // notch2
			Block.box(12, 0, 14, 16, 3, 16)); // bottom

	private static final VoxelShape TOP_RIGHT_OUTER_SHAPE = Shapes.or(Block.box(0, 9, 10, 6, 16, 16),
			Block.box(0, 3, 14, 2, 9, 16), Block.box(2, 8, 12, 4, 16, 16),
			Block.box(0, 8, 12, 2, 16, 14), Block.box(0, 0, 12, 4, 3, 16));

	private static final VoxelShape BOTTOM_LEFT_OUTER_SHAPE = Shapes.or(Block.box(10, 9, 0, 16, 16, 6),
			Block.box(14, 3, 0, 16, 9, 2), Block.box(12, 8, 0, 16, 16, 4),
			Block.box(14, 8, 2, 16, 16, 4), Block.box(12, 0, 0, 16, 3, 4));

	private static final VoxelShape BOTTOM_RIGHT_OUTER_SHAPE = Shapes.or(Block.box(0, 9, 0, 6, 16, 6), // top
			Block.box(0, 3, 0, 2, 9, 2), // middle
			Block.box(0, 8, 2, 4, 16, 4), // notch
			Block.box(2, 8, 0, 4, 16, 2), // notch2
			Block.box(0, 0, 0, 4, 3, 4) // bottom
	);

	// inner corners
	private static final VoxelShape TOP_LEFT_INNER_SHAPE = Shapes.or(SOUTH_FACING_SHAPE,
			Block.box(0, 9, 6, 6, 16, 16), Block.box(0, 3, 2, 2, 9, 16),
			Block.box(2, 8, 4, 4, 9, 16), Block.box(0, 0, 4, 4, 3, 16));

	private static final VoxelShape TOP_RIGHT_INNER_SHAPE = Shapes.or(SOUTH_FACING_SHAPE,
			Block.box(10, 9, 6, 16, 16, 16), Block.box(14, 3, 2, 16, 9, 16),
			Block.box(12, 8, 4, 16, 9, 16), Block.box(12, 0, 4, 16, 3, 16));

	private static final VoxelShape BOTTOM_LEFT_INNER_SHAPE = Shapes.or(NORTH_FACING_SHAPE,
			Block.box(0, 9, 0, 6, 16, 10), Block.box(0, 3, 0, 2, 9, 14),
			Block.box(2, 8, 0, 4, 9, 12), Block.box(0, 0, 0, 4, 3, 12));

	private static final VoxelShape BOTTOM_RIGHT_INNER_SHAPE = Shapes.or(NORTH_FACING_SHAPE,
			Block.box(10, 9, 0, 16, 16, 10), Block.box(14, 3, 0, 16, 9, 14),
			Block.box(12, 8, 0, 16, 9, 12), Block.box(12, 0, 0, 16, 3, 12));

	// SWNE = 0,1,2,3
	private VoxelShape voxelShapes[] = {
			// straight
			SOUTH_FACING_SHAPE, WEST_FACING_SHAPE, NORTH_FACING_SHAPE, EAST_FACING_SHAPE,

			// inner left
			TOP_LEFT_INNER_SHAPE, // 4
			BOTTOM_LEFT_INNER_SHAPE, // 5

			// inner right
			TOP_RIGHT_INNER_SHAPE, // 6
			BOTTOM_RIGHT_INNER_SHAPE, // 7

			// outer left
			TOP_LEFT_OUTER_SHAPE, BOTTOM_LEFT_OUTER_SHAPE,

			// outer right
			TOP_RIGHT_OUTER_SHAPE, BOTTOM_RIGHT_OUTER_SHAPE };

	/**
	 * 
	 * @param properties
	 */
	public CrownMoldingBlock(Properties properties) {
		super(properties);
	}

	/**
	 * Returns the VoxelShape (ie bounding box) of the block in the correctposition.
	 * NOTE if shape != STRAIGHT, then facing index can only == North || South
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		int shapeIndex = getBlockShapeIndex(state, getter, pos, context);
		return voxelShapes[shapeIndex];
	}

	/**
	 * Checks if a block is same as FacadeBlock
	 */
	@Override
	public boolean isBlockInstanceOf(Block block) {
		return block instanceof CrownMoldingBlock;
	}
}
