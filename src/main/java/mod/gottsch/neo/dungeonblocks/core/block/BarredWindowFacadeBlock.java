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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class BarredWindowFacadeBlock extends WaterloggedNonCubeFacingBlock {

	private static final VoxelShape UP_SHAPE = Block.box(0.0D, 0D, 0.0D, 15.99D, 4D, 15.99D);
	private static final VoxelShape DOWN_SHAPE = Block.box(0.0D, 4D, 0.0D, 15.99D, 16D, 15.99D);
	private static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 12.0D, 15.99D, 16.0D, 15.99D);
	private static final VoxelShape EAST_SHAPE = Block.box(0D, 0.0D, 0.0D, 4D, 16.0D, 15.99D);
	private static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 0D, 15.99D, 16.0D, 4D);
	private static final VoxelShape WEST_SHAPE = Block.box(12D, 0.0D, 0.0D, 15.99D, 16.0D, 15.99D);

	public BarredWindowFacadeBlock(Properties properties) {
		super(properties);
	}

	/**
	 * 
	 */
	@Override
	public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);

		return switch (direction) {
			case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case EAST -> EAST_SHAPE;
			case WEST -> WEST_SHAPE;
			default -> DOWN_SHAPE;
		};
	}
}
