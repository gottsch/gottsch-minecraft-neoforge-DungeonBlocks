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
package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModMaterials;
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
            "arrow_slit",
            "brazier",
            "corbel",
            "cornice",
            "crown_molding",
////            "double_sill",
            "door",
            "facade",
            "fluted",
            "grate",
            "lantern",
            "ledge",
            "plate_bracket",
            "angle_plate_bracket",
            "pillar",
            "quarter",
            "sconce",
            "sewer",
            "sill",
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
            "sill",
            "large",
            "brick",
            "left",
            "right",
            "square",
            "chain"
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
        // Stone materials (texture + base block) come from the single source of truth,
        // ModMaterials.STONE. They are added to t2/m2 directly at the end of this constructor.
        // Only the non-stone (misc + wood) textures are declared here.
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
        t.put(Blocks.STRIPPED_CHERRY_WOOD, mcLoc("block/stripped_cherry_wood"));
        t.put(Blocks.STRIPPED_DARK_OAK_WOOD, mcLoc("block/stripped_dark_oak_wood"));
        t.put(Blocks.STRIPPED_JUNGLE_WOOD, mcLoc("block/stripped_jungle_wood"));
        t.put(Blocks.STRIPPED_MANGROVE_WOOD, mcLoc("block/stripped_mangrove_wood"));
        t.put(Blocks.STRIPPED_OAK_WOOD, mcLoc("block/stripped_oak_wood"));
        t.put(Blocks.STRIPPED_SPRUCE_WOOD, mcLoc("block/stripped_spruce_wood"));

        t.forEach((k, v) -> t2.put(k.getDescriptionId().split("\\.")[2], v));
        // custom mappings
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

        t.forEach((k, v) -> m2.put(k.getDescriptionId().split("\\.")[2], k));
        // custom mappings
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
        m2.put("stripped_dark_oak", Blocks.STRIPPED_DARK_OAK_LOG);
        m2.put("stripped_jungle", Blocks.STRIPPED_JUNGLE_LOG);
        m2.put("stripped_mangrove", Blocks.STRIPPED_MANGROVE_LOG);
        m2.put("stripped_oak", Blocks.STRIPPED_OAK_LOG);
        m2.put("stripped_spruce", Blocks.STRIPPED_SPRUCE_LOG);

        // stone materials: single source of truth (texture + base block) is ModMaterials.STONE
        ModMaterials.STONE.forEach(material -> {
            t2.put(material.name(), material.texture());
            m2.put(material.name(), material.base());
        });
    }

    public ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, name);
    }

    public ResourceLocation mcLoc(String name) {
        return ResourceLocation.parse(name);
    }
}
