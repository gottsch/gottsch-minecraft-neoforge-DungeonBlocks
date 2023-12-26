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
import net.minecraft.core.Direction;
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
public class SillBlock extends WaterloggedNonCubeFacingBlock {
	
	// Voxels are like the bounding boxes (AABBs) NF= North Facing, SF = South Facing, etc
	private static final VoxelShape MAIN_PART = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
	private static final VoxelShape NF_TOP_PART = Block.box(0.0D, 12.0D, 8.0D, 16.0D, 16.0D, 16.0D);	
	private static final VoxelShape EF_TOP_PART = Block.box(0.0D, 12.0D, 0.0D, 8.0D, 16.0D, 16.0D);
	private static final VoxelShape SF_TOP_PART = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	private static final VoxelShape WF_TOP_PART = Block.box(8.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	private static final VoxelShape NORTH_FACING_AABB = Shapes.or(MAIN_PART, NF_TOP_PART);
	private static final VoxelShape EAST_FACING_AABB = Shapes.or(MAIN_PART, EF_TOP_PART);
	private static final VoxelShape SOUTH_FACING_AABB = Shapes.or(MAIN_PART, SF_TOP_PART);
	private static final VoxelShape WEST_FACING_AABB = Shapes.or(MAIN_PART, WF_TOP_PART);
	
	/**
	 * 
	 * @param properties
	 */
	public SillBlock(Properties properties) {
		super(properties);
	}

	/**
	 * 
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);

		switch (direction) {
		case NORTH:
		default:
			return NORTH_FACING_AABB;
		case EAST:
			return EAST_FACING_AABB;
		case SOUTH:
			return SOUTH_FACING_AABB;
		case WEST:
			return WEST_FACING_AABB;
		}
	}
}
