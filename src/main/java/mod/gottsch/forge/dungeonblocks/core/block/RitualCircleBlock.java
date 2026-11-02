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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One tile of a ritual circle chalked on a floor, three blocks across. Placed by hand it is built
 * out from the centre ({@link #getStateForPlacement}), not taken from the world grid as the
 * flagstones are, so a circle can be laid wherever a room needs it.
 *
 * <p>Unlike the flagstones, a circle belongs to the place it was drawn: an altar stands before it,
 * a spawner sits under its centre. So a structure must NOT re-align it to the world grid
 * ({@link #alignsToWorld} is false); it keeps the tiles its author placed, and only turns them with
 * the structure. Rotating or mirroring a template moves the nine blocks; {@link #rotate} and
 * {@link #mirror} move each TILE to match, so the cell that lands at the circle's east edge shows the
 * east edge. A tile's picture cannot turn, so the art must look the same after a quarter turn or a
 * mirror - tools/gen_ritual_circle_textures.py draws it that way and refuses a sheet that is not.
 *
 * <p>TILE is col + 3 * row, col along +x and row along +z. Vanilla mirrors, then rotates.
 *
 * @author Mark Gottschling on 10/24/2026
 */
public class RitualCircleBlock extends PatternTileBlock {
    /** The middle cell, the sun. */
    public static final int CENTRE = 4;

    public RitualCircleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean alignsToWorld() {
        return false;
    }

    /**
     * Built out from the centre, wherever it is: a tile placed beside a circle tile on the same level
     * continues the circle from it (the cell one step that way); a tile with none beside it is the
     * centre. So a player lays the sun first, then the ring round it, and the circle can sit anywhere
     * rather than on the world's 3-block grid. A neighbour whose next cell would fall outside its
     * circle (placing beyond an edge) is ignored, so two circles can be laid side by side.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbour = level.getBlockState(pos.relative(direction));
            if (!neighbour.is(this)) {
                continue;
            }
            int tile = neighbour.getValue(TILE);
            // this block is one step from the neighbour, opposite to `direction`
            int col = tile % SPAN - direction.getStepX(), row = tile / SPAN - direction.getStepZ();
            if (col >= 0 && col < SPAN && row >= 0 && row < SPAN) {
                return defaultBlockState().setValue(TILE, col + SPAN * row);
            }
        }
        return defaultBlockState().setValue(TILE, CENTRE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(TILE, rotated(state.getValue(TILE), rotation));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(TILE, mirrored(state.getValue(TILE), mirror));
    }

    /** The tile's cell after the circle turns about its centre cell, clockwise seen from above. */
    static int rotated(int tile, Rotation rotation) {
        int col = tile % SPAN, row = tile / SPAN, last = SPAN - 1;
        return switch (rotation) {
            case NONE -> tile;
            case CLOCKWISE_90 -> (last - row) + SPAN * col;
            case CLOCKWISE_180 -> (last - col) + SPAN * (last - row);
            case COUNTERCLOCKWISE_90 -> row + SPAN * (last - col);
        };
    }

    /** LEFT_RIGHT swaps north and south (rows); FRONT_BACK swaps east and west (columns). */
    static int mirrored(int tile, Mirror mirror) {
        int col = tile % SPAN, row = tile / SPAN, last = SPAN - 1;
        return switch (mirror) {
            case NONE -> tile;
            case LEFT_RIGHT -> col + SPAN * (last - row);
            case FRONT_BACK -> (last - col) + SPAN * row;
        };
    }
}
