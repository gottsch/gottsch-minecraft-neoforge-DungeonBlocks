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

import mod.gottsch.neo.gottschcore.block.FacingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.List;
import java.util.function.Function;

/**
 * A decorative block-type made in many materials. Each type knows its id suffix and its block
 * class; datagen dispatches on the type ({@link ModBlocks#DECOR}), never on the id, so a new
 * material or type cannot be missed by a substring that fails to match.
 */
public enum DecorType {
    FACADE("facade"),
    QUARTER_FACADE("quarter_facade"),
    FLUTED("fluted", FlutedBlock::new),
    FLUTED_FACADE("fluted_facade"),
    SILL("sill", SillBlock::new),
    DOUBLE_SILL("double_sill", DoubleSillBlock::new),
    CORNICE("cornice"),
    CROWN_MOLDING("crown_molding"),
    PILLAR_BASE("pillar_base", PillarBaseBlock::new),
    PILLAR("pillar", PillarBlock::new),
    ARROW_SLIT("arrow_slit", FacingBlock::new),
    BARRED_WINDOW("barred_window", BarredWindowBlock::new),
    BARRED_WINDOW_FACADE("barred_window_facade", BarredWindowFacadeBlock::new),
    LEDGE("ledge"),
    CORBEL("corbel", CorbelBlock::new);

    /** The types every {@link ModMaterials#STONE} material is made in, in registration order. */
    public static final List<DecorType> STONE_TYPES = List.of(FACADE, QUARTER_FACADE, FLUTED, FLUTED_FACADE,
            SILL, DOUBLE_SILL, CORNICE, CROWN_MOLDING, PILLAR_BASE, PILLAR, ARROW_SLIT);

    private final String key;
    private final Function<Properties, Block> factory;

    DecorType(String key, Function<Properties, Block> factory) {
        this.key = key;
        this.factory = factory;
    }

    /** A facade-shaped type: a {@link FacadeShapeBlock} of this kind. */
    DecorType(String key) {
        this.key = key;
        this.factory = null;
    }

    /** This type's block id in a material: {@code <material>_<type>_block}. */
    public String id(ModMaterials.Material material) {
        return material.name() + "_" + key + "_block";
    }

    public Block create(Properties properties) {
        return factory == null ? new FacadeShapeBlock(properties, this) : factory.apply(properties);
    }

    /** One registered decorative block: what it is made of, and what type it is. */
    public record DecorBlock(ModMaterials.Material material, DecorType type, net.neoforged.neoforge.registries.DeferredHolder<Block, Block> block) {
    }
}
