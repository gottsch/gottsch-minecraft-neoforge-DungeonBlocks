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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * TODO Ring block actually is a FACING + BASED block as it is not symmetrical and needs base and facing
 * @author Mark Gottschling on Oct 27, 2023
 *
 */
public class WallRingBlock extends WaterloggedNonCubeFacingBlock {

	// size = 6x9x6
	private static final VoxelShape UP_SHAPE = Shapes.or(
			Block.box(3.5, 0, 3, 12.5, 2, 12),
			Block.box(6.5D, 2, 6, 9.5, 9, 9)
	);
	private static final VoxelShape DOWN_SHAPE = Shapes.or(
			Block.box(3.5, 14, 4, 12.5, 16, 13),
			Block.box(6.5D, 7, 7, 9.5, 14, 10)
	);
	private static final VoxelShape NORTH_SHAPE = Shapes.or(
			Block.box(3.5D, 3D, 14D, 12.5D, 12D, 16D),
			Block.box(6.5D, 6D, 7D, 9.5D, 9, 14));
	private static final VoxelShape EAST_SHAPE = Shapes.or(
			Block.box(0, 3, 3.5D, 2, 12, 12.5D),
			Block.box(2, 6D, 6.5D, 9, 9, 9.5));

	private static final VoxelShape SOUTH_SHAPE = Shapes.or(
			Block.box(3.5D, 3D, 0D, 12.5D, 12D, 2D),
			Block.box(6.5D, 6D, 2D, 9.5D, 9, 9));
	;
	private static final VoxelShape WEST_SHAPE = Shapes.or(
			Block.box(14D, 3D, 3.5D, 16D, 12D, 12.5D),
			Block.box(7, 6, 6.5D, 14, 9, 9.5));

	public WallRingBlock(Properties properties) {
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

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockPos = context.getClickedPos();
		FluidState fluidState = context.getLevel().getFluidState(blockPos);

		BlockState blockState = this.defaultBlockState().setValue(FACING,
				context.getNearestLookingDirection().getOpposite());
		blockState.setValue(WATERLOGGED, Boolean.valueOf(fluidState.getType() == Fluids.WATER));

		return blockState;
	}
}
