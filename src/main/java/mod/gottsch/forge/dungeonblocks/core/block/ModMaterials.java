/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

/**
 * Central, data-driven catalog of the base materials that decorative block families
 * (facade, fluted, sill, cornice, pillar, etc.) are produced from.
 *
 * <p>To add a new material variant for every existing stone block-type, add a single
 * {@link Material} entry to {@link #STONE}. To add a new block-type for every material,
 * add one registration loop in {@code ModBlocks}. Block properties are derived once,
 * here, from the vanilla {@code base} block via {@link BlockBehaviour.Properties#copy}.
 *
 * @author Mark Gottschling
 */
public final class ModMaterials {
    private ModMaterials() {}

    /**
     * A buildable material: an id prefix (e.g. {@code "mossy_stone_bricks"}), the vanilla
     * block whose properties (hardness, blast resistance, sound, tool, map color) are copied
     * for every block made from this material, and the texture used when generating models.
     *
     * <p>The texture defaults to {@code minecraft:block/<name>} but can be overridden (e.g.
     * smooth sandstone uses the {@code sandstone_top} texture).
     */
    public record Material(String name, Block base, ResourceLocation texture) {
        /** Convenience: texture defaults to {@code minecraft:block/<name>}. */
        public Material(String name, Block base) {
            this(name, base, ResourceLocation.withDefaultNamespace("block/" + name));
        }

        /** Fresh, mutable properties copied from the base block. Call once per block. */
        public BlockBehaviour.Properties props() {
            return BlockBehaviour.Properties.ofFullCopy(base);
        }
    }

    /**
     * The stone-like materials. Every entry produces one block per stone block-type
     * (facade, quarter facade, fluted, fluted facade, sill, double sill, cornice,
     * crown molding, pillar base, pillar).
     */
    public static final List<Material> STONE = List.of(
            new Material("stone", Blocks.STONE),
            new Material("smooth_stone", Blocks.SMOOTH_STONE),
            new Material("cobblestone", Blocks.COBBLESTONE),
            new Material("mossy_cobblestone", Blocks.MOSSY_COBBLESTONE),
            new Material("bricks", Blocks.BRICKS),
            new Material("stone_bricks", Blocks.STONE_BRICKS),
            new Material("mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS),
            new Material("cracked_stone_bricks", Blocks.CRACKED_STONE_BRICKS),
            new Material("chiseled_stone_bricks", Blocks.CHISELED_STONE_BRICKS),
            new Material("obsidian", Blocks.OBSIDIAN),

            new Material("sandstone", Blocks.SANDSTONE),
            new Material("smooth_sandstone", Blocks.SMOOTH_SANDSTONE, ResourceLocation.withDefaultNamespace("block/sandstone_top")),
            new Material("chiseled_sandstone", Blocks.CHISELED_SANDSTONE),
            new Material("cut_sandstone", Blocks.CUT_SANDSTONE),
            new Material("red_sandstone", Blocks.RED_SANDSTONE),
            new Material("smooth_red_sandstone", Blocks.SMOOTH_RED_SANDSTONE, ResourceLocation.withDefaultNamespace("block/red_sandstone_top")),
            new Material("chiseled_red_sandstone", Blocks.CHISELED_RED_SANDSTONE),
            new Material("cut_red_sandstone", Blocks.CUT_RED_SANDSTONE),

            new Material("granite", Blocks.GRANITE),
            new Material("polished_granite", Blocks.POLISHED_GRANITE),
            new Material("diorite", Blocks.DIORITE),
            new Material("polished_diorite", Blocks.POLISHED_DIORITE),
            new Material("andesite", Blocks.ANDESITE),
            new Material("polished_andesite", Blocks.POLISHED_ANDESITE),

            new Material("blackstone", Blocks.BLACKSTONE),
            new Material("polished_blackstone", Blocks.POLISHED_BLACKSTONE),
            new Material("chiseled_polished_blackstone", Blocks.CHISELED_POLISHED_BLACKSTONE),
            new Material("gilded_blackstone", Blocks.GILDED_BLACKSTONE),
            new Material("polished_blackstone_bricks", Blocks.POLISHED_BLACKSTONE_BRICKS),
            new Material("cracked_polished_blackstone_bricks", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS),

            new Material("deepslate", Blocks.DEEPSLATE),
            new Material("deepslate_bricks", Blocks.DEEPSLATE_BRICKS),
            new Material("cracked_deepslate_bricks", Blocks.CRACKED_DEEPSLATE_BRICKS),
            new Material("cobbled_deepslate", Blocks.COBBLED_DEEPSLATE),
            new Material("polished_deepslate", Blocks.POLISHED_DEEPSLATE),
            new Material("chiseled_deepslate", Blocks.CHISELED_DEEPSLATE),
            new Material("deepslate_tiles", Blocks.DEEPSLATE_TILES),
            new Material("cracked_deepslate_tiles", Blocks.CRACKED_DEEPSLATE_TILES),

            new Material("tuff", Blocks.TUFF)
    );
}
