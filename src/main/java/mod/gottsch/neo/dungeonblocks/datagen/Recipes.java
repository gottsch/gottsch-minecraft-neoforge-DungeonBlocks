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
import mod.gottsch.neo.dungeonblocks.core.block.BarredWindows;
import mod.gottsch.neo.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Consumer;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class Recipes extends RecipeProvider {
	private static String CRITERIA = "criteria";

		public Recipes(PackOutput output) {
			super(output);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> recipe) {
			Map<Block, RegistryObject<Block>> ingredientMap = new HashMap<>();

			// dungeon lantern
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.DUNGEON_LANTERN.get())
					.requires(Blocks.LANTERN)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LANTERN))
					.save(recipe);

			// torch sconce
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.TORCH_SCONCE.get())
					.requires(Blocks.TORCH)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// candle sconce
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CANDLE_SCONCE.get(), 3)
					.pattern("ccc")
					.pattern("   ")
					.pattern("xxx")
					.define('x', Items.IRON_INGOT)
					.define('c', Items.CANDLE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// plate bracket
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.PLATE_BRACKET_BLOCK.get())
					.requires(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// iron grate
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_IRON_GRATE.get(), 2)
					.pattern("x x")
					.pattern(" x ")
					.pattern("x x")
					.define('x', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// copper grate
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WEATHERED_COPPER_GRATE.get(), 2)
					.pattern("x x")
					.pattern(" x ")
					.pattern("x x")
					.define('x', Items.COPPER_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
					.save(recipe);

			// grate trapdoors
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.DARK_IRON_GRATE_TRAPDOOR.get())
					.requires(Blocks.IRON_TRAPDOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.WEATHERED_COPPER_GRATE_TRAPDOOR.get())
					.requires(Blocks.IRON_TRAPDOOR)
					.requires(Items.COPPER_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// brazier
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BRAZIER.get())
					.requires(Blocks.CAMPFIRE)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// wall ring
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WALL_RING.get())
					.pattern(" x ")
					.pattern("x x")
					.pattern(" x ")
					.define('x', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// dungeon doors
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CRIMSON_DUNGEON_DOOR.get())
					.requires(Blocks.CRIMSON_DOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.DARK_OAK_DUNGEON_DOOR.get())
					.requires(Blocks.DARK_OAK_DOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.MANGROVE_DUNGEON_DOOR.get())
					.requires(Blocks.MANGROVE_DOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.SPRUCE_DUNGEON_DOOR.get())
					.requires(Blocks.SPRUCE_DOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// hay patches
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.HAY_BLOCK)
					.pattern("xxx")
					.pattern("xxx")
					.pattern("xxx")
					.define('x', Ingredient.of(ModBlocks.HAY_PATCH.get(), ModBlocks.DIRTY_HAY_PATCH.get()))
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.HAY_PATCH.get()))
					.save(recipe);

			// barred windows
			ingredientMap.clear();
			ingredientMap.put(Blocks.STONE, BarredWindows.STONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.SMOOTH_STONE, BarredWindows.SMOOTH_STONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.COBBLESTONE, BarredWindows.COBBLESTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.MOSSY_COBBLESTONE, BarredWindows.MOSSY_COBBLESTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.BRICKS, BarredWindows.BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.STONE_BRICKS, BarredWindows.STONE_BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.MOSSY_STONE_BRICKS, BarredWindows.MOSSY_STONE_BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.CRACKED_STONE_BRICKS, BarredWindows.CRACKED_STONE_BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.CHISELED_STONE_BRICKS, BarredWindows.CHISELED_STONE_BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.OBSIDIAN, BarredWindows.OBSIDIAN_BARRED_WINDOW);

			ingredientMap.put(Blocks.SANDSTONE, BarredWindows.SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.SMOOTH_SANDSTONE, BarredWindows.SMOOTH_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.CHISELED_SANDSTONE, BarredWindows.CHISELED_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.CUT_SANDSTONE, BarredWindows.CUT_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.RED_SANDSTONE, BarredWindows.RED_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.SMOOTH_RED_SANDSTONE, BarredWindows.SMOOTH_RED_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.CHISELED_RED_SANDSTONE, BarredWindows.CHISELED_RED_SANDSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.CUT_RED_SANDSTONE, BarredWindows.CUT_RED_SANDSTONE_BARRED_WINDOW);

			ingredientMap.put(Blocks.GRANITE, BarredWindows.GRANITE_BARRED_WINDOW);
			ingredientMap.put(Blocks.ANDESITE, BarredWindows.ANDESITE_BARRED_WINDOW);
			ingredientMap.put(Blocks.DIORITE, BarredWindows.DIORITE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_GRANITE, BarredWindows.POLISHED_GRANITE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_ANDESITE, BarredWindows.POLISHED_ANDESITE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_DIORITE, BarredWindows.POLISHED_DIORITE_BARRED_WINDOW);

			ingredientMap.put(Blocks.BLACKSTONE, BarredWindows.BLACKSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_BLACKSTONE, BarredWindows.POLISHED_BLACKSTONE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_BLACKSTONE_BRICKS, BarredWindows.POLISHED_BLACKSTONE_BRICKS_BARRED_WINDOW);

			ingredientMap.put(Blocks.DEEPSLATE, BarredWindows.DEEPSLATE_BARRED_WINDOW);
			ingredientMap.put(Blocks.DEEPSLATE_BRICKS, BarredWindows.DEEPSLATE_BRICKS_BARRED_WINDOW);
			ingredientMap.put(Blocks.COBBLED_DEEPSLATE, BarredWindows.COBBLED_DEEPSLATE_BARRED_WINDOW);
			ingredientMap.put(Blocks.POLISHED_DEEPSLATE, BarredWindows.POLISHED_DEEPSLATE_BARRED_WINDOW);
			ingredientMap.put(Blocks.DEEPSLATE_TILES, BarredWindows.DEEPSLATE_TILES_BARRED_WINDOW);

			ingredientMap.put(Blocks.TERRACOTTA, BarredWindows.TERRACOTTA_BARRED_WINDOW);

			ingredientMap.forEach((k,v) -> {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, v.get())
						.requires(k)
						.requires(Ingredient.of(ModBlocks.DARK_IRON_GRATE.get(), ModBlocks.DARK_IRON_GRATE_TRAPDOOR.get()))
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.DARK_IRON_GRATE.get(), ModBlocks.DARK_IRON_GRATE_TRAPDOOR.get()))
						.save(recipe);
			});
			ingredientMap.clear();
			ingredientMap.put(BarredWindows.STONE_BARRED_WINDOW.get(), BarredWindows.STONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.SMOOTH_STONE_BARRED_WINDOW.get(), BarredWindows.SMOOTH_STONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.COBBLESTONE_BARRED_WINDOW.get(), BarredWindows.COBBLESTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.MOSSY_COBBLESTONE_BARRED_WINDOW.get(), BarredWindows.MOSSY_COBBLESTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.BRICKS_BARRED_WINDOW.get(), BarredWindows.BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.STONE_BRICKS_BARRED_WINDOW.get(), BarredWindows.STONE_BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.MOSSY_STONE_BRICKS_BARRED_WINDOW.get(), BarredWindows.MOSSY_STONE_BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CRACKED_STONE_BRICKS_BARRED_WINDOW.get(), BarredWindows.CRACKED_STONE_BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CHISELED_STONE_BRICKS_BARRED_WINDOW.get(), BarredWindows.CHISELED_STONE_BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.OBSIDIAN_BARRED_WINDOW.get(), BarredWindows.OBSIDIAN_BARRED_WINDOW_FACADE);

			ingredientMap.put(BarredWindows.SANDSTONE_BARRED_WINDOW.get(), BarredWindows.SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.SMOOTH_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.SMOOTH_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CHISELED_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.CHISELED_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CUT_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.CUT_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.RED_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.RED_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.SMOOTH_RED_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.SMOOTH_RED_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CHISELED_RED_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.CHISELED_RED_SANDSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.CUT_RED_SANDSTONE_BARRED_WINDOW.get(), BarredWindows.CUT_RED_SANDSTONE_BARRED_WINDOW_FACADE);

			ingredientMap.put(BarredWindows.GRANITE_BARRED_WINDOW.get(), BarredWindows.GRANITE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.ANDESITE_BARRED_WINDOW.get(), BarredWindows.ANDESITE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.DIORITE_BARRED_WINDOW.get(), BarredWindows.DIORITE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_GRANITE_BARRED_WINDOW.get(), BarredWindows.POLISHED_GRANITE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_ANDESITE_BARRED_WINDOW.get(), BarredWindows.POLISHED_ANDESITE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_DIORITE_BARRED_WINDOW.get(), BarredWindows.POLISHED_DIORITE_BARRED_WINDOW_FACADE);

			ingredientMap.put(BarredWindows.BLACKSTONE_BARRED_WINDOW.get(), BarredWindows.BLACKSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_BLACKSTONE_BARRED_WINDOW.get(), BarredWindows.POLISHED_BLACKSTONE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_BLACKSTONE_BRICKS_BARRED_WINDOW.get(), BarredWindows.POLISHED_BLACKSTONE_BRICKS_BARRED_WINDOW_FACADE);

			ingredientMap.put(BarredWindows.DEEPSLATE_BARRED_WINDOW.get(), BarredWindows.DEEPSLATE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.DEEPSLATE_BRICKS_BARRED_WINDOW.get(), BarredWindows.DEEPSLATE_BRICKS_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.COBBLED_DEEPSLATE_BARRED_WINDOW.get(), BarredWindows.COBBLED_DEEPSLATE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.POLISHED_DEEPSLATE_BARRED_WINDOW.get(), BarredWindows.POLISHED_DEEPSLATE_BARRED_WINDOW_FACADE);
			ingredientMap.put(BarredWindows.DEEPSLATE_TILES_BARRED_WINDOW.get(), BarredWindows.DEEPSLATE_TILES_BARRED_WINDOW_FACADE);

			ingredientMap.put(BarredWindows.TERRACOTTA_BARRED_WINDOW.get(), BarredWindows.TERRACOTTA_BARRED_WINDOW_FACADE);

			ingredientMap.forEach((k,v) -> {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, v.get(), 2)
						.requires(k)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(v.get()))
						.save(recipe);
			});

			ingredientMap.clear();
			// that's the recipe for a button
//			ingredientMap.put(Blocks.ACACIA_PLANKS, CorbelBlocks.ACACIA_CORBEL);
//			ingredientMap.put(Blocks.BIRCH_PLANKS, CorbelBlocks.BIRCH_CORBEL);
//			ingredientMap.forEach((k,v) -> {
//				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, v.get(), 2)
//						.requires(k)
//						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(v.get()))
//						.save(recipe);
//			});

			// TODO add wood corbels to the stonecutting?
			/*
			 * stone cutting
			 */
//			DataGenMaps.m.forEach((k, v) -> {
//				v.forEach(b -> {
//					SingleItemRecipeBuilder.stonecutting(Ingredient.of(k), RecipeCategory.BUILDING_BLOCKS, b.get())
//							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(k))
//							.save(recipe);
//				});
//			});

			DataGenMaps maps = new DataGenMaps();

			Registration.BLOCKS.getEntries().stream()
					.filter(b -> {
						for(String n : maps.names) {
							if (b.getId().getPath().contains(n)) {
								return true;
							}
						}
						return false;
					})
					.forEach(b -> {
						String name = b.getId().getPath();
						String material = null;
						if (name.contains("corbel")) {
							material = b.getId().getPath().split("_corbel_block")[0];
							DungeonBlocks.LOGGER.info("corbel recipe material ->{} to texture ->{} ", material, maps.m2.get(material));
						} else if (name.contains("ledge")) {
							material = b.getId().getPath().split("_ledge_block")[0];
                        DungeonBlocks.LOGGER.info("ledge recipe material ->{} to texture ->{} ", material, maps.m2.get(material));
						}
//						else if (name.contains("keystone_block")) {
//							material = b.getId().getPath().split("_keystone_block")[0];
//						} else if (name.contains("keystone_slab")) {
//							material = b.getId().getPath().split("_keystone_slab_block")[0];
//						}
						// else do all the other types

						if (material != null) {
							SingleItemRecipeBuilder.stonecutting(Ingredient.of(maps.m2.get(material)), RecipeCategory.BUILDING_BLOCKS, b.get())
									.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(maps.m2.get(material)))
									.save(recipe);
						}

					});

		}
}
