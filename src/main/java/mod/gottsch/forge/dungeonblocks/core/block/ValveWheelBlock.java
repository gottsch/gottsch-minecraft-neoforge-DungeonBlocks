/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2020 Mark Gottschling (gottsch)
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 *
 * @author Mark Gottschling on Mar 5, 2020
 *
 */
public class ValveWheelBlock extends WaterloggedNonCubeFacingBlock {

   // Shapes names are based on the FACING direction. ex. NORTH_SHAPE faces north.
   private static final VoxelShape NORTH_FACING_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_FACING_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
   private static final VoxelShape SOUTH_FACING_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   private static final VoxelShape WEST_FACING_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);


   public ValveWheelBlock(Properties properties) {
      super(properties);
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

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      Direction direction = state.getValue(FACING);

      switch (direction) {
         case NORTH:
         default:
            return NORTH_FACING_SHAPE;
         case EAST:
            return EAST_FACING_SHAPE;
         case SOUTH:
            return SOUTH_FACING_SHAPE;
         case WEST:
            return WEST_FACING_SHAPE;
      }
   }
}
