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

import net.minecraft.world.level.block.AbstractCandleBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * A skull with a candle melted onto its crown, wax run down over its brow. It stands on the floor,
 * or with WALL, sits on an iron bracket against the wall it was placed on, as a torch chooses. FACING
 * is the way the skull looks: out from the wall, or toward the player on a floor.
 *
 * <p>Lit and put out as every {@link CandlePropBlock} is. The models are blockbench/skull_candle.bbmodel,
 * authored facing north; the flame heights and wall positions here must match skull_candle in
 * tools/gen_bbmodels.py.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class SkullCandleBlock extends CandlePropBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WALL = BooleanProperty.create("wall");
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIT) ? 8 : 0;

    /** The skull and the candle standing on it. */
    private static final VoxelShape FLOOR_SHAPE = Shapes.or(Block.box(4, 0, 4, 12, 8, 12), Block.box(6, 8, 6, 10, 13, 10));
    private static final List<Vec3> FLOOR_FLAME = List.of(new Vec3(0.5, 13.5 / 16.0, 0.5));
    private static final Map<Direction, VoxelShape> WALL_SHAPES = new EnumMap<>(Direction.class);
    private static final Map<Direction, List<Vec3>> WALL_FLAMES = new EnumMap<>(Direction.class);

    static {
        WALL_SHAPES.put(Direction.NORTH, Shapes.or(Block.box(4, 1, 8, 12, 10, 16), Block.box(6, 10, 10, 10, 15, 14)));
        WALL_SHAPES.put(Direction.SOUTH, Shapes.or(Block.box(4, 1, 0, 12, 10, 8), Block.box(6, 10, 2, 10, 15, 6)));
        WALL_SHAPES.put(Direction.WEST, Shapes.or(Block.box(8, 1, 4, 16, 10, 12), Block.box(10, 10, 6, 14, 15, 10)));
        WALL_SHAPES.put(Direction.EAST, Shapes.or(Block.box(0, 1, 4, 8, 10, 12), Block.box(2, 10, 6, 6, 15, 10)));
        double y = 15.5 / 16.0;
        WALL_FLAMES.put(Direction.NORTH, List.of(new Vec3(0.5, y, 0.75)));
        WALL_FLAMES.put(Direction.SOUTH, List.of(new Vec3(0.5, y, 0.25)));
        WALL_FLAMES.put(Direction.WEST, List.of(new Vec3(0.75, y, 0.5)));
        WALL_FLAMES.put(Direction.EAST, List.of(new Vec3(0.25, y, 0.5)));
    }

    public static final MapCodec<SkullCandleBlock> CODEC = simpleCodec(SkullCandleBlock::new);

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    public SkullCandleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WALL, false)
                .setValue(LIT, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WALL, LIT, WATERLOGGED);
    }

    /** Against a wall it can hang on, it sits on its bracket; anywhere else it stands, if it can. */
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face.getAxis().isHorizontal()) {
            BlockState onWall = placed(defaultBlockState().setValue(FACING, face).setValue(WALL, true), context);
            if (onWall.canSurvive(context.getLevel(), context.getClickedPos())) {
                return onWall;
            }
        }
        BlockState onFloor = placed(defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()), context);
        return onFloor.canSurvive(context.getLevel(), context.getClickedPos()) ? onFloor : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!state.getValue(WALL)) {
            return super.canSurvive(state, level, pos);
        }
        Direction facing = state.getValue(FACING);
        BlockPos behind = pos.relative(facing.getOpposite());
        return level.getBlockState(behind).isFaceSturdy(level, behind, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(WALL) && direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(WALL) ? WALL_SHAPES.get(state.getValue(FACING)) : FLOOR_SHAPE;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return state.getValue(WALL) ? WALL_FLAMES.get(state.getValue(FACING)) : FLOOR_FLAME;
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
