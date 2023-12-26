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
public class PillarBaseBlock extends WaterloggedNonCubeBasedBlock {

	// Voxels are like the bounding boxes (AABBs) NF= North Facing, SF = South
	// Facing, etc
	private static final VoxelShape NORTH_AABB = Shapes.or(
			Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D), // "base"
			Block.box(1.0D, 1.0D, 6.0D, 15.0D, 15.0D, 8.0D), // "middle base"
			Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 6.0D), // "middle top"
			Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 2.0D) // "top"
	);

	private static final VoxelShape SOUTH_AABB = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D), // "base"
			Block.box(1.0D, 1.0D, 8.0D, 15.0D, 15.0D, 10.0D), // "middle base"
			Block.box(2.0D, 2.0D, 10.0D, 14.0D, 14.0D, 14.0D), // "middle top"
			Block.box(1.0D, 1.0D, 14.0D, 15.0D, 15.0D, 16.0D) // "top"

	);

	private static final VoxelShape EAST_AABB = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D), // "base"
			Block.box(8.0D, 1.0D, 1.0D, 10.0D, 15.0D, 15.0D), // "middle base"
			Block.box(10.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D), // "middle top"
			Block.box(14.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D) // "top"
	);

	private static final VoxelShape WEST_AABB = Shapes.or(
			Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), // "base"
			Block.box(6.0D, 1.0D, 1.0D, 8.0D, 15.0D, 15.0D), // "middle base"
			Block.box(2.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D), // "middle top"
			Block.box(0.0D, 1.0D, 1.0D, 2.0D, 15.0D, 15.0D) // "top"
	);

	private static final VoxelShape UP_AABB = Shapes.or(
			Block.box(1.0D, 14.0D, 1.0D, 15.0D, 16.0D, 15.0D),
			Block.box(2.0D, 10.0D, 2.0D, 14.0D, 14.0D, 14.0D),
			Block.box(1.0D, 8.0D, 1.0D, 15.0D, 10.0D, 15.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));

	private static final VoxelShape DOWN_AABB = Shapes.or( // shape is inverted
			Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D), // "base"
			Block.box(1.0D, 6.0D, 1.0D, 15.0D, 8.0D, 15.0D), // "middle base"
			Block.box(2.0D, 2.0D, 2.0D, 14.0D, 6.0D, 14.0D), // "middle top"
			Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D) // "top"
	);

	public PillarBaseBlock(Properties properties) {
		super(properties);
	}

	/**
	 * 
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(BASE);

		return switch (direction) {
		case UP -> UP_AABB;
		case DOWN -> DOWN_AABB;
		case NORTH -> NORTH_AABB;
		case SOUTH -> SOUTH_AABB;
		case EAST -> EAST_AABB;
		case WEST -> WEST_AABB;
		default -> DOWN_AABB;
		};
	}
}
