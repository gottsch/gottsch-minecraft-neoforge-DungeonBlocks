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
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * One dark iron shape in its rust stages. Unlike copper these are separate blocks, not a chain:
 * vanilla iron does not rust, so a stage never turns into the next, and the rust is only the
 * texture. Ids are {@code [tarnished_|rusted_|corroded_]<stem>}. A shape need not have every stage.
 */
public final class AgedIronFamily {
    public enum Age {
        PLAIN, TARNISHED, RUSTED, CORRODED;

        /** {@code stem} with this age's prefix: dark_iron_grate, tarnished_dark_iron_grate, ... */
        public String id(String stem) {
            return this == PLAIN ? stem : name().toLowerCase() + "_" + stem;
        }
    }

    public static final List<Age> ALL_AGES = List.of(Age.values());

    private final Map<Age, DeferredHolder<Block, Block>> blocks = new EnumMap<>(Age.class);

    private AgedIronFamily() {
    }

    /** Registers one block per age, in the order given. */
    static AgedIronFamily register(String stem, List<Age> ages, Function<Age, Properties> props,
            Function<Properties, Block> factory) {
        AgedIronFamily family = new AgedIronFamily();
        for (Age age : ages) {
            family.blocks.put(age, Registration.BLOCKS.register(age.id(stem), () -> factory.apply(props.apply(age))));
        }
        return family;
    }

    public DeferredHolder<Block, Block> get(Age age) {
        return blocks.get(age);
    }

    public void forEach(BiConsumer<Age, DeferredHolder<Block, Block>> action) {
        blocks.forEach(action);
    }
}
