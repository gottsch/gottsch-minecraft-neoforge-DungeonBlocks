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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * A pattern three blocks square for walls, whose picture crosses block edges. {@link PatternTileBlock}
 * lays one out on a floor from x and z, and a wall also needs y. It also needs each side face to read
 * its own way: a face's texture runs along +x seen from the south but along -x seen from the north. So
 * the block keeps its position mod 3, and its model gives each face the slice that position shows from
 * that side. No direction is needed from the player, and a wall lines up from either side.
 *
 * <p>The state is set once, on placement, for the reason given on {@link PatternTileBlock}. A rotated or
 * mirrored structure moves its blocks, so {@link #rotate} and {@link #mirror} turn X and Z as the
 * position turns. Each block then holds the state it would have if placed there by hand, give or take
 * a shift that is the same for the whole structure: its walls stay whole, though they need not line
 * up with a wall outside it.
 *
 * @author Mark Gottschling on 10/31/2026
 */
public class WallPatternBlock extends Block {
    public static final int SPAN = 3;
    public static final IntegerProperty X = IntegerProperty.create("x", 0, SPAN - 1);
    public static final IntegerProperty Y = IntegerProperty.create("y", 0, SPAN - 1);
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, SPAN - 1);

    public WallPatternBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(X, 0).setValue(Y, 0).setValue(Z, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(X, Y, Z);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(X, Math.floorMod(pos.getX(), SPAN))
                .setValue(Y, Math.floorMod(pos.getY(), SPAN))
                .setValue(Z, Math.floorMod(pos.getZ(), SPAN));
    }

    /** As BlockPos#rotate turns a position. */
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        int x = state.getValue(X);
        int z = state.getValue(Z);
        return switch (rotation) {
            case CLOCKWISE_90 -> withColumn(state, -z, x);
            case CLOCKWISE_180 -> withColumn(state, -x, -z);
            case COUNTERCLOCKWISE_90 -> withColumn(state, z, -x);
            case NONE -> state;
        };
    }

    /** As StructureTemplate mirrors a position: LEFT_RIGHT negates z, FRONT_BACK negates x. */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        int x = state.getValue(X);
        int z = state.getValue(Z);
        return switch (mirror) {
            case LEFT_RIGHT -> withColumn(state, x, -z);
            case FRONT_BACK -> withColumn(state, -x, z);
            case NONE -> state;
        };
    }

    private static BlockState withColumn(BlockState state, int x, int z) {
        return state.setValue(X, Math.floorMod(x, SPAN)).setValue(Z, Math.floorMod(z, SPAN));
    }
}
