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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author by Mark Gottschling on 5/12/2025
 */
public interface ModWeatheringCopper extends ChangeOverTimeBlock<net.minecraft.world.level.block.WeatheringCopper.WeatherState> {
    Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                .put(ModBlocks.COPPER_GRATE.get(), ModBlocks.EXPOSED_COPPER_GRATE.get())
                .put(ModBlocks.EXPOSED_COPPER_GRATE.get(), ModBlocks.WEATHERED_COPPER_GRATE.get())
                .put(ModBlocks.WEATHERED_COPPER_GRATE.get(), ModBlocks.OXIDIZED_COPPER_GRATE.get())

                .put(ModBlocks.COPPER_TRAPDOOR.get(), ModBlocks.EXPOSED_COPPER_TRAPDOOR.get())
                .put(ModBlocks.EXPOSED_COPPER_TRAPDOOR.get(), ModBlocks.WEATHERED_COPPER_TRAPDOOR.get())
                .put(ModBlocks.WEATHERED_COPPER_TRAPDOOR.get(), ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get())

                .put(ModBlocks.COPPER_DOOR.get(), ModBlocks.EXPOSED_COPPER_DOOR.get())
                .put(ModBlocks.EXPOSED_COPPER_DOOR.get(), ModBlocks.WEATHERED_COPPER_DOOR.get())
                .put(ModBlocks.WEATHERED_COPPER_DOOR.get(), ModBlocks.OXIDIZED_COPPER_DOOR.get())

                .put(ModBlocks.COPPER_HEAVY_GRATE.get(), ModBlocks.EXPOSED_COPPER_HEAVY_GRATE.get())
                .put(ModBlocks.EXPOSED_COPPER_HEAVY_GRATE.get(), ModBlocks.WEATHERED_COPPER_HEAVY_GRATE.get())
                .put(ModBlocks.WEATHERED_COPPER_HEAVY_GRATE.get(), ModBlocks.OXIDIZED_COPPER_HEAVY_GRATE.get())

                .put(ModBlocks.COPPER_HEAVY_TRAPDOOR.get(), ModBlocks.EXPOSED_COPPER_HEAVY_TRAPDOOR.get())
                .put(ModBlocks.EXPOSED_COPPER_HEAVY_TRAPDOOR.get(), ModBlocks.WEATHERED_COPPER_HEAVY_TRAPDOOR.get())
                .put(ModBlocks.WEATHERED_COPPER_HEAVY_TRAPDOOR.get(), ModBlocks.OXIDIZED_COPPER_HEAVY_TRAPDOOR.get())

                // TODO PLATE BRACKET

                .put(ModBlocks.COPPER_ANGLE_PLATE_BRACKET.get(), ModBlocks.EXPOSED_COPPER_ANGLE_PLATE_BRACKET.get())
                .put(ModBlocks.EXPOSED_COPPER_ANGLE_PLATE_BRACKET.get(), ModBlocks.WEATHERED_COPPER_ANGLE_PLATE_BRACKET.get())
                .put(ModBlocks.WEATHERED_COPPER_ANGLE_PLATE_BRACKET.get(), ModBlocks.OXIDIZED_COPPER_ANGLE_PLATE_BRACKET.get())

                .put(ModBlocks.COPPER_CORNER_PLATE_BRACKET.get(), ModBlocks.EXPOSED_COPPER_CORNER_PLATE_BRACKET.get())
                .put(ModBlocks.EXPOSED_COPPER_CORNER_PLATE_BRACKET.get(), ModBlocks.WEATHERED_COPPER_CORNER_PLATE_BRACKET.get())
                .put(ModBlocks.WEATHERED_COPPER_CORNER_PLATE_BRACKET.get(), ModBlocks.OXIDIZED_COPPER_CORNER_PLATE_BRACKET.get())

                .build();
    });
    Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
        return NEXT_BY_BLOCK.get().inverse();
    });

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
