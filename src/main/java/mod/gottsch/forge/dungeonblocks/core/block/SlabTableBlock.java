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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A two-block trestle table that is placed, broken and rotated as a single unit, exactly like a
 * vanilla bed.
 *
 * <p>{@link #PART} is vanilla's {@code BedPart} rather than a bespoke enum: the semantics are
 * identical (one placed half plus one projected half) and reusing it means the states read
 * {@code part=foot} / {@code part=head}, which is already familiar to anyone writing structure NBT
 * or {@code /setblock}. {@link #FACING} points from the FOOT toward the HEAD.
 *
 * <p>Both halves share one geometry, a refectory table's: a thin top, a leg and foot at the table's
 * far end, and a stretcher running to the other half. The model is authored as the FOOT facing NORTH,
 * its leg to the south; the blockstate supplies the y-rotation for the other three facings, and turns
 * the HEAD a further half circle, so that its leg is at the other end of the table.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
public class SlabTableBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<SlabTableBlock> CODEC = simpleCodec(SlabTableBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    private static final VoxelShape TOP = Block.box(0D, 12D, 0D, 16D, 16D, 16D);
    // by the side of the block the leg is on, the end of the table. The stretcher is left out, as
    // something to catch on: the pieces are symmetric across the table, so only the z range turns.
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(TOP,
            Block.box(2D, 2D, 11D, 14D, 12D, 14D), Block.box(1D, 0D, 10D, 15D, 2D, 15D));
    private static final VoxelShape SHAPE_NORTH = Shapes.or(TOP,
            Block.box(2D, 2D, 2D, 14D, 12D, 5D), Block.box(1D, 0D, 1D, 15D, 2D, 6D));
    private static final VoxelShape SHAPE_EAST = Shapes.or(TOP,
            Block.box(11D, 2D, 2D, 14D, 12D, 14D), Block.box(10D, 0D, 1D, 15D, 2D, 15D));
    private static final VoxelShape SHAPE_WEST = Shapes.or(TOP,
            Block.box(2D, 2D, 2D, 5D, 12D, 14D), Block.box(1D, 0D, 1D, 6D, 2D, 15D));

    public SlabTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction end = state.getValue(PART) == BedPart.HEAD ? state.getValue(FACING) : state.getValue(FACING).getOpposite();
        return switch (end) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_SOUTH;
        };
    }

    /**
     * Returning null aborts the placement, so a table can never be created with only one half - the
     * far block has to be free first.
     */
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos headPos = context.getClickedPos().relative(facing);
        Level level = context.getLevel();
        return level.getBlockState(headPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(headPos)
                ? this.defaultBlockState().setValue(FACING, facing)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            level.setBlock(pos.relative(state.getValue(FACING)), state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    /**
     * Losing the other half destroys this one, which is what makes the pair break as a unit. Only
     * the direction toward the partner is checked, so unrelated neighbour changes are left alone.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == towardOtherHalf(state)) {
            return neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * In survival the surviving half is destroyed by {@link #updateShape} and the loot table decides
     * what drops. Creative has to clear it explicitly with the no-drop flag, because that cascade
     * runs {@code destroyBlock} with drops enabled and the HEAD's loot condition would otherwise be
     * satisfied - handing the player a free table for breaking one in creative.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BlockPos otherPos = pos.relative(towardOtherHalf(state));
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(PART) != state.getValue(PART)) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /** Direction from this half toward its partner. */
    protected static Direction towardOtherHalf(BlockState state) {
        Direction facing = state.getValue(FACING);
        return state.getValue(PART) == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
