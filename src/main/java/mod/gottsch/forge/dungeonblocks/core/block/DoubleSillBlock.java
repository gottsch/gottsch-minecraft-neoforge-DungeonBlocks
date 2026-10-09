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
public class DoubleSillBlock extends WaterloggedNonCubeFacingBlock {
	
	// The model (tools/gen_obj_models.py, double_sill): 12px walls on the whole footprint and a low
	// gable to a ridge at the top centre. What it hides of its neighbours is the 12px body; the ridge
	// runs across the facing.
	private static final VoxelShape NORTH_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
	private static final VoxelShape EAST_WEST_AABB = NORTH_SOUTH_AABB;
	
	public DoubleSillBlock(Properties properties) {
		super(properties);
	}

	/** A full cube to hit, stand on and select: simpler than the gable to handle, and to build with. */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	/**
	 * What it hides of its neighbours stays the stepped shape: as a full cube it would hide the faces
	 * of blocks beside the gable, and leave holes to see through above its walls.
	 */
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
		Direction direction = state.getValue(FACING);

		return switch (direction) {
		case NORTH, SOUTH -> NORTH_SOUTH_AABB;
		case EAST, WEST -> EAST_WEST_AABB;
		default -> NORTH_SOUTH_AABB;
		};
	}
}
