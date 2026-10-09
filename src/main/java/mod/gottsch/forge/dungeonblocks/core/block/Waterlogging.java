/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * The three pieces every {@code SimpleWaterloggedBlock} here repeats, as vanilla's own waterlogged
 * blocks do them. A block still declares {@code WATERLOGGED} and adds it to its state definition;
 * these are its placement, {@code updateShape} and {@code getFluidState} bodies.
 * <p>
 * Douse-on-water behaviour (brazier, chandelier, censer, sconce) stays in each block's own
 * {@code placeLiquid}: it differs per block.
 */
public final class Waterlogging {
    private Waterlogging() {
    }

    /** Whether a block placed here starts waterlogged: only in a water source, as vanilla. */
    public static boolean placedInWater(BlockPlaceContext context) {
        return context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
    }

    /** For {@code updateShape}: a waterlogged block keeps its water flowing to its neighbours. */
    public static void tickWater(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    /** For {@code getFluidState}: still water when waterlogged, otherwise what the block would have. */
    public static FluidState fluid(BlockState state, FluidState dry) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : dry;
    }
}
