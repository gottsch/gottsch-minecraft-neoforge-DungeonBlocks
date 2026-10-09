/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author by Mark Gottschling on 5/12/2025
 */
public interface ModWeatheringCopper extends ChangeOverTimeBlock<net.minecraft.world.level.block.WeatheringCopper.WeatherState> {
    /**
     * Each weathering block to its next age, for every {@link CopperFamily}. Built from the families
     * rather than listed, so no family can be left out of the chain (plate brackets and valve wheels
     * once were, and never aged). Waxed blocks are in no chain: they never age.
     */
    Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
        ImmutableBiMap.Builder<Block, Block> next = ImmutableBiMap.builder();
        WeatherState[] ages = WeatherState.values();
        for (CopperFamily family : CopperFamily.ALL) {
            for (int i = 0; i + 1 < ages.length; i++) {
                next.put(family.get(ages[i]).get(), family.get(ages[i + 1]).get());
            }
        }
        return next.build();
    });
    Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
        return NEXT_BY_BLOCK.get().inverse();
    });

    /**
     * Each weathering block to its waxed twin, for every {@link CopperFamily}: what a honeycomb
     * turns a block into, and (inverted) what an axe's wax-off turns it back to.
     */
    Supplier<BiMap<Block, Block>> WAXED_BY_BLOCK = Suppliers.memoize(() -> {
        ImmutableBiMap.Builder<Block, Block> waxed = ImmutableBiMap.builder();
        for (CopperFamily family : CopperFamily.ALL) {
            for (WeatherState age : WeatherState.values()) {
                waxed.put(family.get(age).get(), family.waxed(age).get());
            }
        }
        return waxed.build();
    });

    /** The waxed twin of a weathering copper state, if it has one. */
    static Optional<BlockState> getWaxed(BlockState state) {
        return Optional.ofNullable(WAXED_BY_BLOCK.get().get(state.getBlock())).map(b -> b.withPropertiesOf(state));
    }

    /** The weathering block a waxed copper state was waxed from, if it is one. */
    static Optional<BlockState> getUnwaxed(BlockState state) {
        return Optional.ofNullable(WAXED_BY_BLOCK.get().inverse().get(state.getBlock())).map(b -> b.withPropertiesOf(state));
    }

    static Optional<Block> getPrevious(Block p_154891_) {
        return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(p_154891_));
    }

    static Block getFirst(Block p_154898_) {
        Block block = p_154898_;

        for(Block block1 = PREVIOUS_BY_BLOCK.get().get(p_154898_); block1 != null; block1 = PREVIOUS_BY_BLOCK.get().get(block1)) {
            block = block1;
        }

        return block;
    }

    static Optional<BlockState> getPrevious(BlockState p_154900_) {
        return getPrevious(p_154900_.getBlock()).map((p_154903_) -> {
            return p_154903_.withPropertiesOf(p_154900_);
        });
    }

    static Optional<Block> getNext(Block p_154905_) {
        return Optional.ofNullable(NEXT_BY_BLOCK.get().get(p_154905_));
    }

    static BlockState getFirst(BlockState p_154907_) {
        return getFirst(p_154907_.getBlock()).withPropertiesOf(p_154907_);
    }

    default Optional<BlockState> getNext(BlockState p_154893_) {
        return getNext(p_154893_.getBlock()).map((p_154896_) -> {
            return p_154896_.withPropertiesOf(p_154893_);
        });
    }

    default float getChanceModifier() {
        return this.getAge() == net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
    }
}
