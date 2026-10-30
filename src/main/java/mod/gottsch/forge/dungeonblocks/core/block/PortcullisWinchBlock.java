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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Raises and lowers a portcullis. It is a TOGGLE: each redstone pulse - a button press, or a lever
 * switched on - reverses the gate, and so does right-clicking the winch by hand. RAISING records
 * which way the gate was last sent, since nothing else could; POWERED is kept only to see the
 * rising edge of a signal. Switching a lever off therefore does nothing, and a button press is not
 * undone when the button pops back out - which a held-power control, like an iron door's, would do
 * to a gate that takes longer to move than a button stays pressed.
 *
 * <p>HOW A GATE IS BUILT: the winch sits anywhere directly above the gate, within {@link #REACH}
 * blocks - typically on the floor of a room over the gatehouse. The gate rises into the empty slot
 * above it and stops at the first block in its way (that floor, or the winch itself). Nothing
 * links the two but position: each step the winch looks straight down, through whatever is in
 * the way, for the first portcullis block, and takes every portcullis block connected to it in
 * the same plane as the gate. That keeps both blocks stateless - no
 * block entity, nothing ticking - and it is why the control has to be a separate block at all:
 * redstone wired to the gate itself would be left behind as the gate moved away from it.
 *
 * <p>THE MOVE: one row every {@link #STEP_TICKS} ticks, on scheduled ticks, until the gate is
 * blocked. Rising, every column needs empty space above it; lowering, every column needs empty
 * space below it and no living thing standing there - a portcullis stops short rather than
 * crushing whatever is under it. Only air counts as empty, so the gate never overwrites a block.
 */
public class PortcullisWinchBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty RAISING = BooleanProperty.create("raising");

    /** Ticks between rows: a gate three blocks tall opens in about a second and a half. */
    public static final int STEP_TICKS = 8;
    /** How far below the winch it will look for the gate. */
    public static final int REACH = 24;
    /** Largest gate it will move, so a mistake cannot drag half a castle wall. */
    public static final int MAX_GATE = 256;

    private static final VoxelShape X_SHAPE = Block.box(0, 0, 3, 16, 13.5, 13);
    private static final VoxelShape Z_SHAPE = Block.box(3, 0, 0, 13, 13.5, 16);

    public PortcullisWinchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(POWERED, false)
                .setValue(RAISING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POWERED, RAISING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(AXIS, context.getHorizontalDirection().getClockWise().getAxis())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (level.isClientSide) {
            return;
        }
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            // a rising edge reverses the gate; a falling edge only records that the signal is off
            BlockState next = state.setValue(POWERED, powered);
            if (powered) {
                next = next.setValue(RAISING, !state.getValue(RAISING));
                level.scheduleTick(pos, this, 1);
            }
            level.setBlock(pos, next, 2);
        }
    }

    /** Cranking it by hand: the same toggle as a redstone pulse. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(RAISING, !state.getValue(RAISING)), 2);
            level.playSound(null, pos, SoundEvents.CHAIN_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.scheduleTick(pos, this, 1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Set<BlockPos> gate = findGate(level, pos);
        if (gate.isEmpty()) {
            return;
        }
        Direction dir = state.getValue(RAISING) ? Direction.UP : Direction.DOWN;
        if (canMove(level, gate, dir)) {
            move(level, gate, dir);
            level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 0.6F);
            level.scheduleTick(pos, this, STEP_TICKS);
        } else if (dir == Direction.DOWN) {
            // the gate has come to rest - a heavy thud from its lowest point
            BlockPos low = gate.stream().min(Comparator.comparingInt(BlockPos::getY)).orElse(pos);
            level.playSound(null, low, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 0.5F);
        }
    }

    /**
     * The gate below this winch: the first portcullis straight down through air, and every
     * portcullis connected to it in the gate's plane. Empty if there is none within reach.
     */
    static Set<BlockPos> findGate(Level level, BlockPos winch) {
        // through anything, not just air: the usual build puts the winch on the floor of a room
        // above the gatehouse, with that floor between it and the gate
        BlockPos.MutableBlockPos cursor = winch.mutable();
        for (int i = 0; i < REACH; i++) {
            cursor.move(Direction.DOWN);
            BlockState found = level.getBlockState(cursor);
            if (found.getBlock() instanceof PortcullisBlock) {
                return connected(level, cursor.immutable(), found.getValue(PortcullisBlock.AXIS));
            }
        }
        return Set.of();
    }

    private static Set<BlockPos> connected(Level level, BlockPos start, Direction.Axis axis) {
        Direction along = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        List<Direction> steps = List.of(Direction.UP, Direction.DOWN, along, along.getOpposite());
        Set<BlockPos> gate = new HashSet<>();
        Deque<BlockPos> todo = new ArrayDeque<>();
        todo.add(start);
        gate.add(start);
        while (!todo.isEmpty() && gate.size() < MAX_GATE) {
            BlockPos p = todo.poll();
            for (Direction d : steps) {
                BlockPos n = p.relative(d);
                BlockState s = level.getBlockState(n);
                if (!gate.contains(n) && s.getBlock() instanceof PortcullisBlock && s.getValue(PortcullisBlock.AXIS) == axis) {
                    gate.add(n);
                    todo.add(n);
                }
            }
        }
        return gate;
    }

    /** Every cell moving into a space not already part of the gate needs that space empty. */
    private static boolean canMove(Level level, Set<BlockPos> gate, Direction dir) {
        for (BlockPos p : gate) {
            BlockPos to = p.relative(dir);
            if (gate.contains(to)) {
                continue;
            }
            if (!level.getBlockState(to).isAir()) {
                return false;
            }
            if (dir == Direction.DOWN && !level.getEntitiesOfClass(LivingEntity.class, new AABB(to)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void move(Level level, Set<BlockPos> gate, Direction dir) {
        Map<BlockPos, BlockState> moved = new HashMap<>();
        for (BlockPos p : gate) {
            moved.put(p.relative(dir), level.getBlockState(p));
        }
        // clear the cells the gate is leaving, then fill the ones it arrives in. Flag 3 so
        // neighbours update: that is what re-derives BOTTOM on the new lowest row.
        for (BlockPos p : gate) {
            if (!moved.containsKey(p)) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        moved.forEach((p, s) -> level.setBlock(p, s, 3));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            return state.setValue(AXIS, state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }
        return state;
    }
}
