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

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * A {@link SawTrapBlock} hidden in a pattern-tiled floor such as Grave Slab Pavement. It carries the
 * floor's TILE and LAYER and takes them from its position the way {@link StackedPatternTileBlock}
 * does, so it shows the same slice of the pattern as the floor block it replaces. Only the slot
 * overlay on its top gives it away.
 *
 * @author Mark Gottschling on 11/05/2026
 */
public class PatternSawTrapBlock extends SawTrapBlock {

    public PatternSawTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PatternTileBlock.TILE, 0)
                .setValue(StackedPatternTileBlock.LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PatternTileBlock.TILE, StackedPatternTileBlock.LAYER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(PatternTileBlock.TILE, PatternTileBlock.tileAt(context.getClickedPos()))
                .setValue(StackedPatternTileBlock.LAYER,
                        Math.floorMod(context.getClickedPos().getY(), PatternTileBlock.SPAN));
    }
}
