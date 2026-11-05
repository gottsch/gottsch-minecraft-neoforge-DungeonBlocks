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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * One tile of a pattern three blocks square, like glazed terracotta but larger: the flagstone
 * floor, whose stones cross block edges. TILE picks the slice of the 48x48 pattern this block
 * shows, col + 3 * row, and a hand-laid floor lines up because placement takes it from the
 * position ({@link #tileAt}): col = x mod 3, row = z mod 3.
 *
 * <p>TILE is set once, on placement, and never re-derived in {@code updateShape}: worldgen writes
 * states directly, and an {@code updateShape} that reads the level has crashed chunk generation
 * before. A structure that wants the pattern aligned sets TILE itself, from {@link #tileAt} of the
 * world position (a structure processor, since rotating or mirroring a template moves its blocks
 * without changing their TILE). A weathered twin (mossy, cracked) should keep the same TILE
 * property, so a rule swapping one for the other keeps the pattern whole.
 *
 * <p>The textures are tools/gen_flagstone_textures.py's: &lt;name&gt;_&lt;tile&gt;.png, one per TILE.
 *
 * @author Mark Gottschling on 10/22/2026
 */
public class PatternTileBlock extends Block {
    /** The pattern's size in blocks, along x and along z. */
    public static final int SPAN = 3;
    public static final IntegerProperty TILE = IntegerProperty.create("tile", 0, SPAN * SPAN - 1);

    public PatternTileBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TILE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TILE);
    }

    /** The tile a block at `pos` shows, so that every such block together lays out the pattern. */
    public static int tileAt(BlockPos pos) {
        return Math.floorMod(pos.getX(), SPAN) + SPAN * Math.floorMod(pos.getZ(), SPAN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(TILE, tileAt(context.getClickedPos()));
    }

    /**
     * Whether a structure should re-derive TILE from each block's world position ({@link #tileAt}).
     * True for a repeating floor such as the flagstones, which must line up with whatever is around
     * it. False for a picture that belongs to its own place, such as a ritual circle, which a
     * structure must keep where its author drew it (see {@link RitualCircleBlock}).
     */
    public boolean alignsToWorld() {
        return true;
    }
}
