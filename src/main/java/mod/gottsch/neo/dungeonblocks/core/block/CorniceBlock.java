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
public class CorniceBlock extends FacadeShapeBlock {

	// Voxels are like the bounding boxes (AABBs) NF= North Facing, SF = South
	// Facing, etc
	private static final VoxelShape NORTH_FACING_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 8.0D, 16.0D, 12.0D, 16.0D),
			Block.box(0.0D, 12.0D, 3.0D, 16.0D, 16.0D, 16.0D));

	private static final VoxelShape WEST_FACING_SHAPE = Shapes.or(
			Block.box(8.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
			Block.box(3.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D));

	private static final VoxelShape SOUTH_FACING_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 8.0D),
			Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 13.0D));

	private static final VoxelShape EAST_FACING_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 8.0D, 12.0D, 16.0D),
			Block.box(0.0D, 12.0D, 0.0D, 13.0D, 16.0D, 16.0D));

	// outer corners
	private static final VoxelShape TOP_LEFT_OUTER_SHAPE = Shapes.or(
			Block.box(8.0D, 0D, 8.0D, 16.0D, 12.0D, 16.0D),
			Block.box(3.0D, 12D, 3.0D, 16.0D, 16.0D, 16.0D));
	private static final VoxelShape TOP_RIGHT_OUTER_SHAPE = Shapes.or(
			Block.box(0D, 0D, 8.0D, 8.0D, 12.0D, 16.0D),
			Block.box(0D, 12D, 3.0D, 13.0D, 16.0D, 16.0D));

	private static final VoxelShape BOTTOM_LEFT_OUTER_SHAPE = Shapes.or(
			Block.box(8.0D, 0D, 0D, 16.0D, 12.0D, 8.0D),
			Block.box(3D, 12D, 0D, 16.0D, 16.0D, 13.0D));
	private static final VoxelShape BOTTOM_RIGHT_OUTER_SHAPE = Shapes.or(
			Block.box(0D, 0D, 0D, 8.0D, 12.0D, 8.0D),
			Block.box(0D, 12D, 0D, 13.0D, 16.0D, 13.0D));

	// inner corners
	private static final VoxelShape TOP_LEFT_INNER_SHAPE = Shapes.or(SOUTH_FACING_SHAPE,
			Block.box(0.0D, 0D, 8.0D, 8.0D, 12.0D, 16.0D),
			Block.box(0D, 12D, 13D, 13D, 16D, 16D));
	private static final VoxelShape TOP_RIGHT_INNER_SHAPE = Shapes.or(SOUTH_FACING_SHAPE,
			Block.box(8.0D, 0D, 8.0D, 16.0D, 12.0D, 16.0D),
			Block.box(3D, 12D, 13D, 16D, 16D, 16D));

	private static final VoxelShape BOTTOM_LEFT_INNER_SHAPE = Shapes.or(NORTH_FACING_SHAPE,
			Block.box(0.0D, 0D, 0.0D, 8.0D, 12.0D, 8.0D), Block.box(0D, 12D, 0D, 13D, 16D, 3D));
	private static final VoxelShape BOTTOM_RIGHT_INNER_SHAPE = Shapes.or(NORTH_FACING_SHAPE,
			Block.box(8.0D, 0D, 0D, 16.0D, 12.0D, 8.0D), Block.box(3D, 12D, 0D, 16D, 16D, 3D));

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
			TOP_LEFT_OUTER_SHAPE, // 8
			BOTTOM_LEFT_OUTER_SHAPE, //9

			// outer right
			TOP_RIGHT_OUTER_SHAPE, BOTTOM_RIGHT_OUTER_SHAPE };

	/**
	 * 
	 * @param properties
	 */
	public CorniceBlock(Properties properties) {
		super(properties);
	}

	/**
	 * Returns the VoxelShape (ie bounding box) of the block in the correct position.
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
		return block instanceof CorniceBlock;
	}
}
