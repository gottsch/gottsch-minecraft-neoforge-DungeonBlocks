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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * An altar three blocks wide: LEFT, MIDDLE and RIGHT as seen from in front. {@link #FACING} is the
 * way its front faces, toward whoever stands before it, so the LEFT part sits on the
 * {@code getClockWise()} side of the middle and the RIGHT part on the counter-clockwise side.
 *
 * <p><b>No part ever checks its neighbours.</b> A two-block sarcophagus turns a half to air in
 * {@code updateShape} when its partner is missing, and in worldgen that check runs before the
 * partner's chunk is written, so a template tomb split by a chunk seam comes out as one half. The
 * altar keeps no such check: a structure can write its three parts in any order, across any seam,
 * and each one stands. The parts still behave as one object for a player: placing one places all
 * three ({@link #getStateForPlacement} refuses unless both side cells are free), and a player
 * breaking any part, or an explosion taking one, clears the other two without drops
 * ({@link #playerWillDestroy}, {@link #onBlockExploded}). Every part's loot table gives the altar,
 * so whichever part is broken drops it, once. Pistons cannot split it ({@code PushReaction.BLOCK},
 * set in {@code ModBlocks}).
 *
 * <p>Models are templates (blockbench/altar_*.bbmodel), textured per material in datagen. The top
 * is a whole face at every part, so candles stand on it.
 *
 * @author Mark Gottschling on 10/24/2026
 */
public class AltarBlock extends WaterloggedNonCubeFacingBlock {
    public enum Part implements StringRepresentable {
        LEFT, MIDDLE, RIGHT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    /**
     * The shape, per part and facing: the model's own boxes (gen_bbmodels.py {@code altar}), so the
     * outline and collision follow the plinth, body, moulding and top exactly, ends included. Keep
     * the two in step if the model changes.
     */
    private static final Map<Part, Map<Direction, VoxelShape>> SHAPES = new EnumMap<>(Part.class);

    static {
        for (Part part : Part.values()) {
            Map<Direction, VoxelShape> byFacing = new EnumMap<>(Direction.class);
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                byFacing.put(facing, shape(part, facing));
            }
            SHAPES.put(part, byFacing);
        }
    }

    /** One part's boxes as authored (facing north, its left part's open end east), turned to `facing`. */
    private static VoxelShape shape(Part part, Direction facing) {
        boolean left = part == Part.LEFT, right = part == Part.RIGHT;
        double[][] boxes = {
                {right ? 1 : 0, 0, 1, left ? 15 : 16, 2, 15},     // plinth
                {right ? 2 : 0, 2, 3, left ? 14 : 16, 12, 13},    // body
                {right ? 1 : 0, 12, 2, left ? 15 : 16, 13, 14},   // moulding
                {0, 13, 0, 16, 16, 16},                           // top
                part == Part.MIDDLE ? new double[] {3, 4, 2, 13, 10, 3}    // carved panel
                        : right ? new double[] {2, 2, 2, 5, 12, 3}           // pilaster
                        : new double[] {11, 2, 2, 14, 12, 3}};
        VoxelShape shape = Shapes.empty();
        for (double[] b : boxes) {
            shape = Shapes.or(shape, turned(b, facing));
        }
        return shape.optimize();
    }

    /**
     * A north-authored box turned as the blockstate turns the model: clockwise seen from above, a
     * quarter turn per step from north, (x, z) to (16 - z, x).
     */
    private static VoxelShape turned(double[] b, Direction facing) {
        double x0 = b[0], z0 = b[2], x1 = b[3], z1 = b[5];
        // 2D data values run south, west, north, east: clockwise, one step per quarter turn
        int turns = Math.floorMod(facing.get2DDataValue() - Direction.NORTH.get2DDataValue(), 4);
        for (int i = 0; i < turns; i++) {
            double nx0 = 16 - z1, nx1 = 16 - z0, nz0 = x0, nz1 = x1;
            x0 = nx0; x1 = nx1; z0 = nz0; z1 = nz1;
        }
        return Block.box(x0, b[1], z0, x1, b[4], z1);
    }

    public AltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.MIDDLE).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(PART)).getOrDefault(state.getValue(FACING), SHAPES.get(state.getValue(PART)).get(Direction.NORTH));
    }

    /** The clicked cell takes the middle, facing the player; null, so nothing is placed, unless both sides are free. */
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState middle = super.getStateForPlacement(context).setValue(PART, Part.MIDDLE);
        Level level = context.getLevel();
        for (Part side : new Part[] {Part.LEFT, Part.RIGHT}) {
            BlockPos pos = context.getClickedPos().relative(fromMiddle(middle.getValue(FACING), side));
            if (!level.getBlockState(pos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(pos)) {
                return null;
            }
        }
        return middle;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            for (Part side : new Part[] {Part.LEFT, Part.RIGHT}) {
                BlockPos sidePos = pos.relative(fromMiddle(state.getValue(FACING), side));
                level.setBlock(sidePos, state.setValue(PART, side)
                        .setValue(WATERLOGGED, level.getFluidState(sidePos).getType() == Fluids.WATER), 3);
            }
        }
    }

    /** A player breaking one part takes the altar down whole; the broken part's loot is the one drop. */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            clearOtherParts(level, pos, state, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * An explosion reads each block it blows up afresh, so clearing the other parts here, before it
     * reaches them, keeps one blast from dropping three altars.
     */
    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            clearOtherParts(level, pos, state, null);
        }
        super.onBlockExploded(state, level, pos, explosion);
    }

    /**
     * Air, with no drops (flag 35), in the cells this part's siblings should hold - but only where
     * one actually stands, facing the same way, so an altar built part by part beside another is
     * never taken with it.
     */
    private void clearOtherParts(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
        Direction facing = state.getValue(FACING);
        Part self = state.getValue(PART);
        BlockPos middle = self == Part.MIDDLE ? pos : pos.relative(fromMiddle(facing, self).getOpposite());
        for (Part part : Part.values()) {
            if (part == self) {
                continue;
            }
            BlockPos other = middle.relative(fromMiddle(facing, part), part == Part.MIDDLE ? 0 : 1);
            BlockState otherState = level.getBlockState(other);
            if (otherState.is(this) && otherState.getValue(FACING) == facing && otherState.getValue(PART) == part) {
                BlockState empty = otherState.getValue(WATERLOGGED)
                        ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(other, empty, 35);
                level.levelEvent(player, 2001, other, Block.getId(otherState));
            }
        }
    }

    /** The direction from the middle toward `part`; the middle's own is unused (distance 0). */
    static Direction fromMiddle(Direction facing, Part part) {
        return part == Part.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /** A mirror turns the altar's handedness: left and right swap, whichever axis is mirrored. */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        Part part = state.getValue(PART);
        Part swapped = part == Part.LEFT ? Part.RIGHT : part == Part.RIGHT ? Part.LEFT : part;
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING))).setValue(PART, swapped);
    }
}
