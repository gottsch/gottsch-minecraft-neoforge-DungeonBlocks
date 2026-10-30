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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * A tapestry: a woven scene WIDTH blocks wide and HEIGHT tall, hung on a rod against a wall. Each
 * block is one part of it, COLUMN counted from the left as you face it and ROW from the bottom; its
 * model shows that part's window of the one texture (tools/gen_tapestry_textures.py). FACING is the
 * way the cloth faces, out from its wall; the rod rests on the wall and the cloth hangs 1px out from it.
 *
 * <p>Placed as one, against a wall: the block clicked is the top row, second from the left, and the
 * rest hangs down and to the sides. Every part needs its space free and a wall behind it, or none of
 * it is placed. Losing any part - or the wall behind one - takes down the lot; only the bottom-left
 * part drops the item. Cloth: no collision, and no tool needed.
 */
public class TapestryBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<TapestryBlock> CODEC = simpleCodec(TapestryBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public static final int WIDTH = 4;
    public static final int HEIGHT = 3;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, WIDTH - 1);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, HEIGHT - 1);
    /** The column of the part placed where the player clicked, in the top row. */
    private static final int PLACED_COLUMN = 1;

    /** The cloth and rod, a thin layer against the wall behind. */
    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, Block.box(0, 0, 13, 16, 16, 16),
            Direction.SOUTH, Block.box(0, 0, 0, 16, 16, 3),
            Direction.WEST, Block.box(13, 0, 0, 16, 16, 16),
            Direction.EAST, Block.box(0, 0, 0, 3, 16, 16));

    public TapestryBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COLUMN, 0).setValue(ROW, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN, ROW);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    /** To the right, as you stand facing the cloth. */
    private static Direction right(Direction facing) {
        return facing.getCounterClockWise();
    }

    /** The bottom-left part's position, from any part's. */
    private static BlockPos origin(BlockPos pos, BlockState state) {
        return pos.relative(right(state.getValue(FACING)), -state.getValue(COLUMN)).below(state.getValue(ROW));
    }

    private static boolean hasWall(LevelReader level, BlockPos pos, Direction facing) {
        BlockPos wall = pos.relative(facing.getOpposite());
        return level.getBlockState(wall).isFaceSturdy(level, wall, facing);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        if (!facing.getAxis().isHorizontal()) {
            return null;
        }
        Level level = context.getLevel();
        BlockState state = defaultBlockState().setValue(FACING, facing)
                .setValue(COLUMN, PLACED_COLUMN).setValue(ROW, HEIGHT - 1);
        BlockPos origin = origin(context.getClickedPos(), state);
        for (int c = 0; c < WIDTH; c++) {
            for (int r = 0; r < HEIGHT; r++) {
                BlockPos p = origin.relative(right(facing), c).above(r);
                if (level.isOutsideBuildHeight(p) || !level.getBlockState(p).canBeReplaced(context)
                        || !hasWall(level, p, facing)) {
                    return null;
                }
            }
        }
        return state;
    }

    /** Hangs the rest of the cloth around the part that was placed. */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos origin = origin(pos, state);
        Direction right = right(state.getValue(FACING));
        for (int c = 0; c < WIDTH; c++) {
            for (int r = 0; r < HEIGHT; r++) {
                BlockPos p = origin.relative(right, c).above(r);
                if (!p.equals(pos)) {
                    level.setBlock(p, state.setValue(COLUMN, c).setValue(ROW, r), Block.UPDATE_ALL);
                }
            }
        }
    }

    /**
     * Each part needs its wall, and the neighbouring parts the grid says are there: losing either
     * takes this part down too, and so the whole cloth comes down as a unit.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            return hasWall(level, pos, facing) ? state : Blocks.AIR.defaultBlockState();
        }
        int c = state.getValue(COLUMN);
        int r = state.getValue(ROW);
        int dc = direction == right(facing) ? 1 : direction == right(facing).getOpposite() ? -1 : 0;
        int dr = direction == Direction.UP ? 1 : direction == Direction.DOWN ? -1 : 0;
        if (dc == 0 && dr == 0) {
            return state;
        }
        int nc = c + dc;
        int nr = r + dr;
        if (nc < 0 || nc >= WIDTH || nr < 0 || nr >= HEIGHT) {
            return state;
        }
        boolean held = neighbour.is(this) && neighbour.getValue(FACING) == facing
                && neighbour.getValue(COLUMN) == nc && neighbour.getValue(ROW) == nr;
        return held ? state : Blocks.AIR.defaultBlockState();
    }

    /**
     * In creative, the cascade from breaking one part would reach the bottom-left part with drops on
     * and hand the player a free tapestry: clear that part with the no-drop flag first.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BlockPos origin = origin(pos, state);
            BlockState first = level.getBlockState(origin);
            if (!origin.equals(pos) && first.is(this)) {
                level.setBlock(origin, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, origin, Block.getId(first));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
}
