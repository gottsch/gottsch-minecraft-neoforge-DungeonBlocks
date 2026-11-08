/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A leaded stained glass panel, 2px thick in the middle of the block, that builds a lancet window
 * when stacked. Each block works out its own PART from the windows above and below it on the same
 * AXIS: the arch on the top one, the sill on the bottom one, plain quarries between, and both on a
 * window one block tall. Intact and broken windows join each other, so a window can mix them.
 *
 * @author Mark Gottschling on 11/02/2026
 */
public class StainedGlassWindowBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // AXIS is the way the panel runs
    private static final VoxelShape X_AABB = Block.box(0, 0, 7, 16, 16, 9);
    private static final VoxelShape Z_AABB = Block.box(7, 0, 0, 9, 16, 16);

    public StainedGlassWindowBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(PART, Part.SINGLE).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PART, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_AABB : Z_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // the panel faces the player, so it runs across the way they look
        Direction.Axis axis = context.getHorizontalDirection().getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
        BlockState state = this.defaultBlockState().setValue(AXIS, axis)
                .setValue(WATERLOGGED, Waterlogging.placedInWater(context));
        return state.setValue(PART, part(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        Waterlogging.tickWater(state, level, pos);
        if (direction.getAxis() == Direction.Axis.Y) {
            return state.setValue(PART, part(state, level, pos));
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    private static Part part(BlockState state, BlockGetter level, BlockPos pos) {
        boolean above = joins(state, level.getBlockState(pos.above()));
        boolean below = joins(state, level.getBlockState(pos.below()));
        return above ? (below ? Part.MIDDLE : Part.BOTTOM) : (below ? Part.TOP : Part.SINGLE);
    }

    private static boolean joins(BlockState state, BlockState other) {
        return other.getBlock() instanceof StainedGlassWindowBlock && other.getValue(AXIS) == state.getValue(AXIS);
    }

    /**
     * Hides the edge between two panels of one window, which would otherwise show through the glass.
     */
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction direction) {
        return (adjacent.getBlock() instanceof StainedGlassWindowBlock && adjacent.getValue(AXIS) == state.getValue(AXIS)
                && direction.getAxis() != (state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))
                || super.skipRendering(state, adjacent, direction);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.setValue(AXIS,
                    state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
            default -> state;
        };
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }

    public enum Part implements StringRepresentable {
        SINGLE("single"), BOTTOM("bottom"), MIDDLE("middle"), TOP("top");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
