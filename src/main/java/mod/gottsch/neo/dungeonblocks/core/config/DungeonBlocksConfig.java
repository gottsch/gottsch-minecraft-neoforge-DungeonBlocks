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
package mod.gottsch.neo.dungeonblocks.core.config;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.gottschcore.config.AbstractConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.common.Mod.EventBusSubscriber;

/**
 * 
 * @author Mark Gottschling on Jan 5, 2020
 *
 */
public class DungeonBlocksConfig extends AbstractConfig {
	protected static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
	protected static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

	public static ModConfigSpec COMMON_CONFIG;
	public static ModConfigSpec CLIENT_CONFIG;

	public static DungeonBlocksConfig instance = new DungeonBlocksConfig();
	
	static {
		COMMON_CONFIG = COMMON_BUILDER.build();
	}

	public DungeonBlocksConfig() {
	}

	public static class BlockID {
		// facade
		public static final String STONE_FACADE_ID = "stone_facade_block";
		public static final String SMOOTH_STONE_FACADE_ID = "smooth_stone_facade_block";
		public static final String COBBLESTONE_FACADE_ID = "cobblestone_facade_block";
		public static final String MOSSY_COBBLESTONE_FACADE_ID = "mossy_cobblestone_facade_block";
		public static final String BRICKS_FACADE_ID = "bricks_facade_block";
		public static final String STONE_BRICKS_FACADE_ID = "stone_bricks_facade_block";
		public static final String MOSSY_STONE_BRICKS_FACADE_ID = "mossy_stone_bricks_facade_block";
		public static final String CRACKED_STONE_BRICKS_FACADE_ID = "cracked_stone_bricks_facade_block";
		public static final String CHISELED_STONE_BRICKS_FACADE_ID = "chiseled_stone_bricks_facade_block";
		public static final String OBSIDIAN_FACADE_ID = "obsidian_facade_block";

		public static final String SANDSTONE_FACADE_ID = "sandstone_facade_block";
		public static final String SMOOTH_SANDSTONE_FACADE_ID = "smooth_sandstone_facade_block";
		public static final String CHISELED_SANDSTONE_FACADE_ID = "chiseled_sandstone_facade_block";
		public static final String CUT_SANDSTONE_FACADE_ID = "cut_sandstone_facade_block";
		public static final String RED_SANDSTONE_FACADE_ID = "red_sandstone_facade_block";
		public static final String SMOOTH_RED_SANDSTONE_FACADE_ID = "smooth_red_sandstone_facade_block";
		public static final String CHISELED_RED_SANDSTONE_FACADE_ID = "chiseled_red_sandstone_facade_block";
		public static final String CUT_RED_SANDSTONE_FACADE_ID = "cut_red_sandstone_facade_block";

		public static final String GRANITE_FACADE_ID = "granite_facade_block";
		public static final String POLISHED_GRANITE_FACADE_ID = "polished_granite_facade_block";
		public static final String DIORITE_FACADE_ID = "diorite_facade_block";
		public static final String POLISHED_DIORITE_FACADE_ID = "polished_diorite_facade_block";
		public static final String ANDESITE_FACADE_ID = "andesite_facade_block";
		public static final String POLISHED_ANDESITE_FACADE_ID = "polished_andesite_facade_block";
		
		public static final String BLACKSTONE_FACADE_ID = "blackstone_facade_block";
		public static final String POLISHED_BLACKSTONE_FACADE_ID = "polished_blackstone_facade_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_FACADE_ID = "chiseled_polished_blackstone_facade_block";
		public static final String GILDED_BLACKSTONE_FACADE_ID = "gilded_blackstone_facade_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_FACADE_ID = "polished_blackstone_bricks_facade_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_FACADE_ID = "cracked_polished_blackstone_bricks_facade_block";
		
		public static final String DEEPSLATE_FACADE_ID = "deepslate_facade_block";
		public static final String DEEPSLATE_BRICKS_FACADE_ID = "deepslate_bricks_facade_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_FACADE_ID = "cracked_deepslate_bricks_facade_block";
		public static final String COBBLED_DEEPSLATE_FACADE_ID = "cobbled_deepslate_facade_block";
		public static final String POLISHED_DEEPSLATE_FACADE_ID = "polished_deepslate_facade_block";
		public static final String CHISELED_DEEPSLATE_FACADE_ID = "chiseled_deepslate_facade_block";
		public static final String DEEPSLATE_TILES_FACADE_ID = "deepslate_tiles_facade_block";
		public static final String CRACKED_DEEPSLATE_TILES_FACADE_ID = "cracked_deepslate_tiles_facade_block";

		// quarter
		public static final String STONE_QUARTER_FACADE_ID = "stone_quarter_facade_block";
		public static final String SMOOTH_STONE_QUARTER_FACADE_ID = "smooth_stone_quarter_facade_block";
		public static final String COBBLESTONE_QUARTER_FACADE_ID = "cobblestone_quarter_facade_block";
		public static final String MOSSY_COBBLESTONE_QUARTER_FACADE_ID = "mossy_cobblestone_quarter_facade_block";
		public static final String BRICKS_QUARTER_FACADE_ID = "bricks_quarter_facade_block";
		public static final String STONE_BRICKS_QUARTER_FACADE_ID = "stone_bricks_quarter_facade_block";
		public static final String MOSSY_STONE_BRICKS_QUARTER_FACADE_ID = "mossy_stone_bricks_quarter_facade_block";
		public static final String CRACKED_STONE_BRICKS_QUARTER_FACADE_ID = "cracked_stone_bricks_quarter_facade_block";
		public static final String CHISELED_STONE_BRICKS_QUARTER_FACADE_ID = "chiseled_stone_bricks_quarter_facade_block";
		public static final String OBSIDIAN_QUARTER_FACADE_ID = "obsidian_quarter_facade_block";
		public static final String SANDSTONE_QUARTER_FACADE_ID = "sandstone_quarter_facade_block";
		public static final String SMOOTH_SANDSTONE_QUARTER_FACADE_ID = "smooth_sandstone_quarter_facade_block";
		public static final String CHISELED_SANDSTONE_QUARTER_FACADE_ID = "chiseled_sandstone_quarter_facade_block";
		public static final String CUT_SANDSTONE_QUARTER_FACADE_ID = "cut_sandstone_quarter_facade_block";
		public static final String RED_SANDSTONE_QUARTER_FACADE_ID = "red_sandstone_quarter_facade_block";
		public static final String SMOOTH_RED_SANDSTONE_QUARTER_FACADE_ID = "smooth_red_sandstone_quarter_facade_block";
		public static final String CHISELED_RED_SANDSTONE_QUARTER_FACADE_ID = "chiseled_red_sandstone_quarter_facade_block";
		public static final String CUT_RED_SANDSTONE_QUARTER_FACADE_ID = "cut_red_sandstone_quarter_facade_block";
		public static final String GRANITE_QUARTER_FACADE_ID = "granite_quarter_facade_block";
		public static final String POLISHED_GRANITE_QUARTER_FACADE_ID = "polished_granite_quarter_facade_block";
		public static final String DIORITE_QUARTER_FACADE_ID = "diorite_quarter_facade_block";
		public static final String POLISHED_DIORITE_QUARTER_FACADE_ID = "polished_diorite_quarter_facade_block";
		public static final String ANDESITE_QUARTER_FACADE_ID = "andesite_quarter_facade_block";
		public static final String POLISHED_ANDESITE_QUARTER_FACADE_ID = "polished_andesite_quarter_facade_block";
		public static final String BLACKSTONE_QUARTER_FACADE_ID = "blackstone_quarter_facade_block";
		public static final String POLISHED_BLACKSTONE_QUARTER_FACADE_ID = "polished_blackstone_quarter_facade_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_QUARTER_FACADE_ID = "chiseled_polished_blackstone_quarter_facade_block";
		public static final String GILDED_BLACKSTONE_QUARTER_FACADE_ID = "gilded_blackstone_quarter_facade_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE_ID = "polished_blackstone_bricks_quarter_facade_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE_ID = "cracked_polished_blackstone_bricks_quarter_facade_block";
		public static final String DEEPSLATE_QUARTER_FACADE_ID = "deepslate_quarter_facade_block";
		public static final String DEEPSLATE_BRICKS_QUARTER_FACADE_ID = "deepslate_bricks_quarter_facade_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_QUARTER_FACADE_ID = "cracked_deepslate_bricks_quarter_facade_block";
		public static final String COBBLED_DEEPSLATE_QUARTER_FACADE_ID = "cobbled_deepslate_quarter_facade_block";
		public static final String POLISHED_DEEPSLATE_QUARTER_FACADE_ID = "polished_deepslate_quarter_facade_block";
		public static final String CHISELED_DEEPSLATE_QUARTER_FACADE_ID = "chiseled_deepslate_quarter_facade_block";
		public static final String DEEPSLATE_TILES_QUARTER_FACADE_ID = "deepslate_tiles_quarter_facade_block";
		public static final String CRACKED_DEEPSLATE_TILES_QUARTER_FACADE_ID = "cracked_deepslate_tiles_quarter_facade_block";
		
		// fluted
		public static final String STONE_FLUTED_ID = "stone_fluted_block";
		public static final String SMOOTH_STONE_FLUTED_ID = "smooth_stone_fluted_block";
		public static final String COBBLESTONE_FLUTED_ID = "cobblestone_fluted_block";
		public static final String MOSSY_COBBLESTONE_FLUTED_ID = "mossy_cobblestone_fluted_block";
		public static final String BRICKS_FLUTED_ID = "bricks_fluted_block";
		public static final String STONE_BRICKS_FLUTED_ID = "stone_bricks_fluted_block";
		public static final String MOSSY_STONE_BRICKS_FLUTED_ID = "mossy_stone_bricks_fluted_block";
		public static final String CRACKED_STONE_BRICKS_FLUTED_ID = "cracked_stone_bricks_fluted_block";
		public static final String CHISELED_STONE_BRICKS_FLUTED_ID = "chiseled_stone_bricks_fluted_block";
		public static final String OBSIDIAN_FLUTED_ID = "obsidian_fluted_block";
		public static final String SANDSTONE_FLUTED_ID = "sandstone_fluted_block";
		public static final String SMOOTH_SANDSTONE_FLUTED_ID = "smooth_sandstone_fluted_block";
		public static final String CHISELED_SANDSTONE_FLUTED_ID = "chiseled_sandstone_fluted_block";
		public static final String CUT_SANDSTONE_FLUTED_ID = "cut_sandstone_fluted_block";
		public static final String RED_SANDSTONE_FLUTED_ID = "red_sandstone_fluted_block";
		public static final String SMOOTH_RED_SANDSTONE_FLUTED_ID = "smooth_red_sandstone_fluted_block";
		public static final String CHISELED_RED_SANDSTONE_FLUTED_ID = "chiseled_red_sandstone_fluted_block";
		public static final String CUT_RED_SANDSTONE_FLUTED_ID = "cut_red_sandstone_fluted_block";
		public static final String GRANITE_FLUTED_ID = "granite_fluted_block";
		public static final String POLISHED_GRANITE_FLUTED_ID = "polished_granite_fluted_block";
		public static final String DIORITE_FLUTED_ID = "diorite_fluted_block";
		public static final String POLISHED_DIORITE_FLUTED_ID = "polished_diorite_fluted_block";
		public static final String ANDESITE_FLUTED_ID = "andesite_fluted_block";
		public static final String POLISHED_ANDESITE_FLUTED_ID = "polished_andesite_fluted_block";
		public static final String BLACKSTONE_FLUTED_ID = "blackstone_fluted_block";
		public static final String POLISHED_BLACKSTONE_FLUTED_ID = "polished_blackstone_fluted_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_FLUTED_ID = "chiseled_polished_blackstone_fluted_block";
		public static final String GILDED_BLACKSTONE_FLUTED_ID = "gilded_blackstone_fluted_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_FLUTED_ID = "polished_blackstone_bricks_fluted_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED_ID = "cracked_polished_blackstone_bricks_fluted_block";
		public static final String DEEPSLATE_FLUTED_ID = "deepslate_fluted_block";
		public static final String DEEPSLATE_BRICKS_FLUTED_ID = "deepslate_bricks_fluted_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_FLUTED_ID = "cracked_deepslate_bricks_fluted_block";
		public static final String COBBLED_DEEPSLATE_FLUTED_ID = "cobbled_deepslate_fluted_block";
		public static final String POLISHED_DEEPSLATE_FLUTED_ID = "polished_deepslate_fluted_block";
		public static final String CHISELED_DEEPSLATE_FLUTED_ID = "chiseled_deepslate_fluted_block";
		public static final String DEEPSLATE_TILES_FLUTED_ID = "deepslate_tiles_fluted_block";
		public static final String CRACKED_DEEPSLATE_TILES_FLUTED_ID = "cracked_deepslate_tiles_fluted_block";
		
		// fluted facade
		public static final String STONE_FLUTED_FACADE_ID = "stone_fluted_facade_block";
		public static final String SMOOTH_STONE_FLUTED_FACADE_ID = "smooth_stone_fluted_facade_block";
		public static final String COBBLESTONE_FLUTED_FACADE_ID = "cobblestone_fluted_facade_block";
		public static final String MOSSY_COBBLESTONE_FLUTED_FACADE_ID = "mossy_cobblestone_fluted_facade_block";
		public static final String BRICKS_FLUTED_FACADE_ID = "bricks_fluted_facade_block";
		public static final String STONE_BRICKS_FLUTED_FACADE_ID = "stone_bricks_fluted_facade_block";
		public static final String MOSSY_STONE_BRICKS_FLUTED_FACADE_ID = "mossy_stone_bricks_fluted_facade_block";
		public static final String CRACKED_STONE_BRICKS_FLUTED_FACADE_ID = "cracked_stone_bricks_fluted_facade_block";
		public static final String CHISELED_STONE_BRICKS_FLUTED_FACADE_ID = "chiseled_stone_bricks_fluted_facade_block";
		public static final String OBSIDIAN_FLUTED_FACADE_ID = "obsidian_fluted_facade_block";
		public static final String SANDSTONE_FLUTED_FACADE_ID = "sandstone_fluted_facade_block";
		public static final String SMOOTH_SANDSTONE_FLUTED_FACADE_ID = "smooth_sandstone_fluted_facade_block";
		public static final String CHISELED_SANDSTONE_FLUTED_FACADE_ID = "chiseled_sandstone_fluted_facade_block";
		public static final String CUT_SANDSTONE_FLUTED_FACADE_ID = "cut_sandstone_fluted_facade_block";
		public static final String RED_SANDSTONE_FLUTED_FACADE_ID = "red_sandstone_fluted_facade_block";
		public static final String SMOOTH_RED_SANDSTONE_FLUTED_FACADE_ID = "smooth_red_sandstone_fluted_facade_block";
		public static final String CHISELED_RED_SANDSTONE_FLUTED_FACADE_ID = "chiseled_red_sandstone_fluted_facade_block";
		public static final String CUT_RED_SANDSTONE_FLUTED_FACADE_ID = "cut_red_sandstone_fluted_facade_block";
		public static final String GRANITE_FLUTED_FACADE_ID = "granite_fluted_facade_block";
		public static final String POLISHED_GRANITE_FLUTED_FACADE_ID = "polished_granite_fluted_facade_block";
		public static final String DIORITE_FLUTED_FACADE_ID = "diorite_fluted_facade_block";
		public static final String POLISHED_DIORITE_FLUTED_FACADE_ID = "polished_diorite_fluted_facade_block";
		public static final String ANDESITE_FLUTED_FACADE_ID = "andesite_fluted_facade_block";
		public static final String POLISHED_ANDESITE_FLUTED_FACADE_ID = "polished_andesite_fluted_facade_block";
		public static final String BLACKSTONE_FLUTED_FACADE_ID = "blackstone_fluted_facade_block";
		public static final String POLISHED_BLACKSTONE_FLUTED_FACADE_ID = "polished_blackstone_fluted_facade_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_FLUTED_FACADE_ID = "chiseled_polished_blackstone_fluted_facade_block";
		public static final String GILDED_BLACKSTONE_FLUTED_FACADE_ID = "gilded_blackstone_fluted_facade_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE_ID = "polished_blackstone_bricks_fluted_facade_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE_ID = "cracked_polished_blackstone_bricks_fluted_facade_block";
		public static final String DEEPSLATE_FLUTED_FACADE_ID = "deepslate_fluted_facade_block";
		public static final String DEEPSLATE_BRICKS_FLUTED_FACADE_ID = "deepslate_bricks_fluted_facade_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_FLUTED_FACADE_ID = "cracked_deepslate_bricks_fluted_facade_block";
		public static final String COBBLED_DEEPSLATE_FLUTED_FACADE_ID = "cobbled_deepslate_fluted_facade_block";
		public static final String POLISHED_DEEPSLATE_FLUTED_FACADE_ID = "polished_deepslate_fluted_facade_block";
		public static final String CHISELED_DEEPSLATE_FLUTED_FACADE_ID = "chiseled_deepslate_fluted_facade_block";
		public static final String DEEPSLATE_TILES_FLUTED_FACADE_ID = "deepslate_tiles_fluted_facade_block";
		public static final String CRACKED_DEEPSLATE_TILES_FLUTED_FACADE_ID = "cracked_deepslate_tiles_fluted_facade_block";

		// sill
		public static final String STONE_SILL_ID = "stone_sill_block";
		public static final String SMOOTH_STONE_SILL_ID = "smooth_stone_sill_block";
		public static final String COBBLESTONE_SILL_ID = "cobblestone_sill_block";
		public static final String MOSSY_COBBLESTONE_SILL_ID = "mossy_cobblestone_sill_block";
		public static final String BRICKS_SILL_ID = "bricks_sill_block";
		public static final String STONE_BRICKS_SILL_ID = "stone_bricks_sill_block";
		public static final String MOSSY_STONE_BRICKS_SILL_ID = "mossy_stone_bricks_sill_block";
		public static final String CRACKED_STONE_BRICKS_SILL_ID = "cracked_stone_bricks_sill_block";
		public static final String CHISELED_STONE_BRICKS_SILL_ID = "chiseled_stone_bricks_sill_block";
		public static final String OBSIDIAN_SILL_ID = "obsidian_sill_block";
		public static final String SANDSTONE_SILL_ID = "sandstone_sill_block";
		public static final String SMOOTH_SANDSTONE_SILL_ID = "smooth_sandstone_sill_block";
		public static final String CHISELED_SANDSTONE_SILL_ID = "chiseled_sandstone_sill_block";
		public static final String CUT_SANDSTONE_SILL_ID = "cut_sandstone_sill_block";
		public static final String RED_SANDSTONE_SILL_ID = "red_sandstone_sill_block";
		public static final String SMOOTH_RED_SANDSTONE_SILL_ID = "smooth_red_sandstone_sill_block";
		public static final String CHISELED_RED_SANDSTONE_SILL_ID = "chiseled_red_sandstone_sill_block";
		public static final String CUT_RED_SANDSTONE_SILL_ID = "cut_red_sandstone_sill_block";
		public static final String GRANITE_SILL_ID = "granite_sill_block";
		public static final String POLISHED_GRANITE_SILL_ID = "polished_granite_sill_block";
		public static final String DIORITE_SILL_ID = "diorite_sill_block";
		public static final String POLISHED_DIORITE_SILL_ID = "polished_diorite_sill_block";
		public static final String ANDESITE_SILL_ID = "andesite_sill_block";
		public static final String POLISHED_ANDESITE_SILL_ID = "polished_andesite_sill_block";
		public static final String BLACKSTONE_SILL_ID = "blackstone_sill_block";
		public static final String POLISHED_BLACKSTONE_SILL_ID = "polished_blackstone_sill_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_SILL_ID = "chiseled_polished_blackstone_sill_block";
		public static final String GILDED_BLACKSTONE_SILL_ID = "gilded_blackstone_sill_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_SILL_ID = "polished_blackstone_bricks_sill_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_SILL_ID = "cracked_polished_blackstone_bricks_sill_block";
		public static final String DEEPSLATE_SILL_ID = "deepslate_sill_block";
		public static final String DEEPSLATE_BRICKS_SILL_ID = "deepslate_bricks_sill_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_SILL_ID = "cracked_deepslate_bricks_sill_block";
		public static final String COBBLED_DEEPSLATE_SILL_ID = "cobbled_deepslate_sill_block";
		public static final String POLISHED_DEEPSLATE_SILL_ID = "polished_deepslate_sill_block";
		public static final String CHISELED_DEEPSLATE_SILL_ID = "chiseled_deepslate_sill_block";
		public static final String DEEPSLATE_TILES_SILL_ID = "deepslate_tiles_sill_block";
		public static final String CRACKED_DEEPSLATE_TILES_SILL_ID = "cracked_deepslate_tiles_sill_block";

		// double sill
		public static final String STONE_DOUBLE_SILL_ID = "stone_double_sill_block";
		public static final String SMOOTH_STONE_DOUBLE_SILL_ID = "smooth_stone_double_sill_block";
		public static final String COBBLESTONE_DOUBLE_SILL_ID = "cobblestone_double_sill_block";
		public static final String MOSSY_COBBLESTONE_DOUBLE_SILL_ID = "mossy_cobblestone_double_sill_block";
		public static final String BRICKS_DOUBLE_SILL_ID = "bricks_double_sill_block";
		public static final String STONE_BRICKS_DOUBLE_SILL_ID = "stone_bricks_double_sill_block";
		public static final String MOSSY_STONE_BRICKS_DOUBLE_SILL_ID = "mossy_stone_bricks_double_sill_block";
		public static final String CRACKED_STONE_BRICKS_DOUBLE_SILL_ID = "cracked_stone_bricks_double_sill_block";
		public static final String CHISELED_STONE_BRICKS_DOUBLE_SILL_ID = "chiseled_stone_bricks_double_sill_block";
		public static final String OBSIDIAN_DOUBLE_SILL_ID = "obsidian_double_sill_block";
		public static final String SANDSTONE_DOUBLE_SILL_ID = "sandstone_double_sill_block";
		public static final String SMOOTH_SANDSTONE_DOUBLE_SILL_ID = "smooth_sandstone_double_sill_block";
		public static final String CHISELED_SANDSTONE_DOUBLE_SILL_ID = "chiseled_sandstone_double_sill_block";
		public static final String CUT_SANDSTONE_DOUBLE_SILL_ID = "cut_sandstone_double_sill_block";
		public static final String RED_SANDSTONE_DOUBLE_SILL_ID = "red_sandstone_double_sill_block";
		public static final String SMOOTH_RED_SANDSTONE_DOUBLE_SILL_ID = "smooth_red_sandstone_double_sill_block";
		public static final String CHISELED_RED_SANDSTONE_DOUBLE_SILL_ID = "chiseled_red_sandstone_double_sill_block";
		public static final String CUT_RED_SANDSTONE_DOUBLE_SILL_ID = "cut_red_sandstone_double_sill_block";
		public static final String GRANITE_DOUBLE_SILL_ID = "granite_double_sill_block";
		public static final String POLISHED_GRANITE_DOUBLE_SILL_ID = "polished_granite_double_sill_block";
		public static final String DIORITE_DOUBLE_SILL_ID = "diorite_double_sill_block";
		public static final String POLISHED_DIORITE_DOUBLE_SILL_ID = "polished_diorite_double_sill_block";
		public static final String ANDESITE_DOUBLE_SILL_ID = "andesite_double_sill_block";
		public static final String POLISHED_ANDESITE_DOUBLE_SILL_ID = "polished_andesite_double_sill_block";
		public static final String BLACKSTONE_DOUBLE_SILL_ID = "blackstone_double_sill_block";
		public static final String POLISHED_BLACKSTONE_DOUBLE_SILL_ID = "polished_blackstone_double_sill_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_DOUBLE_SILL_ID = "chiseled_polished_blackstone_double_sill_block";
		public static final String GILDED_BLACKSTONE_DOUBLE_SILL_ID = "gilded_blackstone_double_sill_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL_ID = "polished_blackstone_bricks_double_sill_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL_ID = "cracked_polished_blackstone_bricks_double_sill_block";
		public static final String DEEPSLATE_DOUBLE_SILL_ID = "deepslate_double_sill_block";
		public static final String DEEPSLATE_BRICKS_DOUBLE_SILL_ID = "deepslate_bricks_double_sill_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_DOUBLE_SILL_ID = "cracked_deepslate_bricks_double_sill_block";
		public static final String COBBLED_DEEPSLATE_DOUBLE_SILL_ID = "cobbled_deepslate_double_sill_block";
		public static final String POLISHED_DEEPSLATE_DOUBLE_SILL_ID = "polished_deepslate_double_sill_block";
		public static final String CHISELED_DEEPSLATE_DOUBLE_SILL_ID = "chiseled_deepslate_double_sill_block";
		public static final String DEEPSLATE_TILES_DOUBLE_SILL_ID = "deepslate_tiles_double_sill_block";
		public static final String CRACKED_DEEPSLATE_TILES_DOUBLE_SILL_ID = "cracked_deepslate_tiles_double_sill_block";

		// cornice
		public static final String STONE_CORNICE_ID = "stone_cornice_block";
		public static final String SMOOTH_STONE_CORNICE_ID = "smooth_stone_cornice_block";
		public static final String COBBLESTONE_CORNICE_ID = "cobblestone_cornice_block";
		public static final String MOSSY_COBBLESTONE_CORNICE_ID = "mossy_cobblestone_cornice_block";
		public static final String BRICKS_CORNICE_ID = "bricks_cornice_block";
		public static final String STONE_BRICKS_CORNICE_ID = "stone_bricks_cornice_block";
		public static final String MOSSY_STONE_BRICKS_CORNICE_ID = "mossy_stone_bricks_cornice_block";
		public static final String CRACKED_STONE_BRICKS_CORNICE_ID = "cracked_stone_bricks_cornice_block";
		public static final String CHISELED_STONE_BRICKS_CORNICE_ID = "chiseled_stone_bricks_cornice_block";
		public static final String OBSIDIAN_CORNICE_ID = "obsidian_cornice_block";
		public static final String SANDSTONE_CORNICE_ID = "sandstone_cornice_block";
		public static final String SMOOTH_SANDSTONE_CORNICE_ID = "smooth_sandstone_cornice_block";
		public static final String CHISELED_SANDSTONE_CORNICE_ID = "chiseled_sandstone_cornice_block";
		public static final String CUT_SANDSTONE_CORNICE_ID = "cut_sandstone_cornice_block";
		public static final String RED_SANDSTONE_CORNICE_ID = "red_sandstone_cornice_block";
		public static final String SMOOTH_RED_SANDSTONE_CORNICE_ID = "smooth_red_sandstone_cornice_block";
		public static final String CHISELED_RED_SANDSTONE_CORNICE_ID = "chiseled_red_sandstone_cornice_block";
		public static final String CUT_RED_SANDSTONE_CORNICE_ID = "cut_red_sandstone_cornice_block";
		public static final String GRANITE_CORNICE_ID = "granite_cornice_block";
		public static final String POLISHED_GRANITE_CORNICE_ID = "polished_granite_cornice_block";
		public static final String DIORITE_CORNICE_ID = "diorite_cornice_block";
		public static final String POLISHED_DIORITE_CORNICE_ID = "polished_diorite_cornice_block";
		public static final String ANDESITE_CORNICE_ID = "andesite_cornice_block";
		public static final String POLISHED_ANDESITE_CORNICE_ID = "polished_andesite_cornice_block";
		public static final String BLACKSTONE_CORNICE_ID = "blackstone_cornice_block";
		public static final String POLISHED_BLACKSTONE_CORNICE_ID = "polished_blackstone_cornice_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_CORNICE_ID = "chiseled_polished_blackstone_cornice_block";
		public static final String GILDED_BLACKSTONE_CORNICE_ID = "gilded_blackstone_cornice_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_CORNICE_ID = "polished_blackstone_bricks_cornice_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_CORNICE_ID = "cracked_polished_blackstone_bricks_cornice_block";
		public static final String DEEPSLATE_CORNICE_ID = "deepslate_cornice_block";
		public static final String DEEPSLATE_BRICKS_CORNICE_ID = "deepslate_bricks_cornice_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_CORNICE_ID = "cracked_deepslate_bricks_cornice_block";
		public static final String COBBLED_DEEPSLATE_CORNICE_ID = "cobbled_deepslate_cornice_block";
		public static final String POLISHED_DEEPSLATE_CORNICE_ID = "polished_deepslate_cornice_block";
		public static final String CHISELED_DEEPSLATE_CORNICE_ID = "chiseled_deepslate_cornice_block";
		public static final String DEEPSLATE_TILES_CORNICE_ID = "deepslate_tiles_cornice_block";
		public static final String CRACKED_DEEPSLATE_TILES_CORNICE_ID = "cracked_deepslate_tiles_cornice_block";

		// crown molding
		public static final String STONE_CROWN_MOLDING_ID = "stone_crown_molding_block";
		public static final String SMOOTH_STONE_CROWN_MOLDING_ID = "smooth_stone_crown_molding_block";
		public static final String COBBLESTONE_CROWN_MOLDING_ID = "cobblestone_crown_molding_block";
		public static final String MOSSY_COBBLESTONE_CROWN_MOLDING_ID = "mossy_cobblestone_crown_molding_block";
		public static final String BRICKS_CROWN_MOLDING_ID = "bricks_crown_molding_block";
		public static final String STONE_BRICKS_CROWN_MOLDING_ID = "stone_bricks_crown_molding_block";
		public static final String MOSSY_STONE_BRICKS_CROWN_MOLDING_ID = "mossy_stone_bricks_crown_molding_block";
		public static final String CRACKED_STONE_BRICKS_CROWN_MOLDING_ID = "cracked_stone_bricks_crown_molding_block";
		public static final String CHISELED_STONE_BRICKS_CROWN_MOLDING_ID = "chiseled_stone_bricks_crown_molding_block";
		public static final String OBSIDIAN_CROWN_MOLDING_ID = "obsidian_crown_molding_block";
		public static final String SANDSTONE_CROWN_MOLDING_ID = "sandstone_crown_molding_block";
		public static final String SMOOTH_SANDSTONE_CROWN_MOLDING_ID = "smooth_sandstone_crown_molding_block";
		public static final String CHISELED_SANDSTONE_CROWN_MOLDING_ID = "chiseled_sandstone_crown_molding_block";
		public static final String CUT_SANDSTONE_CROWN_MOLDING_ID = "cut_sandstone_crown_molding_block";
		public static final String RED_SANDSTONE_CROWN_MOLDING_ID = "red_sandstone_crown_molding_block";
		public static final String SMOOTH_RED_SANDSTONE_CROWN_MOLDING_ID = "smooth_red_sandstone_crown_molding_block";
		public static final String CHISELED_RED_SANDSTONE_CROWN_MOLDING_ID = "chiseled_red_sandstone_crown_molding_block";
		public static final String CUT_RED_SANDSTONE_CROWN_MOLDING_ID = "cut_red_sandstone_crown_molding_block";
		public static final String GRANITE_CROWN_MOLDING_ID = "granite_crown_molding_block";
		public static final String POLISHED_GRANITE_CROWN_MOLDING_ID = "polished_granite_crown_molding_block";
		public static final String DIORITE_CROWN_MOLDING_ID = "diorite_crown_molding_block";
		public static final String POLISHED_DIORITE_CROWN_MOLDING_ID = "polished_diorite_crown_molding_block";
		public static final String ANDESITE_CROWN_MOLDING_ID = "andesite_crown_molding_block";
		public static final String POLISHED_ANDESITE_CROWN_MOLDING_ID = "polished_andesite_crown_molding_block";
		public static final String BLACKSTONE_CROWN_MOLDING_ID = "blackstone_crown_molding_block";
		public static final String POLISHED_BLACKSTONE_CROWN_MOLDING_ID = "polished_blackstone_crown_molding_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_CROWN_MOLDING_ID = "chiseled_polished_blackstone_crown_molding_block";
		public static final String GILDED_BLACKSTONE_CROWN_MOLDING_ID = "gilded_blackstone_crown_molding_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING_ID = "polished_blackstone_bricks_crown_molding_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING_ID = "cracked_polished_blackstone_bricks_crown_molding_block";
		public static final String DEEPSLATE_CROWN_MOLDING_ID = "deepslate_crown_molding_block";
		public static final String DEEPSLATE_BRICKS_CROWN_MOLDING_ID = "deepslate_bricks_crown_molding_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_CROWN_MOLDING_ID = "cracked_deepslate_bricks_crown_molding_block";
		public static final String COBBLED_DEEPSLATE_CROWN_MOLDING_ID = "cobbled_deepslate_crown_molding_block";
		public static final String POLISHED_DEEPSLATE_CROWN_MOLDING_ID = "polished_deepslate_crown_molding_block";
		public static final String CHISELED_DEEPSLATE_CROWN_MOLDING_ID = "chiseled_deepslate_crown_molding_block";
		public static final String DEEPSLATE_TILES_CROWN_MOLDING_ID = "deepslate_tiles_crown_molding_block";
		public static final String CRACKED_DEEPSLATE_TILES_CROWN_MOLDING_ID = "cracked_deepslate_tiles_crown_molding_block";

		// pillar base
		public static final String STONE_PILLAR_BASE_ID = "stone_pillar_base_block";
		public static final String SMOOTH_STONE_PILLAR_BASE_ID = "smooth_stone_pillar_base_block";
		public static final String COBBLESTONE_PILLAR_BASE_ID = "cobblestone_pillar_base_block";
		public static final String MOSSY_COBBLESTONE_PILLAR_BASE_ID = "mossy_cobblestone_pillar_base_block";
		public static final String BRICKS_PILLAR_BASE_ID = "bricks_pillar_base_block";
		public static final String STONE_BRICKS_PILLAR_BASE_ID = "stone_bricks_pillar_base_block";
		public static final String MOSSY_STONE_BRICKS_PILLAR_BASE_ID = "mossy_stone_bricks_pillar_base_block";
		public static final String CRACKED_STONE_BRICKS_PILLAR_BASE_ID = "cracked_stone_bricks_pillar_base_block";
		public static final String CHISELED_STONE_BRICKS_PILLAR_BASE_ID = "chiseled_stone_bricks_pillar_base_block";
		public static final String OBSIDIAN_PILLAR_BASE_ID = "obsidian_pillar_base_block";
		public static final String SANDSTONE_PILLAR_BASE_ID = "sandstone_pillar_base_block";
		public static final String SMOOTH_SANDSTONE_PILLAR_BASE_ID = "smooth_sandstone_pillar_base_block";
		public static final String CHISELED_SANDSTONE_PILLAR_BASE_ID = "chiseled_sandstone_pillar_base_block";
		public static final String CUT_SANDSTONE_PILLAR_BASE_ID = "cut_sandstone_pillar_base_block";
		public static final String RED_SANDSTONE_PILLAR_BASE_ID = "red_sandstone_pillar_base_block";
		public static final String SMOOTH_RED_SANDSTONE_PILLAR_BASE_ID = "smooth_red_sandstone_pillar_base_block";
		public static final String CHISELED_RED_SANDSTONE_PILLAR_BASE_ID = "chiseled_red_sandstone_pillar_base_block";
		public static final String CUT_RED_SANDSTONE_PILLAR_BASE_ID = "cut_red_sandstone_pillar_base_block";
		public static final String GRANITE_PILLAR_BASE_ID = "granite_pillar_base_block";
		public static final String POLISHED_GRANITE_PILLAR_BASE_ID = "polished_granite_pillar_base_block";
		public static final String DIORITE_PILLAR_BASE_ID = "diorite_pillar_base_block";
		public static final String POLISHED_DIORITE_PILLAR_BASE_ID = "polished_diorite_pillar_base_block";
		public static final String ANDESITE_PILLAR_BASE_ID = "andesite_pillar_base_block";
		public static final String POLISHED_ANDESITE_PILLAR_BASE_ID = "polished_andesite_pillar_base_block";
		public static final String BLACKSTONE_PILLAR_BASE_ID = "blackstone_pillar_base_block";
		public static final String POLISHED_BLACKSTONE_PILLAR_BASE_ID = "polished_blackstone_pillar_base_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_PILLAR_BASE_ID = "chiseled_polished_blackstone_pillar_base_block";
		public static final String GILDED_BLACKSTONE_PILLAR_BASE_ID = "gilded_blackstone_pillar_base_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE_ID = "polished_blackstone_bricks_pillar_base_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE_ID = "cracked_polished_blackstone_bricks_pillar_base_block";
		public static final String DEEPSLATE_PILLAR_BASE_ID = "deepslate_pillar_base_block";
		public static final String DEEPSLATE_BRICKS_PILLAR_BASE_ID = "deepslate_bricks_pillar_base_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_PILLAR_BASE_ID = "cracked_deepslate_bricks_pillar_base_block";
		public static final String COBBLED_DEEPSLATE_PILLAR_BASE_ID = "cobbled_deepslate_pillar_base_block";
		public static final String POLISHED_DEEPSLATE_PILLAR_BASE_ID = "polished_deepslate_pillar_base_block";
		public static final String CHISELED_DEEPSLATE_PILLAR_BASE_ID = "chiseled_deepslate_pillar_base_block";
		public static final String DEEPSLATE_TILES_PILLAR_BASE_ID = "deepslate_tiles_pillar_base_block";
		public static final String CRACKED_DEEPSLATE_TILES_PILLAR_BASE_ID = "cracked_deepslate_tiles_pillar_base_block";

		// pillar
		public static final String STONE_PILLAR_ID = "stone_pillar_block";
		public static final String SMOOTH_STONE_PILLAR_ID = "smooth_stone_pillar_block";
		public static final String COBBLESTONE_PILLAR_ID = "cobblestone_pillar_block";
		public static final String MOSSY_COBBLESTONE_PILLAR_ID = "mossy_cobblestone_pillar_block";
		public static final String BRICKS_PILLAR_ID = "bricks_pillar_block";
		public static final String STONE_BRICKS_PILLAR_ID = "stone_bricks_pillar_block";
		public static final String MOSSY_STONE_BRICKS_PILLAR_ID = "mossy_stone_bricks_pillar_block";
		public static final String CRACKED_STONE_BRICKS_PILLAR_ID = "cracked_stone_bricks_pillar_block";
		public static final String CHISELED_STONE_BRICKS_PILLAR_ID = "chiseled_stone_bricks_pillar_block";
		public static final String OBSIDIAN_PILLAR_ID = "obsidian_pillar_block";
		public static final String SANDSTONE_PILLAR_ID = "sandstone_pillar_block";
		public static final String SMOOTH_SANDSTONE_PILLAR_ID = "smooth_sandstone_pillar_block";
		public static final String CHISELED_SANDSTONE_PILLAR_ID = "chiseled_sandstone_pillar_block";
		public static final String CUT_SANDSTONE_PILLAR_ID = "cut_sandstone_pillar_block";
		public static final String RED_SANDSTONE_PILLAR_ID = "red_sandstone_pillar_block";
		public static final String SMOOTH_RED_SANDSTONE_PILLAR_ID = "smooth_red_sandstone_pillar_block";
		public static final String CHISELED_RED_SANDSTONE_PILLAR_ID = "chiseled_red_sandstone_pillar_block";
		public static final String CUT_RED_SANDSTONE_PILLAR_ID = "cut_red_sandstone_pillar_block";
		public static final String GRANITE_PILLAR_ID = "granite_pillar_block";
		public static final String POLISHED_GRANITE_PILLAR_ID = "polished_granite_pillar_block";
		public static final String DIORITE_PILLAR_ID = "diorite_pillar_block";
		public static final String POLISHED_DIORITE_PILLAR_ID = "polished_diorite_pillar_block";
		public static final String ANDESITE_PILLAR_ID = "andesite_pillar_block";
		public static final String POLISHED_ANDESITE_PILLAR_ID = "polished_andesite_pillar_block";
		public static final String BLACKSTONE_PILLAR_ID = "blackstone_pillar_block";
		public static final String POLISHED_BLACKSTONE_PILLAR_ID = "polished_blackstone_pillar_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_PILLAR_ID = "chiseled_polished_blackstone_pillar_block";
		public static final String GILDED_BLACKSTONE_PILLAR_ID = "gilded_blackstone_pillar_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_PILLAR_ID = "polished_blackstone_bricks_pillar_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR_ID = "cracked_polished_blackstone_bricks_pillar_block";
		public static final String DEEPSLATE_PILLAR_ID = "deepslate_pillar_block";
		public static final String DEEPSLATE_BRICKS_PILLAR_ID = "deepslate_bricks_pillar_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_PILLAR_ID = "cracked_deepslate_bricks_pillar_block";
		public static final String COBBLED_DEEPSLATE_PILLAR_ID = "cobbled_deepslate_pillar_block";
		public static final String POLISHED_DEEPSLATE_PILLAR_ID = "polished_deepslate_pillar_block";
		public static final String CHISELED_DEEPSLATE_PILLAR_ID = "chiseled_deepslate_pillar_block";
		public static final String DEEPSLATE_TILES_PILLAR_ID = "deepslate_tiles_pillar_block";
		public static final String CRACKED_DEEPSLATE_TILES_PILLAR_ID = "cracked_deepslate_tiles_pillar_block";

		// wall sconce
		public static final String STONE_WALL_SCONCE_ID = "stone_wall_sconce_block";
		public static final String SMOOTH_STONE_WALL_SCONCE_ID = "smooth_stone_wall_sconce_block";
		public static final String COBBLESTONE_WALL_SCONCE_ID = "cobblestone_wall_sconce_block";
		public static final String MOSSY_COBBLESTONE_WALL_SCONCE_ID = "mossy_cobblestone_wall_sconce_block";
		public static final String BRICKS_WALL_SCONCE_ID = "bricks_wall_sconce_block";
		public static final String STONE_BRICKS_WALL_SCONCE_ID = "stone_bricks_wall_sconce_block";
		public static final String MOSSY_STONE_BRICKS_WALL_SCONCE_ID = "mossy_stone_bricks_wall_sconce_block";
		public static final String CRACKED_STONE_BRICKS_WALL_SCONCE_ID = "cracked_stone_bricks_wall_sconce_block";
		public static final String CHISELED_STONE_BRICKS_WALL_SCONCE_ID = "chiseled_stone_bricks_wall_sconce_block";
		public static final String OBSIDIAN_WALL_SCONCE_ID = "obsidian_wall_sconce_block";
		public static final String SANDSTONE_WALL_SCONCE_ID = "sandstone_wall_sconce_block";
		public static final String SMOOTH_SANDSTONE_WALL_SCONCE_ID = "smooth_sandstone_wall_sconce_block";
		public static final String CHISELED_SANDSTONE_WALL_SCONCE_ID = "chiseled_sandstone_wall_sconce_block";
		public static final String CUT_SANDSTONE_WALL_SCONCE_ID = "cut_sandstone_wall_sconce_block";
		public static final String RED_SANDSTONE_WALL_SCONCE_ID = "red_sandstone_wall_sconce_block";
		public static final String SMOOTH_RED_SANDSTONE_WALL_SCONCE_ID = "smooth_red_sandstone_wall_sconce_block";
		public static final String CHISELED_RED_SANDSTONE_WALL_SCONCE_ID = "chiseled_red_sandstone_wall_sconce_block";
		public static final String CUT_RED_SANDSTONE_WALL_SCONCE_ID = "cut_red_sandstone_wall_sconce_block";
		public static final String GRANITE_WALL_SCONCE_ID = "granite_wall_sconce_block";
		public static final String POLISHED_GRANITE_WALL_SCONCE_ID = "polished_granite_wall_sconce_block";
		public static final String DIORITE_WALL_SCONCE_ID = "diorite_wall_sconce_block";
		public static final String POLISHED_DIORITE_WALL_SCONCE_ID = "polished_diorite_wall_sconce_block";
		public static final String ANDESITE_WALL_SCONCE_ID = "andesite_wall_sconce_block";
		public static final String POLISHED_ANDESITE_WALL_SCONCE_ID = "polished_andesite_wall_sconce_block";
		public static final String BLACKSTONE_WALL_SCONCE_ID = "blackstone_wall_sconce_block";
		public static final String POLISHED_BLACKSTONE_WALL_SCONCE_ID = "polished_blackstone_wall_sconce_block";
		public static final String CHISELED_POLISHED_BLACKSTONE_WALL_SCONCE_ID = "chiseled_polished_blackstone_wall_sconce_block";
		public static final String GILDED_BLACKSTONE_WALL_SCONCE_ID = "gilded_blackstone_wall_sconce_block";
		public static final String POLISHED_BLACKSTONE_BRICKS_WALL_SCONCE_ID = "polished_blackstone_bricks_wall_sconce_block";
		public static final String CRACKED_POLISHED_BLACKSTONE_BRICKS_WALL_SCONCE_ID = "cracked_polished_blackstone_bricks_wall_sconce_block";
		public static final String DEEPSLATE_WALL_SCONCE_ID = "deepslate_wall_sconce_block";
		public static final String DEEPSLATE_BRICKS_WALL_SCONCE_ID = "deepslate_bricks_wall_sconce_block";
		public static final String CRACKED_DEEPSLATE_BRICKS_WALL_SCONCE_ID = "cracked_deepslate_bricks_wall_sconce_block";
		public static final String COBBLED_DEEPSLATE_WALL_SCONCE_ID = "cobbled_deepslate_wall_sconce_block";
		public static final String POLISHED_DEEPSLATE_WALL_SCONCE_ID = "polished_deepslate_wall_sconce_block";
		public static final String CHISELED_DEEPSLATE_WALL_SCONCE_ID = "chiseled_deepslate_wall_sconce_block";
		public static final String DEEPSLATE_TILES_WALL_SCONCE_ID = "deepslate_tiles_wall_sconce_block";
		public static final String CRACKED_DEEPSLATE_TILES_WALL_SCONCE_ID = "cracked_deepslate_tiles_wall_sconce_block";

		public static final String GRATE_ID = "grate_block";

	}
}
