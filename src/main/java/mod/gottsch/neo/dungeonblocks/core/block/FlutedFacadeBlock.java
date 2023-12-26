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
 * @author Mark Gottschling on Jan 15, 2020
 *
 */
public class FlutedFacadeBlock extends FacadeShapeBlock {

	// Voxels are like the bounding boxes (AABBs)
	private static final VoxelShape NORTH_FACING_PART = Block.box(2.0D, 0.0D, 10.0D, 14.0D, 16.0D, 16.0D);
	private static final VoxelShape N1_PART = Block.box(0, 0, 8, 4, 16, 12);
	private static final VoxelShape N2_PART = Block.box(12.0D, 0.0D, 8.0D, 16.0D, 16.0D, 12.0D);

	private static final VoxelShape SOUTH_FACING_PART = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 6.0D);
	private static final VoxelShape S1_PART = Block.box(0, 0, 4, 4, 16, 8);
	private static final VoxelShape S2_PART = Block.box(12, 0.0D, 4.0D, 16.0D, 16.0D, 8.0D);

	private static final VoxelShape EAST_FACING_PART = Block.box(0.0D, 0.0D, 2.0D, 6.0D, 16.0D, 14.0D);
	private static final VoxelShape E1_PART = Block.box(4.0D, 0.0D, 0.0D, 8.0D, 16.0D, 4.0D);
	private static final VoxelShape E2_PART = Block.box(4.0D, 0.0D, 12.0D, 8.0D, 16.0D, 16.0D);

	private static final VoxelShape WEST_FACING_PART = Block.box(10.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);
	private static final VoxelShape W1_PART = Block.box(8.0D, 0.0D, 0.0D, 12.0D, 16.0D, 4.0D);
	private static final VoxelShape W2_PART = Block.box(8.0D, 0.0D, 12.0D, 12.0D, 16.0D, 16.0D);

	private static final VoxelShape NORTH_FACING_SHAPE = Shapes.or(NORTH_FACING_PART, N1_PART, N2_PART);
	private static final VoxelShape EAST_FACING_SHAPE = Shapes.or(EAST_FACING_PART, E1_PART, E2_PART);
	private static final VoxelShape SOUTH_FACING_SHAPE = Shapes.or(SOUTH_FACING_PART, S1_PART, S2_PART);
	private static final VoxelShape WEST_FACING_SHAPE = Shapes.or(WEST_FACING_PART, W1_PART, W2_PART);

	// outer corners
	private static final VoxelShape TOP_LEFT_OUTER_SHAPE = Block.box(10, 0, 10, 16, 16, 16);
	private static final VoxelShape TOP_RIGHT_OUTER_SHAPE = Block.box(0, 0, 10, 6, 16, 16);

	private static final VoxelShape BOTTOM_LEFT_OUTER_SHAPE = Block.box(10, 0, 0, 16, 16, 6);
	private static final VoxelShape BOTTOM_RIGHT_OUTER_SHAPE = Block.box(0, 0, 0, 6, 16, 6);

	// inner corners
	private static final VoxelShape TOP_LEFT_INNER_SHAPE = Shapes.or(Block.box(0, 0, 0, 14, 16, 6),
			S2_PART, Block.box(0, 0, 6, 6, 16, 14), // med block
			Block.box(4, 0, 12, 8, 16, 16)); // small block

	private static final VoxelShape TOP_RIGHT_INNER_SHAPE = Shapes.or(Block.box(2, 0, 0, 16, 16, 6),
			S1_PART, Block.box(10, 0, 6, 16, 16, 14), // med block
			Block.box(8, 0, 12, 12, 16, 16)); // small block

	private static final VoxelShape BOTTOM_LEFT_INNER_SHAPE = Shapes.or(
			Block.box(0, 0, 10, 14, 16, 16), N2_PART, Block.box(0, 0, 2, 6, 16, 10), // med
			Block.box(4, 0, 0, 8, 16, 4)); // small

	private static final VoxelShape BOTTOM_RIGHT_INNER_SHAPE = Shapes.or(
			Block.box(2, 0, 10, 16, 16, 16), N1_PART, Block.box(10, 0, 2, 16, 16, 10),
			Block.box(8, 0, 0, 12, 16, 4));

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
	public FlutedFacadeBlock(Properties properties) {
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
		return block instanceof FlutedFacadeBlock;
	}
}
