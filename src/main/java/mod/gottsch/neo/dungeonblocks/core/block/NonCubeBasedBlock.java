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

import mod.gottsch.neo.gottschcore.block.BasedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 
 * @author Mark Gottschling on Oct 28, 2023
 *
 */
public class NonCubeBasedBlock extends BasedBlock {
	private static final VoxelShape DEFAULT_SHAPE = Block.box(0.0D, 0D, 0.0D, 15.99D, 15.99D, 15.99D);


	/**
	 *
	 * @param properties
	 */
	public NonCubeBasedBlock(Properties properties) {
		super(properties);
	}

	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(BASE);

		return switch (direction) {
			case UP -> getUpShape();
			case NORTH -> getNorthShape();
			case SOUTH -> getSouthShape();
			case EAST -> getEastShape();
			case WEST -> getWestShape();
			default -> getDownShape();
		};
	}

	public VoxelShape getUpShape() {return DEFAULT_SHAPE; }
	public VoxelShape getNorthShape() {return DEFAULT_SHAPE; }
	public VoxelShape getSouthShape() {return DEFAULT_SHAPE; }
	public VoxelShape getEastShape() {return DEFAULT_SHAPE; }
	public VoxelShape getWestShape() {return DEFAULT_SHAPE; }
	public VoxelShape getDownShape() {return DEFAULT_SHAPE; }

	/**
	 * This method returns the state of the block so that the correct entry in the
	 * blockstate.json file can be selected and the corresponding block model
	 * rendered.
	 */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockPos = context.getClickedPos();
		FluidState fluidState = context.getLevel().getFluidState(blockPos);

		BlockState blockState = this.defaultBlockState().setValue(BASE, context.getClickedFace());

		return blockState;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos pos, BlockPos p_56930_) {
		return super.updateShape(state, direction, newState, levelAccessor, pos, p_56930_);
	}
	
	@Override
	public FluidState getFluidState(BlockState blockState) {
		return super.getFluidState(blockState);
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
}
