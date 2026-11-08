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

import mod.gottsch.forge.dungeonblocks.core.entity.SeatEntity;
import mod.gottsch.forge.dungeonblocks.core.state.properties.BenchPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * A stone bench: a dressed seat slab on a stone support at each end, a carved border round its top
 * and a recessed panel on each support's face. Benches of the same stone set side by side along
 * their AXIS join into one long bench: each block's PART comes from its neighbours, the supports
 * stay at the two ends and the carved border runs round the whole run.
 *
 * <p>It runs across the way the player faces when placing it, so they sit facing the way they
 * looked; placed against the side of a bench, it carries that bench on. Use one with an empty hand
 * to sit on it ({@link SeatEntity}); each block seats one. Sneak to stand. Waterloggable.
 *
 * <p>Textures and models from tools/gen_stone_benches.py, dressed from the mod's large bricks.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class StoneBenchBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<BenchPart> PART = EnumProperty.create("part", BenchPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** The seat's top, in blocks: where a sitter sits (gen_stone_benches.py SEAT_Y1). */
    public static final double SEAT_HEIGHT = 9 / 16.0;

    private static final Map<BenchPart, VoxelShape> X_SHAPES = new EnumMap<>(BenchPart.class);
    private static final Map<BenchPart, VoxelShape> Z_SHAPES = new EnumMap<>(BenchPart.class);

    static {
        // the model's boxes, along x: the seat 1px past a free end's support, supports 3px thick
        for (BenchPart part : BenchPart.values()) {
            double x0 = part.hasNegativeSupport() ? 1 : 0;
            double x1 = part.hasPositiveSupport() ? 15 : 16;
            VoxelShape x = box(x0, 6, 2, x1, 9, 14);
            VoxelShape z = box(2, 6, x0, 14, 9, x1);
            if (part.hasNegativeSupport()) {
                x = Shapes.or(x, box(2, 0, 3, 5, 6, 13));
                z = Shapes.or(z, box(3, 0, 2, 13, 6, 5));
            }
            if (part.hasPositiveSupport()) {
                x = Shapes.or(x, box(11, 0, 3, 14, 6, 13));
                z = Shapes.or(z, box(3, 0, 11, 13, 6, 14));
            }
            X_SHAPES.put(part, x);
            Z_SHAPES.put(part, z);
        }
    }

    public StoneBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(PART, BenchPart.SINGLE)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PART, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(AXIS) == Direction.Axis.X ? X_SHAPES : Z_SHAPES).get(state.getValue(PART));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        // against a bench's end: carry it on; otherwise across the way the player faces
        Direction.Axis axis = context.getHorizontalDirection().getClockWise().getAxis();
        if (face.getAxis().isHorizontal()) {
            BlockState against = level.getBlockState(pos.relative(face.getOpposite()));
            if (against.is(this) && against.getValue(AXIS) == face.getAxis()) {
                axis = face.getAxis();
            }
        }
        return defaultBlockState().setValue(AXIS, axis).setValue(PART, part(level, pos, axis))
                .setValue(WATERLOGGED, Waterlogging.placedInWater(context));
    }

    /** This block's part of a run, from the benches of the same stone and axis either side of it. */
    private BenchPart part(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        return BenchPart.of(joins(level, pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE)), axis),
                joins(level, pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)), axis));
    }

    private boolean joins(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        BlockState state = level.getBlockState(pos);
        return state.is(this) && state.getValue(AXIS) == axis;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        Waterlogging.tickWater(state, level, pos);
        if (direction.getAxis() == state.getValue(AXIS)) {
            state = state.setValue(PART, part(level, pos, state.getValue(AXIS)));
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    /** An empty main hand sits down on this block of the bench, if no one is sitting there. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // 1.21 runs this for whatever the main hand holds, so check that it is empty
        if (!player.getMainHandItem().isEmpty() || player.isShiftKeyDown()
                || player.isPassenger()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return SeatEntity.sit(level, pos, player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    /** A rotated structure turns the bench, and swaps its ends where the run now points the other way. */
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return turned(state, rotation.rotate(Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.POSITIVE)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return turned(state, mirror.mirror(Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.POSITIVE)));
    }

    /** The bench with its positive end now toward `positive`. */
    private static BlockState turned(BlockState state, Direction positive) {
        BenchPart part = state.getValue(PART);
        if (positive.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            part = part == BenchPart.START ? BenchPart.END : part == BenchPart.END ? BenchPart.START : part;
        }
        return state.setValue(AXIS, positive.getAxis()).setValue(PART, part);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
