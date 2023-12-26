/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
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

/**
 * @author Mark Gottschling on Nov 13, 2023
 *
 */
public class PlateBracketBlock extends WaterloggedNonCubeFacingBlock {
    private static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 2, 16D);
    private static final VoxelShape DOWN_SHAPE = Block.box(0D, 14, 0, 16, 16, 16D);

    private static final VoxelShape NORTH_SHAPE = Block.box(0D, 0D, 14D, 16D, 16D, 16D);
    private static final VoxelShape EAST_SHAPE = Block.box(0, 0D, 0D, 2, 16D, 16D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0D, 0D, 0D, 16D, 16D, 2D);
    private static final VoxelShape WEST_SHAPE = Block.box(14D, 0D, 0D, 16, 16D, 16D);


    /**
     * @param properties
     */
    public PlateBracketBlock(Properties properties) {
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
