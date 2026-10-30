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

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
     * for every block made from this material, the texture used when generating models, and the
     * ingredient recipes and the stonecutter take for it.
     *
     * <p>The texture defaults to {@code minecraft:block/<name>} but can be overridden (e.g.
     * smooth sandstone uses the {@code sandstone_top} texture). The ingredient defaults to
     * {@code base}; it is a supplier because some ingredients are this mod's own blocks.
     */
    public record Material(String name, Block base, ResourceLocation texture, Supplier<Block> ingredient) {
        /** Convenience: texture defaults to {@code minecraft:block/<name>}. */
        public Material(String name, Block base) {
            this(name, base, ResourceLocation.withDefaultNamespace("block/" + name));
        }

        public Material(String name, Block base, ResourceLocation texture) {
            this(name, base, texture, () -> base);
        }

        /** Fresh, mutable properties copied from the base block. Call once per block. */
        public BlockBehaviour.Properties props() {
            return BlockBehaviour.Properties.ofFullCopy(base);
        }
    }

    /**
     * Texture owned by this mod rather than vanilla. Used by the materials that have no vanilla
     * block behind them — the mossy deepslates, which vanilla does not ship in any form.
     */
    private static ResourceLocation modTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/" + name);
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
            // vanilla ships no mossy deepslate of any kind, so these three take their properties
            // from the plain block they are moss over, and their textures from this mod
            // (tools/gen_mossy_textures.py). Their ingredient is the mod's own mossy block, so a
            // stonecutter recipe can never make mossy output from plain deepslate.
            new Material("mossy_deepslate_bricks", Blocks.DEEPSLATE_BRICKS, modTexture("mossy_deepslate_bricks"),
                    () -> ModBlocks.MOSSY_DEEPSLATE_BRICKS.get()),
            new Material("cracked_deepslate_bricks", Blocks.CRACKED_DEEPSLATE_BRICKS),
            new Material("cobbled_deepslate", Blocks.COBBLED_DEEPSLATE),
            new Material("mossy_cobbled_deepslate", Blocks.COBBLED_DEEPSLATE, modTexture("mossy_cobbled_deepslate"),
                    () -> ModBlocks.MOSSY_COBBLED_DEEPSLATE.get()),
            new Material("polished_deepslate", Blocks.POLISHED_DEEPSLATE),
            new Material("chiseled_deepslate", Blocks.CHISELED_DEEPSLATE),
            new Material("deepslate_tiles", Blocks.DEEPSLATE_TILES),
            new Material("mossy_deepslate_tiles", Blocks.DEEPSLATE_TILES, modTexture("mossy_deepslate_tiles"),
                    () -> ModBlocks.MOSSY_DEEPSLATE_TILES.get()),
            new Material("cracked_deepslate_tiles", Blocks.CRACKED_DEEPSLATE_TILES),

            new Material("tuff", Blocks.TUFF)
    );

    /**
     * The woods corbels are made in. Planks are the planks; a stripped wood takes its properties
     * from the stripped wood block but is textured and crafted as the stripped log. Stripped
     * mangrove copies plain MANGROVE_WOOD, as it always has.
     */
    public static final List<Material> WOOD = List.of(
            plank("acacia", Blocks.ACACIA_PLANKS), plank("birch", Blocks.BIRCH_PLANKS),
            plank("cherry", Blocks.CHERRY_PLANKS), plank("dark_oak", Blocks.DARK_OAK_PLANKS),
            plank("jungle", Blocks.JUNGLE_PLANKS), plank("mangrove", Blocks.MANGROVE_PLANKS),
            plank("oak", Blocks.OAK_PLANKS), plank("spruce", Blocks.SPRUCE_PLANKS),
            stripped("acacia", Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG),
            stripped("birch", Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG),
            stripped("cherry", Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG),
            stripped("dark_oak", Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG),
            stripped("jungle", Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG),
            stripped("mangrove", Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG),
            stripped("oak", Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG),
            stripped("spruce", Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG));

    private static Material plank(String wood, Block planks) {
        return new Material(wood, planks, ResourceLocation.parse("block/" + wood + "_planks"));
    }

    private static Material stripped(String wood, Block props, Block log) {
        return new Material("stripped_" + wood, props, ResourceLocation.parse("block/stripped_" + wood + "_log"), () -> log);
    }

    /** Materials only a block-type or two use: barred windows (terracotta), ledges (concrete). */
    public static final List<Material> OTHER = List.of(
            new Material("terracotta", Blocks.TERRACOTTA),
            // drawn with plain stone's texture
            new Material("light_gray_concrete", Blocks.LIGHT_GRAY_CONCRETE, ResourceLocation.parse("block/stone")));

    private static final Map<String, Material> BY_NAME = new LinkedHashMap<>();

    static {
        Stream.of(STONE, WOOD, OTHER).flatMap(List::stream).forEach(m -> BY_NAME.put(m.name(), m));
    }

    /** The material of this name, from {@link #STONE}, {@link #WOOD} or {@link #OTHER}. */
    public static Material get(String name) {
        Material material = BY_NAME.get(name);
        if (material == null) {
            throw new IllegalArgumentException("no material " + name);
        }
        return material;
    }

    /*
     * The block-types only some materials have, each listing its materials in the order its
     * blocks are registered - which is also their order in the creative tab.
     */

    public static final List<String> BARRED_WINDOWS = List.of(
            "stone", "smooth_stone", "cobblestone", "mossy_cobblestone", "bricks", "stone_bricks",
            "mossy_stone_bricks", "cracked_stone_bricks", "chiseled_stone_bricks", "obsidian",
            "sandstone", "smooth_sandstone", "chiseled_sandstone", "cut_sandstone",
            "red_sandstone", "smooth_red_sandstone", "chiseled_red_sandstone", "cut_red_sandstone",
            "granite", "andesite", "diorite", "polished_granite", "polished_andesite", "polished_diorite",
            "blackstone", "polished_blackstone", "polished_blackstone_bricks",
            "deepslate", "deepslate_bricks", "cobbled_deepslate", "polished_deepslate", "deepslate_tiles",
            "terracotta");

    public static final List<String> LEDGES = List.of(
            "stone", "smooth_stone", "cobblestone", "mossy_cobblestone", "bricks", "stone_bricks",
            "mossy_stone_bricks", "light_gray_concrete",
            "granite", "andesite", "diorite", "polished_granite", "polished_andesite", "polished_diorite",
            "blackstone", "polished_blackstone", "polished_blackstone_bricks",
            "deepslate", "deepslate_bricks", "cobbled_deepslate", "polished_deepslate", "deepslate_tiles");

    /**
     * Ledges that do not copy their own material's properties, and what they copy instead: the
     * stone-like ones plain stone, the deepslate ones plain deepslate. Kept as they have always
     * been, since it decides their hardness and map colour.
     */
    public static final Map<String, Block> LEDGE_PROPS = Map.ofEntries(
            Map.entry("smooth_stone", Blocks.STONE), Map.entry("cobblestone", Blocks.STONE),
            Map.entry("mossy_cobblestone", Blocks.STONE), Map.entry("bricks", Blocks.STONE),
            Map.entry("stone_bricks", Blocks.STONE), Map.entry("mossy_stone_bricks", Blocks.STONE),
            Map.entry("light_gray_concrete", Blocks.STONE),
            Map.entry("deepslate_bricks", Blocks.DEEPSLATE), Map.entry("cobbled_deepslate", Blocks.DEEPSLATE),
            Map.entry("polished_deepslate", Blocks.DEEPSLATE), Map.entry("deepslate_tiles", Blocks.DEEPSLATE));

    public static final List<String> CORBELS = List.of(
            "acacia", "birch", "cherry", "dark_oak", "jungle", "mangrove", "oak", "spruce",
            "stripped_acacia", "stripped_birch", "stripped_cherry", "stripped_dark_oak",
            "stripped_jungle", "stripped_mangrove", "stripped_oak", "stripped_spruce",
            "stone", "smooth_stone", "cobblestone", "mossy_cobblestone", "stone_bricks", "mossy_stone_bricks",
            "granite", "andesite", "diorite", "polished_granite", "polished_andesite", "polished_diorite",
            "blackstone", "polished_blackstone", "polished_blackstone_bricks",
            "deepslate", "deepslate_bricks", "cobbled_deepslate", "polished_deepslate", "deepslate_tiles");
}
