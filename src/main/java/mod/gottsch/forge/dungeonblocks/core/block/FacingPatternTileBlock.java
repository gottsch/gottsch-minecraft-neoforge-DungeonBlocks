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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/**
 * A {@link PatternTileBlock} whose pattern has a direction and can be laid four ways: the fan
 * cobbles, whose arcs bulge toward FACING, and the sandstone ashlar, whose courses run across it.
 * The textures are drawn facing north; the model turns
 * to FACING, and TILE is taken from the position turned back the same way ({@link #tileAt}), so a
 * floor laid in any one direction lines up as the north one does.
 *
 * <p>Placed by hand it faces the way the player looks, unless it is laid beside one already down -
 * any FacingPatternTileBlock, so plain and mossy mix in one floor - whose FACING it takes, so a
 * floor stays one way however the player turns while laying it. Sneak to start a new direction
 * beside an old one.
 *
 * <p>A structure rotating a template turns FACING and keeps TILE, which turns the floor rigidly
 * and keeps it whole. Mirroring cannot be shown (the art has no mirror image), so a mirrored floor
 * comes out turned instead, and a structure that mirrors one should re-derive TILE from
 * {@link #tileAt} of each world position and its FACING.
 *
 * @author Mark Gottschling on 10/30/2026
 */
public class FacingPatternTileBlock extends PatternTileBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public FacingPatternTileBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    /**
     * The tile a block at `pos` shows when the pattern faces `facing`: the position turned back to
     * the north-facing sheet, as the model turns the sheet forward (clockwise seen from above).
     */
    public static int tileAt(BlockPos pos, Direction facing) {
        int x = pos.getX(), z = pos.getZ();
        int col, row;
        switch (facing) {
            case EAST -> { col = z; row = -x - 1; }
            case SOUTH -> { col = -x - 1; row = -z - 1; }
            case WEST -> { col = -z - 1; row = x; }
            default -> { col = x; row = z; }
        }
        return Math.floorMod(col, SPAN) + SPAN * Math.floorMod(row, SPAN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        if (!context.isSecondaryUseActive()) {
            for (Direction side : Direction.Plane.HORIZONTAL) {
                BlockState neighbour = context.getLevel().getBlockState(pos.relative(side));
                if (neighbour.getBlock() instanceof FacingPatternTileBlock) {
                    facing = neighbour.getValue(FACING);
                    break;
                }
            }
        }
        return defaultBlockState().setValue(FACING, facing).setValue(TILE, tileAt(pos, facing));
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
