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
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;

/**
 * Loose chips of stone strewn over a floor, laid down as vanilla's pink petals are: each rubble
 * scatter used on one adds more chips, up to four stages, and breaking it gives back one per stage.
 * A player's scatter starts at the first, a few chips.
 *
 * <p>The default is the thickest stage, so a scatter a structure places without naming CHIPS is a
 * full one - the same default the bone pile has. The models, one per stage, are built from
 * blockbench/rubble_scatter.bbmodel, and the blockstate turns each a random quarter per block so a
 * floor of them does not repeat.
 */
public class RubbleScatterBlock extends CarpetBlock {
    public static final int FULL = 4;
    public static final IntegerProperty CHIPS = IntegerProperty.create("chips", 1, FULL);

    public RubbleScatterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHIPS, FULL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHIPS);
    }

    /** More rubble scatter, used on this one, lands in it rather than on top of it. */
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(asItem()) && state.getValue(CHIPS) < FULL
                || super.canBeReplaced(state, context);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) {
            return existing.setValue(CHIPS, Math.min(FULL, existing.getValue(CHIPS) + 1));
        }
        return defaultBlockState().setValue(CHIPS, 1);
    }
}
