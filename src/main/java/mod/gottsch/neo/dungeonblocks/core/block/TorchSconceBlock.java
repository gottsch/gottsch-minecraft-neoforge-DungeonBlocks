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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mark Gottschling on Oct 29, 2023
 *
 */
public class TorchSconceBlock extends NonCubeFacingBlock {
	// TODO remove waterlogged and look at torch code to pop if submerged
	// OR make another model without torch, no light, that this turns into if waterlogged

	// TODO update sizes
	private static final VoxelShape NORTH_FACING_SHAPE = Block
			.box(5, 3, 8, 11, 13, 16);
	private static final VoxelShape EAST_FACING_SHAPE = Block
			.box(0, 3, 5, 8, 13, 11);
	private static final VoxelShape SOUTH_FACING_SHAPE = Block
			.box(5, 3, 0, 11, 13, 8);
	private static final VoxelShape WEST_FACING_SHAPE = Block
			.box(8, 3, 5, 16, 13, 11);

	/**
	 *
	 * @param properties
	 */
	public TorchSconceBlock(Properties properties) {
		super(properties);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
		return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return blockstate.isFaceSturdy(level, blockpos, direction);
	}

	/**
	 *
	 * @param state
	 * @param level
	 * @param pos
	 * @param rand
	 */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		Direction direction = state.getValue(FACING);
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.5D;
		double d2 = (double) pos.getZ() + 0.5D;
		double d3 = 0.40D; // y offset
		double d4 = 0.05D; // horizontal offset middle

		if (direction.getAxis().isHorizontal()) {
			Direction directionFacing = direction.getOpposite();
			// middle
			level.addParticle(ParticleTypes.SMOKE,
					d0 + d4 * (double) directionFacing.getStepX(), d1 + d3,
					d2 + d4 * (double) directionFacing.getStepZ(), 0.0D, 0.0D,
					0.0D);
			level.addParticle(ParticleTypes.FLAME,
					d0 + d4 * (double) directionFacing.getStepX(), d1 + d3,
					d2 + d4 * (double) directionFacing.getStepZ(), 0.0D, 0.0D,
					0.0D);

		}
	}

	/**
	 * 
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);

		switch (direction) {
		case NORTH :
		default :
			return NORTH_FACING_SHAPE;
		case EAST :
			return EAST_FACING_SHAPE;
		case SOUTH :
			return SOUTH_FACING_SHAPE;
		case WEST :
			return WEST_FACING_SHAPE;
		}
	}
}
