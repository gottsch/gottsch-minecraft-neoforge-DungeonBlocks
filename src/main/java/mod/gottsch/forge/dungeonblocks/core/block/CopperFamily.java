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

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * One copper shape in all 8 of its blocks: the 4 weathering ages and a waxed twin of each.
 * Ids are {@code [waxed_][exposed_|weathered_|oxidized_]<stem>}, the vanilla pattern, so the stem
 * carries any suffix ({@code copper_plate_bracket_block}).
 * <p>
 * The weathering chain ({@link ModWeatheringCopper#NEXT_BY_BLOCK}) is built from {@link #ALL}, so a
 * new family weathers without being listed anywhere else.
 */
public final class CopperFamily {
    /** Every family, in registration order. */
    public static final List<CopperFamily> ALL = new ArrayList<>();

    private final String stem;
    private final Map<WeatherState, DeferredHolder<Block, Block>> weathering = new EnumMap<>(WeatherState.class);
    private final Map<WeatherState, DeferredHolder<Block, Block>> waxed = new EnumMap<>(WeatherState.class);

    private CopperFamily(String stem) {
        this.stem = stem;
    }

    /**
     * Registers the 8 blocks, the 4 weathering ones first, each in age order. Properties come from
     * an explicit source per age, named at the call site, so a waxed block cannot copy the wrong age.
     *
     * @param props       the properties of the weathering block of each age
     * @param weathering  makes the weathering block of an age
     * @param waxedProps  the properties of the waxed block of each age
     * @param waxed       makes a waxed block
     */
    static CopperFamily register(String stem,
            Function<WeatherState, Properties> props, BiFunction<WeatherState, Properties, Block> weathering,
            Function<WeatherState, Properties> waxedProps, Function<Properties, Block> waxed) {
        CopperFamily family = new CopperFamily(stem);
        for (WeatherState age : WeatherState.values()) {
            family.weathering.put(age, Registration.BLOCKS.register(id(age, stem),
                    () -> weathering.apply(age, props.apply(age))));
        }
        for (WeatherState age : WeatherState.values()) {
            family.waxed.put(age, Registration.BLOCKS.register("waxed_" + id(age, stem),
                    () -> waxed.apply(waxedProps.apply(age))));
        }
        ALL.add(family);
        return family;
    }

    /** {@code stem} with the age's vanilla prefix: copper_grate, exposed_copper_grate, ... */
    public static String id(WeatherState age, String stem) {
        return age == WeatherState.UNAFFECTED ? stem : age.name().toLowerCase() + "_" + stem;
    }

    public String stem() {
        return stem;
    }

    /** The weathering block of this age. */
    public DeferredHolder<Block, Block> get(WeatherState age) {
        return weathering.get(age);
    }

    public DeferredHolder<Block, Block> waxed(WeatherState age) {
        return waxed.get(age);
    }

    /** A copy of the weathering block's properties at this age, for a family that borrows them. */
    public Properties props(WeatherState age) {
        return Properties.ofFullCopy(get(age).get());
    }

    /** All 8 blocks with their age, the 4 weathering ones first, as registered. */
    public void forEach(BiConsumer<WeatherState, DeferredHolder<Block, Block>> action) {
        weathering.forEach(action);
        waxed.forEach(action);
    }
}
