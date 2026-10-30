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

import mod.gottsch.forge.dungeonblocks.core.state.properties.DoorSegment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A door of arbitrary height (3 or more one-block-tall segments), built and broken as a single
 * unit but represented in the world as independently-placed blocks that stay vertically in sync -
 * the same approach vanilla uses for its 2-tall {@code DoorBlock}, generalized.
 * <p>
 * Only three vertical positions ever need to be distinguished in the blockstate: {@code BOTTOM},
 * {@code TOP}, and {@code MIDDLE} for everything in between, regardless of how many interior
 * segments there are - the middle texture is expected to tile, so a 3-tall and a 4-tall (or
 * taller) door share the same {@link DoorSegment} property values and the same model set.
 * <p>
 * This intentionally does not extend vanilla's {@code DoorBlock}: that class hardwires its
 * placement, shape-sync, and survival logic to exactly two segments via {@code DoubleBlockHalf},
 * so there is nothing safe to reuse by subclassing it.
 * <p>
 * Ported from the NeoForge 1.21.1 version of this mod; only the vanilla {@code Block} override
 * modifiers (public here vs. protected there) and the interaction method name
 * ({@code use} vs. the later split {@code useWithoutItem}) differ between the two.
 *
 * @author Mark Gottschling on Jul 20, 2026
 */
public class TallDoorBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoorSegment> SEGMENT = EnumProperty.create("segment", DoorSegment.class);

    protected static final float AABB_DOOR_THICKNESS = 3.0F;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, AABB_DOOR_THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 16.0 - AABB_DOOR_THICKNESS, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(16.0 - AABB_DOOR_THICKNESS, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, AABB_DOOR_THICKNESS, 16.0, 16.0);

    private final BlockSetType type;
    private final int height;

    /**
     * @param properties block properties
     * @param type       vanilla sound/behavior set (reused purely for open/close sounds)
     * @param height     total number of one-block segments the door occupies, top to bottom; must be &gt;= 3
     */
    public TallDoorBlock(Properties properties, BlockSetType type, int height) {
        super(properties);
        if (height < 3) {
            throw new IllegalArgumentException("TallDoorBlock height must be >= 3 (use vanilla DoorBlock for a 2-tall door): " + height);
        }
        this.type = type;
        this.height = height;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.FALSE)
                .setValue(HINGE, DoorHingeSide.LEFT)
                .setValue(POWERED, Boolean.FALSE)
                .setValue(SEGMENT, DoorSegment.BOTTOM));
    }

    public BlockSetType type() {
        return this.type;
    }

    public int height() {
        return this.height;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        boolean closed = !state.getValue(OPEN);
        boolean hingeRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;

        switch (direction) {
            case SOUTH:
                return closed ? SOUTH_AABB : (hingeRight ? EAST_AABB : WEST_AABB);
            case WEST:
                return closed ? WEST_AABB : (hingeRight ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return closed ? NORTH_AABB : (hingeRight ? WEST_AABB : EAST_AABB);
            default:
                return closed ? EAST_AABB : (hingeRight ? NORTH_AABB : SOUTH_AABB);
        }
    }

    /**
     * Generalizes vanilla {@code DoorBlock}'s updateShape: any Y-direction neighbor that is part
     * of the same door stack copies FACING/OPEN/HINGE/POWERED across the seam (so a change
     * anywhere in the column propagates through every segment); any Y-direction neighbor that
     * ought to be part of the stack but isn't collapses this segment to air, and BOTTOM
     * additionally re-checks its ground support - the same cascade vanilla relies on to keep a
     * 2-tall door's halves in sync now walks the whole column one link at a time.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis() == Direction.Axis.Y) {
            DoorSegment segment = state.getValue(SEGMENT);
            boolean linksUp = direction == Direction.UP && segment != DoorSegment.TOP;
            boolean linksDown = direction == Direction.DOWN && segment != DoorSegment.BOTTOM;
            if (linksUp || linksDown) {
                boolean neighborInStack = neighborState.is(this)
                        && (linksUp ? neighborState.getValue(SEGMENT) != DoorSegment.BOTTOM : neighborState.getValue(SEGMENT) != DoorSegment.TOP);
                if (neighborInStack) {
                    return state.setValue(FACING, neighborState.getValue(FACING))
                            .setValue(OPEN, neighborState.getValue(OPEN))
                            .setValue(HINGE, neighborState.getValue(HINGE))
                            .setValue(POWERED, neighborState.getValue(POWERED));
                }
                return Blocks.AIR.defaultBlockState();
            }
        }
        if (state.getValue(SEGMENT) == DoorSegment.BOTTOM && direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state))) {
            preventDropFromRestOfStack(level, pos, state, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Breaking any one segment should drop exactly one item, not one per segment, and should
     * never leave the rest of the column floating. The broken segment drops normally through the
     * regular destroy path; this silently clears every other segment in the column (walking down
     * to BOTTOM and up to TOP) before that happens.
     */
    private void preventDropFromRestOfStack(Level level, BlockPos pos, BlockState state, Player player) {
        DoorSegment segment = state.getValue(SEGMENT);
        if (segment != DoorSegment.BOTTOM) {
            BlockPos p = pos.below();
            BlockState s = level.getBlockState(p);
            while (s.is(this)) {
                DoorSegment sAt = s.getValue(SEGMENT);
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 35);
                if (sAt == DoorSegment.BOTTOM) {
                    break;
                }
                p = p.below();
                s = level.getBlockState(p);
            }
        }
        if (segment != DoorSegment.TOP) {
            BlockPos p = pos.above();
            BlockState s = level.getBlockState(p);
            while (s.is(this)) {
                DoorSegment sAt = s.getValue(SEGMENT);
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 35);
                if (sAt == DoorSegment.TOP) {
                    break;
                }
                p = p.above();
                s = level.getBlockState(p);
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        switch (type) {
            case LAND:
            case AIR:
                return state.getValue(OPEN);
            case WATER:
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos basePos = context.getClickedPos();
        Level level = context.getLevel();
        if (basePos.getY() + this.height - 1 > level.getMaxBuildHeight() - 1) {
            return null;
        }
        for (int i = 1; i < this.height; i++) {
            if (!level.getBlockState(basePos.above(i)).canBeReplaced(context)) {
                return null;
            }
        }
        boolean powered = level.hasNeighborSignal(basePos) || level.hasNeighborSignal(basePos.above(this.height - 1));
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(HINGE, getHinge(context))
                .setValue(POWERED, powered)
                .setValue(OPEN, powered)
                .setValue(SEGMENT, DoorSegment.BOTTOM);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        for (int i = 1; i < this.height; i++) {
            DoorSegment segment = i == this.height - 1 ? DoorSegment.TOP : DoorSegment.MIDDLE;
            level.setBlock(pos.above(i), state.setValue(SEGMENT, segment), 3);
        }
    }

    /** Same neighbor-sniffing hinge heuristic vanilla DoorBlock uses; only the BOTTOM/adjacent-door check is retargeted at this class. */
    private DoorHingeSide getHinge(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        BlockPos abovePos = pos.above();
        Direction ccw = facing.getCounterClockWise();
        BlockPos leftPos = pos.relative(ccw);
        BlockState leftState = level.getBlockState(leftPos);
        BlockPos aboveLeftPos = abovePos.relative(ccw);
        BlockState aboveLeftState = level.getBlockState(aboveLeftPos);
        Direction cw = facing.getClockWise();
        BlockPos rightPos = pos.relative(cw);
        BlockState rightState = level.getBlockState(rightPos);
        BlockPos aboveRightPos = abovePos.relative(cw);
        BlockState aboveRightState = level.getBlockState(aboveRightPos);

        int score = (leftState.isCollisionShapeFullBlock(level, leftPos) ? -1 : 0)
                + (aboveLeftState.isCollisionShapeFullBlock(level, aboveLeftPos) ? -1 : 0)
                + (rightState.isCollisionShapeFullBlock(level, rightPos) ? 1 : 0)
                + (aboveRightState.isCollisionShapeFullBlock(level, aboveRightPos) ? 1 : 0);

        boolean leftIsDoorBase = leftState.getBlock() instanceof TallDoorBlock && leftState.getValue(SEGMENT) == DoorSegment.BOTTOM;
        boolean rightIsDoorBase = rightState.getBlock() instanceof TallDoorBlock && rightState.getValue(SEGMENT) == DoorSegment.BOTTOM;

        if ((!leftIsDoorBase || rightIsDoorBase) && score <= 0) {
            if ((!rightIsDoorBase || leftIsDoorBase) && score >= 0) {
                int stepX = facing.getStepX();
                int stepZ = facing.getStepZ();
                Vec3 click = context.getClickLocation();
                double dx = click.x - pos.getX();
                double dz = click.z - pos.getZ();
                return (stepX >= 0 || !(dz < 0.5)) && (stepX <= 0 || !(dz > 0.5)) && (stepZ >= 0 || !(dx > 0.5)) && (stepZ <= 0 || !(dx < 0.5))
                        ? DoorHingeSide.LEFT
                        : DoorHingeSide.RIGHT;
            }
            return DoorHingeSide.LEFT;
        }
        return DoorHingeSide.RIGHT;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!this.type.canOpenByHand()) {
            return InteractionResult.PASS;
        }
        BlockState newState = state.cycle(OPEN);
        level.setBlock(pos, newState, 10);
        playSound(player, level, pos, newState.getValue(OPEN));
        level.gameEvent(player, isOpen(newState) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean isOpen(BlockState state) {
        return state.getValue(OPEN);
    }

    public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
        if (state.is(this) && state.getValue(OPEN) != open) {
            level.setBlock(pos, state.setValue(OPEN, open), 10);
            playSound(entity, level, pos, open);
            level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean powered = level.hasNeighborSignal(pos);
        if (!this.defaultBlockState().is(neighborBlock) && powered != state.getValue(POWERED)) {
            if (powered != state.getValue(OPEN)) {
                playSound(null, level, pos, powered);
                level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), 2);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(SEGMENT) == DoorSegment.BOTTOM) {
            BlockPos belowPos = pos.below();
            return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
        }
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return belowState.is(this) && belowState.getValue(SEGMENT) != DoorSegment.TOP;
    }

    private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open) {
        level.playSound(entity, pos, open ? this.type.doorOpen() : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEGMENT, FACING, OPEN, HINGE, POWERED);
    }
}
