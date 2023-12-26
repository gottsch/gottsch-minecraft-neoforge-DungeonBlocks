/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.dungeonblocks.datagen;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Requires instantiation as to not occupy memory when it is only used during data generation.
 * @author Mark Gottschling on Nov 17, 2023
 *
 */
public class DataGenMaps {
    public Map<String, Block> m2 = new HashMap<>();

    // map from block to texture resource location
    public Map<Block, ResourceLocation> t = new HashMap<>();

    public Map<String, ResourceLocation> t2 = new HashMap<>();

    public List<String> names = Arrays.asList(
            "barred_window",
            "brazier",
            "corbel",
////            "cornice",
////            "crown_molding",
////            "double_sill",
            "door",
////            "facade",
////            "fluted",
            "grate",
//            "keystone",
            "lantern",
            "ledge",
            "plate_bracket",
////            "pillar",
////            "quarter",
            "sconce",
            "sewer",
////            "sill",
            "hay",
            "wall_ring"
    );

    public List<String> stone_blocks = Arrays.asList(
            "barred_window",
            "ledge",
            "greek",
            "corbel",
            "cornice",
            "crown_molding",
            "double_sill",
            "facade",
            "fluted",
            "pillar",
            "quarter",
            "sill"
            );

    public List<String> wood_names = Arrays.asList(
            "acacia",
            "birch",
            "cherry",
            "dark_oak",
            "jungle",
            "mangrove",
            "oak",
            "spruce",
            "stripped_acacia",
            "stripped_birch",
            "stripped_cherry",
            "stripped_dark_oak",
            "stripped_jungle",
            "stripped_mangrove",
            "stripped_oak",
            "stripped_spruce"
    );

    /**
     *
     */
    public DataGenMaps() {
        t.put(Blocks.STONE, mcLoc("block/stone"));
        t.put(Blocks.SMOOTH_STONE, mcLoc("block/smooth_stone"));
        t.put(Blocks.COBBLESTONE, mcLoc("block/cobblestone"));
        t.put(Blocks.MOSSY_COBBLESTONE, mcLoc("block/mossy_cobblestone"));
        t.put(Blocks.BRICKS, mcLoc("block/bricks"));
        t.put(Blocks.STONE_BRICKS, mcLoc("block/stone_bricks"));
        t.put(Blocks.MOSSY_STONE_BRICKS, mcLoc("block/mossy_stone_bricks"));
        t.put(Blocks.CRACKED_STONE_BRICKS, mcLoc("block/cracked_stone_bricks"));
        t.put(Blocks.CHISELED_STONE_BRICKS, mcLoc("block/chiseled_stone_bricks"));
        t.put(Blocks.OBSIDIAN, mcLoc("block/obsidian"));

        t.put(Blocks.SANDSTONE, mcLoc("block/sandstone"));
        t.put(Blocks.CHISELED_SANDSTONE, mcLoc("block/chiseled_sandstone"));
        t.put(Blocks.CUT_SANDSTONE, mcLoc("block/cut_sandstone"));
        t.put(Blocks.RED_SANDSTONE, mcLoc("block/red_sandstone"));
        t.put(Blocks.CHISELED_RED_SANDSTONE, mcLoc("block/chiseled_red_sandstone"));
        t.put(Blocks.CUT_RED_SANDSTONE, mcLoc("block/cut_red_sandstone"));

        t.put(Blocks.GRANITE, mcLoc("block/granite"));
        t.put(Blocks.ANDESITE, mcLoc("block/andesite"));
        t.put(Blocks.DIORITE, mcLoc("block/diorite"));
        t.put(Blocks.POLISHED_GRANITE, mcLoc("block/polished_granite"));
        t.put(Blocks.POLISHED_ANDESITE, mcLoc("block/polished_andesite"));
        t.put(Blocks.POLISHED_DIORITE, mcLoc("block/polished_diorite"));

        t.put(Blocks.BLACKSTONE, mcLoc("block/blackstone"));
        t.put(Blocks.POLISHED_BLACKSTONE, mcLoc("block/polished_blackstone"));
        t.put(Blocks.CHISELED_POLISHED_BLACKSTONE, mcLoc("block/chiseled_polished_blackstone"));
        t.put(Blocks.GILDED_BLACKSTONE, mcLoc("block/gilded_blackstone"));
        t.put(Blocks.POLISHED_BLACKSTONE_BRICKS, mcLoc("block/polished_blackstone_bricks"));
        t.put(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, mcLoc("block/cracked_polished_blackstone_bricks"));

        t.put(Blocks.DEEPSLATE, mcLoc("block/deepslate"));
        t.put(Blocks.DEEPSLATE_BRICKS, mcLoc("block/deepslate_bricks"));
        t.put(Blocks.CRACKED_DEEPSLATE_BRICKS, mcLoc("block/cracked_deepslate_bricks"));
        t.put(Blocks.COBBLED_DEEPSLATE, mcLoc("block/cobbled_deepslate"));
        t.put(Blocks.POLISHED_DEEPSLATE, mcLoc("block/polished_deepslate"));
        t.put(Blocks.CHISELED_DEEPSLATE, mcLoc("block/chiseled_deepslate"));
        t.put(Blocks.DEEPSLATE_TILES, mcLoc("block/deepslate_tiles"));
        t.put(Blocks.CRACKED_DEEPSLATE_TILES, mcLoc("block/cracked_deepslate_tiles"));

        t.put(Blocks.TERRACOTTA, mcLoc("block/terracotta"));
        t.put(Blocks.LIGHT_GRAY_CONCRETE, mcLoc("block/stone"));
        t.put(Blocks.POLISHED_BASALT, mcLoc("block/polished_basalt"));

        t.put(Blocks.ACACIA_PLANKS, mcLoc("block/acacia_planks"));
        t.put(Blocks.BIRCH_PLANKS, mcLoc("block/birch_planks"));
        t.put(Blocks.CHERRY_PLANKS, mcLoc("block/cherry_planks"));
        t.put(Blocks.DARK_OAK_PLANKS, mcLoc("block/dark_oak_planks"));
        t.put(Blocks.JUNGLE_PLANKS, mcLoc("block/jungle_planks"));
        t.put(Blocks.MANGROVE_PLANKS, mcLoc("block/mangrove_planks"));
        t.put(Blocks.OAK_PLANKS, mcLoc("block/oak_planks"));
        t.put(Blocks.SPRUCE_PLANKS, mcLoc("block/spruce_planks"));

        t.put(Blocks.STRIPPED_ACACIA_WOOD, mcLoc("block/stripped_acacia_wood"));
        t.put(Blocks.STRIPPED_BIRCH_WOOD, mcLoc("block/stripped_birch_wood"));
        t.put(Blocks.STRIPPED_CHERRY_WOOD, mcLoc("block/cstripped_herry_wood"));
        t.put(Blocks.STRIPPED_DARK_OAK_WOOD, mcLoc("block/stripped_dark_oak_wood"));
        t.put(Blocks.STRIPPED_JUNGLE_WOOD, mcLoc("block/stripped_jungle_wood"));
        t.put(Blocks.STRIPPED_MANGROVE_WOOD, mcLoc("block/stripped_mangrove_wood"));
        t.put(Blocks.STRIPPED_OAK_WOOD, mcLoc("block/stripped_oak_wood"));
        t.put(Blocks.STRIPPED_SPRUCE_WOOD, mcLoc("block/stripped_spruce_wood"));

        t.forEach((k, v) -> {
            DungeonBlocks.LOGGER.info("t2 mapping {} to", k.getDescriptionId());
            t2.put(k.getDescriptionId().split("\\.")[2], v);
        });
        // custom mappings
        t2.put("smooth_sandstone", mcLoc("block/sandstone_top"));
        t2.put("smooth_red_sandstone", mcLoc("block/red_sandstone_top"));
        t2.put("acacia", mcLoc("block/acacia_planks"));
        t2.put("birch", mcLoc("block/birch_planks"));
        t2.put("cherry", mcLoc("block/cherry_planks"));
        t2.put("dark_oak", mcLoc("block/dark_oak_planks"));
        t2.put("jungle", mcLoc("block/jungle_planks"));
        t2.put("mangrove", mcLoc("block/mangrove_planks"));
        t2.put("oak", mcLoc("block/oak_planks"));
        t2.put("spruce", mcLoc("block/spruce_planks"));
        t2.put("stripped_acacia", mcLoc("block/stripped_acacia_log"));
        t2.put("stripped_birch", mcLoc("block/stripped_birch_log"));
        t2.put("stripped_cherry", mcLoc("block/stripped_cherry_log"));
        t2.put("stripped_dark_oak", mcLoc("block/stripped_dark_oak_log"));
        t2.put("stripped_jungle", mcLoc("block/stripped_jungle_log"));
        t2.put("stripped_mangrove", mcLoc("block/stripped_mangrove_log"));
        t2.put("stripped_oak", mcLoc("block/stripped_oak_log"));
        t2.put("stripped_spruce", mcLoc("block/stripped_spruce_log"));

        t.forEach((k, v) -> {
            DungeonBlocks.LOGGER.info("m2 mapping {} to", k.getDescriptionId());
            m2.put(k.getDescriptionId().split("\\.")[2], k);
        });
        // custom mappings
        m2.put("smooth_sandstone", Blocks.SANDSTONE);
        m2.put("smooth_red_sandstone", Blocks.RED_SANDSTONE);
        m2.put("acacia", Blocks.ACACIA_PLANKS);
        m2.put("birch", Blocks.BIRCH_PLANKS);
        m2.put("cherry", Blocks.CHERRY_PLANKS);
        m2.put("dark_oak", Blocks.DARK_OAK_PLANKS);
        m2.put("jungle", Blocks.JUNGLE_PLANKS);
        m2.put("mangrove", Blocks.MANGROVE_PLANKS);
        m2.put("oak", Blocks.OAK_PLANKS);
        m2.put("spruce", Blocks.SPRUCE_PLANKS);
        m2.put("stripped_acacia", Blocks.STRIPPED_ACACIA_LOG);
        m2.put("stripped_birch", Blocks.STRIPPED_BIRCH_LOG);
        m2.put("stripped_cherry", Blocks.STRIPPED_CHERRY_LOG);
        m2.put("stripped_dark_oak", Blocks.STRIPPED_OAK_LOG);
        m2.put("stripped_jungle", Blocks.STRIPPED_JUNGLE_WOOD);
        m2.put("stripped_mangrove", Blocks.STRIPPED_MANGROVE_LOG);
        m2.put("stripped_oak", Blocks.STRIPPED_OAK_LOG);
        m2.put("stripped_spruce", Blocks.STRIPPED_SPRUCE_LOG);
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(DungeonBlocks.MOD_ID, name);
    }

    public ResourceLocation mcLoc(String name) {
        return new ResourceLocation(name);
    }

    // TODO this m map can be replace with the same type of code as ModelBlockStateProvider
    // ie Registration.BLOCKS.getEntries().stream().filter()...

    // TODO put into constructor
//    static {
//        m.put(Blocks.STONE, Arrays.asList(
//                ModBlocks.STONE_CORNICE,
//                ModBlocks.STONE_CROWN_MOLDING,
//                ModBlocks.STONE_DOUBLE_SILL,
//                ModBlocks.STONE_FACADE,
//                ModBlocks.STONE_FLUTED,
//                ModBlocks.STONE_FLUTED_FACADE,
//                ModBlocks.STONE_PILLAR,
//                ModBlocks.STONE_PILLAR_BASE,
//                ModBlocks.STONE_QUARTER_FACADE,
//                ModBlocks.STONE_SILL,
//                ModBlocks.STONE_GREEK_BLOCK,
//                CorbelBlocks.STONE_CORBEL,
//                LedgeBlocks.STONE_LEDGE
////                KeystoneBlocks.STONE_KEYSTONE,
////                KeystoneBlocks.STONE_KEYSTONE_SLAB
//        ));
//        m.put(Blocks.SMOOTH_STONE, Arrays.asList(
////					ModBlocks.SMOOTH_STONE_CORNICE
////                KeystoneBlocks.SMOOTH_STONE_KEYSTONE,
////                KeystoneBlocks.SMOOTH_STONE_KEYSTONE_SLAB
//                CorbelBlocks.SMOOTH_STONE_CORBEL,
//                LedgeBlocks.SMOOTH_STONE_LEDGE
//        ));
//        m.put(Blocks.COBBLESTONE, Arrays.asList(
////                KeystoneBlocks.COBBLESTONE_KEYSTONE,
////                KeystoneBlocks.COBBLESTONE_KEYSTONE_SLAB
//                CorbelBlocks.COBBLESTONE_CORBEL,
//                LedgeBlocks.COBBLESTONE_LEDGE
//        ));
//        m.put(Blocks.MOSSY_COBBLESTONE, Arrays.asList(
////                KeystoneBlocks.MOSSY_COBBLESTONE_KEYSTONE,
////                KeystoneBlocks.MOSSY_COBBLESTONE_KEYSTONE_SLAB
//                CorbelBlocks.MOSSY_COBBLESTONE_CORBEL,
//                LedgeBlocks.MOSSY_COBBLESTONE_LEDGE
//        ));
//        m.put(Blocks.BRICKS, Arrays.asList(
////                KeystoneBlocks.BRICKS_KEYSTONE,
////                KeystoneBlocks.BRICKS_KEYSTONE_SLAB
//
//                LedgeBlocks.BRICKS_LEDGE
//        ));
//        m.put(Blocks.STONE_BRICKS, Arrays.asList(
////                KeystoneBlocks.STONE_BRICKS_KEYSTONE,
////                KeystoneBlocks.STONE_BRICKS_KEYSTONE_SLAB
//                CorbelBlocks.STONE_BRICKS_CORBEL,
//                LedgeBlocks.STONE_BRICKS_LEDGE
//        ));
//        m.put(Blocks.MOSSY_STONE_BRICKS, Arrays.asList(
////                KeystoneBlocks.MOSSY_STONE_BRICKS_KEYSTONE,
////                KeystoneBlocks.MOSSY_STONE_BRICKS_KEYSTONE_SLAB
//                CorbelBlocks.MOSSY_STONE_BRICKS_CORBEL,
//                LedgeBlocks.MOSSY_STONE_BRICKS_LEDGE
//        ));
//        m.put(Blocks.CRACKED_STONE_BRICKS, Arrays.asList(
////                KeystoneBlocks.CRACKED_STONE_BRICKS_KEYSTONE,
////                KeystoneBlocks.CRACKED_STONE_BRICKS_KEYSTONE_SLAB
//
//        ));
//        m.put(Blocks.CHISELED_STONE_BRICKS, Arrays.asList(
////                KeystoneBlocks.CHISELED_STONE_BRICKS_KEYSTONE,
////                KeystoneBlocks.CHISELED_STONE_BRICKS_KEYSTONE_SLAB
//        ));
//        m.put(Blocks.OBSIDIAN, Arrays.asList(
////                KeystoneBlocks.OBSIDIAN_KEYSTONE,
////                KeystoneBlocks.OBSIDIAN_KEYSTONE_SLAB
//        ));
//
//        // TODO add all the SANDs
//
//        m.put(Blocks.GRANITE, Arrays.asList(
//                CorbelBlocks.GRANITE_CORBEL,
//                LedgeBlocks.GRANITE_LEDGE
//
//        ));
//        m.put(Blocks.ANDESITE, Arrays.asList(
//                ModBlocks.ANDESITE_GREEK_BLOCK,
//                CorbelBlocks.ANDESITE_CORBEL,
//                LedgeBlocks.ANDESITE_LEDGE
//
//        ));
//        m.put(Blocks.DIORITE, Arrays.asList(
//                CorbelBlocks.DIORITE_CORBEL,
//                LedgeBlocks.DIORITE_LEDGE
//        ));
//        m.put(Blocks.POLISHED_GRANITE, Arrays.asList(
//                CorbelBlocks.POLISHED_GRANITE_CORBEL,
//                LedgeBlocks.POLISHED_GRANITE_LEDGE
//
//        ));
//        m.put(Blocks.POLISHED_ANDESITE, Arrays.asList(
//                CorbelBlocks.POLISHED_ANDESITE_CORBEL,
//                LedgeBlocks.POLISHED_ANDESITE_LEDGE
//
//        ));
//        m.put(Blocks.POLISHED_DIORITE, Arrays.asList(
////                KeystoneBlocks.POLISHED_DIORITE_KEYSTONE,
////                KeystoneBlocks.POLISHED_DIORITE_KEYSTONE_SLAB
//                CorbelBlocks.POLISHED_DIORITE_CORBEL,
//                LedgeBlocks.POLISHED_DIORITE_LEDGE
//        ));
//
//        m.put(Blocks.BLACKSTONE, Arrays.asList(
//                CorbelBlocks.BLACKSTONE_CORBEL,
//                LedgeBlocks.BLACKSTONE_LEDGE
//        ));
//        m.put(Blocks.POLISHED_BLACKSTONE, Arrays.asList(
//                CorbelBlocks.POLISHED_BLACKSTONE_CORBEL,
//                LedgeBlocks.POLISHED_BLACKSTONE_LEDGE
//        ));
//        m.put(Blocks.POLISHED_BLACKSTONE_BRICKS, Arrays.asList(
//                CorbelBlocks.POLISHED_BLACKSTONE_BRICKS_CORBEL,
//                LedgeBlocks.POLISHED_BLACKSTONE_BRICKS_LEDGE
//        ));
//
//        m.put(Blocks.DEEPSLATE, Arrays.asList(
//                CorbelBlocks.DEEPSLATE_CORBEL,
//                LedgeBlocks.DEEPSLATE_LEDGE
//        ));
//        m.put(Blocks.DEEPSLATE_BRICKS, Arrays.asList(
//                CorbelBlocks.DEEPSLATE_BRICKS_CORBEL,
//                LedgeBlocks.DEEPSLATE_BRICKS_LEDGE
//        ));
//        m.put(Blocks.COBBLED_DEEPSLATE, Arrays.asList(
//                CorbelBlocks.COBBLED_DEEPSLATE_CORBEL,
//                LedgeBlocks.COBBLED_DEEPSLATE_LEDGE
//        ));
//        m.put(Blocks.POLISHED_DEEPSLATE, Arrays.asList(
//                CorbelBlocks.POLISHED_DEEPSLATE_CORBEL,
//                LedgeBlocks.POLISHED_DEEPSLATE_LEDGE
//        ));
//        m.put(Blocks.DEEPSLATE_TILES, Arrays.asList(
//                CorbelBlocks.DEEPSLATE_TILES_CORBEL,
//                LedgeBlocks.DEEPSLATE_TILES_LEDGE
//        ));
//
//        // one-offs
//        m.put(Blocks.TERRACOTTA, Arrays.asList(
//                ModBlocks.TERRACOTTA_SEWER
//        ));
//        m.put(Blocks.POLISHED_BASALT, Arrays.asList(
//                ModBlocks.POLISHED_BASALT_GREEK_BLOCK
//        ));
//        m.put(Blocks.LIGHT_GRAY_CONCRETE, Arrays.asList(
////                KeystoneBlocks.LIGHT_GRAY_CONCRETE_KEYSTONE,
////                KeystoneBlocks.LIGHT_GRAY_CONCRETE_KEYSTONE_SLAB
//        ));
//    }
}
