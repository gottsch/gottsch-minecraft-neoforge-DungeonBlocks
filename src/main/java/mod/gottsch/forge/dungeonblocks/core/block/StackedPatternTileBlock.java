/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * A {@link PatternTileBlock} that can also be stacked into a wall. The top and bottom show TILE, as
 * on a floor. The sides show the same pattern stood upright, at this block's height: LAYER is y mod
 * 3, set once on placement like TILE, so a stack's joints line up from one block to the next.
 *
 * <p>A structure's rotation moves x and z but not y, so LAYER needs no re-deriving when it turns.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class StackedPatternTileBlock extends PatternTileBlock {
    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, SPAN - 1);

    public StackedPatternTileBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAYER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(LAYER, Math.floorMod(context.getClickedPos().getY(), SPAN));
    }
}
