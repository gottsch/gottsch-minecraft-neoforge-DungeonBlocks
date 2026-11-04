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
import mod.gottsch.forge.dungeonblocks.core.block.DecorType;
import mod.gottsch.forge.dungeonblocks.core.block.ModMaterials;
import mod.gottsch.forge.dungeonblocks.core.block.AgedIronFamily;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ShelfBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class Recipes extends RecipeProvider {
	private static String CRITERIA = "criteria";

		public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected void buildRecipes(RecipeOutput recipe) {

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

			// angle cobwebs
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.ANGLE_COBWEB_1.get())
					.requires(Items.STRING, 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.STRING))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.ANGLE_COBWEB_2.get())
					.requires(Items.STRING, 2)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.STRING))
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
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.DARK_IRON_PLATE_BRACKET.get())
					.requires(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// iron grate
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_IRON_GRATES.get(AgedIronFamily.Age.PLAIN).get(), 2)
					.pattern("x x")
					.pattern(" x ")
					.pattern("x x")
					.define('x', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// copper grate
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COPPER_GRATES.get(WeatherState.WEATHERED).get(), 2)
					.pattern("x x")
					.pattern(" x ")
					.pattern("x x")
					.define('x', Items.COPPER_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
					.save(recipe);

			// iron bars door
			ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.IRON_BARS_DOOR.get())
					.requires(Blocks.IRON_DOOR)
					.requires(Blocks.IRON_BARS)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BARS))
					.save(recipe);

			// dark iron bars: eight iron bars around a coal, blackened; the door as the iron one is
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DARK_IRON_BARS.get(AgedIronFamily.Age.PLAIN).get(), 8)
					.pattern("bbb")
					.pattern("bcb")
					.pattern("bbb")
					.define('b', Blocks.IRON_BARS)
					.define('c', Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BARS))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.DARK_IRON_BARS_DOORS.get(AgedIronFamily.Age.PLAIN).get())
					.requires(Blocks.IRON_DOOR)
					.requires(ModBlocks.DARK_IRON_BARS.get(AgedIronFamily.Age.PLAIN).get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.DARK_IRON_BARS.get(AgedIronFamily.Age.PLAIN).get()))
					.save(recipe);

			// dark iron ladder: vanilla's ladder in iron ingots, blackened with a coal
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DARK_IRON_LADDER.get(), 8)
					.pattern("i i")
					.pattern("ici")
					.pattern("i i")
					.define('i', Items.IRON_INGOT)
					.define('c', Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// sharpened logs: a stripped log sharpened with flint
			sharpened(recipe, ModBlocks.SHARPENED_OAK_LOG, Blocks.STRIPPED_OAK_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG);
			sharpened(recipe, ModBlocks.SHARPENED_BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK);
			sharpened(recipe, ModBlocks.SHARPENED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM);
			sharpened(recipe, ModBlocks.SHARPENED_WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);

			// capstones: stonecut from their source, one for one
			ModBlocks.CAPSTONES.forEach((capstone, source) ->
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(source.get()), RecipeCategory.BUILDING_BLOCKS, capstone.get())
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(source.get()))
							.save(recipe));

			// spikes: a row of nuggets for points over a row of ingots for the plate
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.IRON_SPIKES.get(), 2)
					.pattern("nnn")
					.pattern("iii")
					.define('n', Items.IRON_NUGGET)
					.define('i', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);
			// dark iron spikes: iron spikes blackened with coal
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_IRON_SPIKES.get())
					.requires(ModBlocks.IRON_SPIKES.get())
					.requires(Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.IRON_SPIKES.get()))
					.save(recipe);

			// cheval-de-frise: a log with a sharpened log either side of it
			ModBlocks.CHEVALS_DE_FRISE.forEach(cheval -> {
				String log = DataGenMaps.logOf(DataGenMaps.woodOf(cheval.getId().getPath(), "cheval_de_frise"));
				Block sharpened = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "sharpened_" + log));
				ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, cheval.get())
						.pattern("sls")
						.define('s', sharpened)
						.define('l', BuiltInRegistries.BLOCK.get(ResourceLocation.parse(log)))
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(sharpened))
						.save(recipe);
			});

			// walkway bracket: stripped logs laid out as the knee brace itself
			ModBlocks.WALKWAY_BRACKETS.forEach(bracket -> {
				String log = DataGenMaps.logOf(DataGenMaps.woodOf(bracket.getId().getPath(), "walkway_bracket"));
				Block stripped = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("stripped_" + log));
				ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bracket.get(), 4)
						.pattern("ss")
						.pattern("s ")
						.define('s', stripped)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(stripped))
						.save(recipe);
			});

			// portcullis: iron bars braced with ingots; the winch is a drum, a chain and iron
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.PORTCULLIS.get(), 4)
					.pattern("bib")
					.pattern("bib")
					.define('b', Blocks.IRON_BARS)
					.define('i', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BARS))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.PORTCULLIS_WINCH.get())
					.requires(Blocks.STRIPPED_SPRUCE_LOG)
					.requires(Blocks.CHAIN)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.PORTCULLIS.get()))
					.save(recipe);

			// sarcophagi: a lid of slabs over a chest of the carved block
			sarcophagus(recipe, ModBlocks.STONE_SARCOPHAGUS, Blocks.SMOOTH_STONE_SLAB, Blocks.CHISELED_STONE_BRICKS);
			sarcophagus(recipe, ModBlocks.DEEPSLATE_SARCOPHAGUS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.CHISELED_DEEPSLATE);
			// the offering bowl: three copper ingots in a bowl's shape, as vanilla's bowl is planks
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.COPPER_OFFERING_BOWL.get())
					.pattern("c c")
					.pattern(" c ")
					.define('c', Items.COPPER_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
					.save(recipe);
			// the ritual circles: their polished stone chalked with bone meal, one tile each
			for (Map.Entry<DeferredHolder<Block, Block>, Block> circle : Map.of(ModBlocks.DEEPSLATE_RITUAL_CIRCLE, Blocks.POLISHED_DEEPSLATE,
					ModBlocks.BLACKSTONE_RITUAL_CIRCLE, Blocks.POLISHED_BLACKSTONE).entrySet()) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, circle.getKey().get())
						.requires(circle.getValue())
						.requires(Items.BONE_MEAL)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(circle.getValue()))
						.save(recipe);
			}
			// the altar: a slab top on polished deepslate, a carved block in the middle of its front
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DEEPSLATE_ALTAR.get())
					.pattern("sss")
					.pattern("pcp")
					.define('s', Blocks.POLISHED_DEEPSLATE_SLAB)
					.define('p', Blocks.POLISHED_DEEPSLATE)
					.define('c', Blocks.CHISELED_DEEPSLATE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHISELED_DEEPSLATE))
					.save(recipe);

			// iron maiden: an iron case around iron bars
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.IRON_MAIDEN.get())
					.pattern("iii")
					.pattern("ibi")
					.pattern("iii")
					.define('i', Items.IRON_INGOT)
					.define('b', Blocks.IRON_BARS)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// gibbet: a cage of iron bars around a bone block
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GIBBET.get())
					.pattern("bbb")
					.pattern("bxb")
					.pattern("bbb")
					.define('b', Blocks.IRON_BARS)
					.define('x', Blocks.BONE_BLOCK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.IRON_BARS))
					.save(recipe);

			// pillory: a slab board between two log posts; the rack: a plank bed on fence legs with a
			// log roller at each end, strung with string. Each takes a bone block to fill, as the
			// gibbet does.
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.PILLORY.get())
					.pattern("lsl")
					.pattern("l l")
					.pattern("l l")
					.define('l', Blocks.DARK_OAK_LOG)
					.define('s', Blocks.DARK_OAK_SLAB)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DARK_OAK_LOG))
					.save(recipe);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TORTURE_RACK.get())
					.pattern("ltl")
					.pattern("ppp")
					.pattern("f f")
					.define('l', Blocks.DARK_OAK_LOG)
					.define('t', Items.STRING)
					.define('p', Blocks.DARK_OAK_PLANKS)
					.define('f', Blocks.DARK_OAK_FENCE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DARK_OAK_LOG))
					.save(recipe);
			for (List<DeferredHolder<Block, Block>> pair : List.of(List.of(ModBlocks.PILLORY, ModBlocks.OCCUPIED_PILLORY),
					List.of(ModBlocks.TORTURE_RACK, ModBlocks.OCCUPIED_TORTURE_RACK))) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, pair.get(1).get())
						.requires(pair.get(0).get())
						.requires(Blocks.BONE_BLOCK)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(pair.get(0).get()))
						.save(recipe);
			}

			// firewood rack: logs between two hoops of iron bars - any log a campfire would burn
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.FIREWOOD_RACK.get())
					.pattern("b b")
					.pattern("lll")
					.pattern("b b")
					.define('b', Blocks.IRON_BARS)
					.define('l', ItemTags.LOGS_THAT_BURN)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.IRON_BARS))
					.save(recipe);

			// weapon rack: a rail of iron bars on two iron legs
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WEAPON_RACK.get())
					.pattern("bbb")
					.pattern("i i")
					.define('b', Blocks.IRON_BARS)
					.define('i', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// coffins: a lid of the wood's slabs over its planks, with a bone laid in
			coffin(recipe, ModBlocks.SPRUCE_COFFIN, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
			coffin(recipe, ModBlocks.DARK_OAK_COFFIN, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
			coffin(recipe, ModBlocks.CRIMSON_COFFIN, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
			coffin(recipe, ModBlocks.MANGROVE_COFFIN, Blocks.MANGROVE_SLAB, Blocks.MANGROVE_PLANKS);

			// catacomb niches: the stone with a bone in it
			ModBlocks.CATACOMB_NICHES.forEach((niche, source) ->
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, niche.get())
							.requires(source.get())
							.requires(Items.BONE)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(source.get()))
							.save(recipe));

			// skull pike: a bone block on two sticks
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SKULL_PIKE.get())
					.pattern("b")
					.pattern("s")
					.pattern("s")
					.define('b', Blocks.BONE_BLOCK)
					.define('s', Items.STICK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BONE_BLOCK))
					.save(recipe);
			// a zombie's head: rotten flesh on the pike; Steve's: leather for his skin, and red dye
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ZOMBIE_HEAD_PIKE.get())
					.pattern("f")
					.pattern("s")
					.pattern("s")
					.define('f', Items.ROTTEN_FLESH)
					.define('s', Items.STICK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.ROTTEN_FLESH))
					.save(recipe);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLOODY_STEVE_HEAD_PIKE.get())
					.pattern("lr")
					.pattern("s ")
					.pattern("s ")
					.define('l', Items.LEATHER)
					.define('r', Items.RED_DYE)
					.define('s', Items.STICK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER))
					.save(recipe);

			// bone pile: four bones
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BONE_PILE.get())
					.requires(Items.BONE, 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.BONE))
					.save(recipe);
			// skull pile: a bone block for the skulls, as on the skull pike, and bones
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.SKULL_PILE.get())
					.requires(Blocks.BONE_BLOCK)
					.requires(Items.BONE, 3)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BONE_BLOCK))
					.save(recipe);

			// chandelier: a row of candles on a row of iron, hung from a chain
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHANDELIER.get())
					.pattern(" c ")
					.pattern("kkk")
					.pattern("iii")
					.define('c', Blocks.CHAIN)
					.define('k', ItemTags.CANDLES)
					.define('i', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHAIN))
					.save(recipe);

			// bone chandelier: the chandelier's chain and candles, on bone blocks for its skulls
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BONE_CHANDELIER.get())
					.pattern(" c ")
					.pattern("kkk")
					.pattern("bbb")
					.define('c', Blocks.CHAIN)
					.define('k', ItemTags.CANDLES)
					.define('b', Blocks.BONE_BLOCK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BONE_BLOCK))
					.save(recipe);
			// medium bone chandelier: the bone chandelier grown with more bone blocks and candles
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.MEDIUM_BONE_CHANDELIER.get())
					.pattern("kkk")
					.pattern("bcb")
					.pattern("bbb")
					.define('c', ModBlocks.BONE_CHANDELIER.get())
					.define('k', ItemTags.CANDLES)
					.define('b', Blocks.BONE_BLOCK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BONE_CHANDELIER.get()))
					.save(recipe);

			// soul vent: a dark iron grate over a soul campfire
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.SOUL_VENT.get())
					.requires(ModBlocks.DARK_IRON_GRATES.get(AgedIronFamily.Age.PLAIN).get())
					.requires(Items.SOUL_CAMPFIRE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.SOUL_CAMPFIRE))
					.save(recipe);

			// giant chain: an iron block blackened with coal for the anchor, and four chains for the
			// chain - one makes a chain of any length
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CHAIN_ANCHOR.get())
					.requires(Blocks.IRON_BLOCK)
					.requires(Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.CHAIN))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModItems.GIANT_CHAIN.get())
					.requires(Items.CHAIN, 4)
					.requires(Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.CHAIN))
					.save(recipe);
			// medium chain: two chains, blackened the same way; it hangs on the same anchors
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModItems.MEDIUM_CHAIN.get())
					.requires(Items.CHAIN, 2)
					.requires(Items.COAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.CHAIN))
					.save(recipe);

			// stained glass windows: red and blue panes leaded round an iron nugget, one window a pane;
			// a stone through one breaks it
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STAINED_GLASS_WINDOW.get(), 8)
					.pattern("rbr")
					.pattern("bnb")
					.pattern("rbr")
					.define('r', Blocks.RED_STAINED_GLASS_PANE)
					.define('b', Blocks.BLUE_STAINED_GLASS_PANE)
					.define('n', Items.IRON_NUGGET)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.RED_STAINED_GLASS_PANE))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BROKEN_STAINED_GLASS_WINDOW.get())
					.requires(ModBlocks.STAINED_GLASS_WINDOW.get())
					.requires(Items.FLINT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.STAINED_GLASS_WINDOW.get()))
					.save(recipe);

			// skull candles: a bone block for the skull, as on the skull pike, and a candle
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.SKULL_CANDLE.get())
					.requires(Blocks.BONE_BLOCK)
					.requires(Blocks.CANDLE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BONE_BLOCK))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BLACK_SKULL_CANDLE.get())
					.requires(Blocks.BONE_BLOCK)
					.requires(Blocks.BLACK_CANDLE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BONE_BLOCK))
					.save(recipe);

			// wall shelves: a block of the stone cuts into two; a mossy one is the plain shelf with a
			// vine or a moss block, as mossy flagstones are
			ModBlocks.SHELVES.forEach(shelf -> {
				Block source = shelf.source().get();
				if (!(source instanceof ShelfBlock)) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(source), RecipeCategory.DECORATIONS, shelf.block().get(), 2)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(source))
							.save(recipe);
					return;
				}
				for (Block moss : List.of(Blocks.VINE, Blocks.MOSS_BLOCK)) {
					ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, shelf.block().get())
							.requires(source)
							.requires(moss)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(moss))
							.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, shelf.block().getId().getPath() + "_from_"
									+ BuiltInRegistries.BLOCK.getKey(moss).getPath()));
				}
			});

			// square and large bricks are cut from the stone's own bricks, as the ossuary ones are
			cutBricks(recipe, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Map.of(
					ModBlocks.SQUARE_STONE_BRICK, ModBlocks.MOSSY_SQUARE_STONE_BRICK,
					ModBlocks.LEFT_LARGE_STONE_BRICK, ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK,
					ModBlocks.RIGHT_LARGE_STONE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK));
			// the mod has no mossy mud bricks to cut from, so these only take a vine or a moss block
			cutBricks(recipe, Blocks.MUD_BRICKS, null, Map.of(
					ModBlocks.SQUARE_MUD_BRICK, ModBlocks.MOSSY_SQUARE_MUD_BRICK,
					ModBlocks.LEFT_LARGE_MUD_BRICK, ModBlocks.MOSSY_LEFT_LARGE_MUD_BRICK,
					ModBlocks.RIGHT_LARGE_MUD_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_MUD_BRICK));
			cutBricks(recipe, Blocks.DEEPSLATE_BRICKS, ModBlocks.MOSSY_DEEPSLATE_BRICKS.get(), Map.of(
					ModBlocks.SQUARE_DEEPSLATE_BRICK, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK,
					ModBlocks.LEFT_LARGE_DEEPSLATE_BRICK, ModBlocks.MOSSY_LEFT_LARGE_DEEPSLATE_BRICK,
					ModBlocks.RIGHT_LARGE_DEEPSLATE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_DEEPSLATE_BRICK));
			cutBricks(recipe, Blocks.BRICKS, ModBlocks.MOSSY_BRICKS.get(), Map.of(
					ModBlocks.LARGE_BRICKS, ModBlocks.MOSSY_LARGE_BRICKS,
					ModBlocks.SQUARE_BRICK, ModBlocks.MOSSY_SQUARE_BRICK,
					ModBlocks.LEFT_LARGE_BRICK, ModBlocks.MOSSY_LEFT_LARGE_BRICK,
					ModBlocks.RIGHT_LARGE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_BRICK));
			// their pieces are cut from the brick, in as many as the block's volume makes
			cutPieces(recipe, ModBlocks.SQUARE_STONE_BRICK, ModBlocks.SQUARE_STONE_BRICK_STAIRS, ModBlocks.SQUARE_STONE_BRICK_SLAB,
					ModBlocks.SQUARE_STONE_BRICK_FACADE_BLOCK, ModBlocks.SQUARE_STONE_BRICK_QUARTER_FACADE_BLOCK);
			cutPieces(recipe, ModBlocks.MOSSY_SQUARE_STONE_BRICK, ModBlocks.MOSSY_SQUARE_STONE_BRICK_STAIRS, ModBlocks.MOSSY_SQUARE_STONE_BRICK_SLAB,
					null, null);
			cutPieces(recipe, ModBlocks.SQUARE_MUD_BRICK, ModBlocks.SQUARE_MUD_BRICK_STAIRS, null,
					ModBlocks.SQUARE_MUD_BRICK_FACADE_BLOCK, ModBlocks.SQUARE_MUD_BRICK_QUARTER_FACADE_BLOCK);
			cutPieces(recipe, ModBlocks.SQUARE_DEEPSLATE_BRICK, ModBlocks.SQUARE_DEEPSLATE_BRICK_STAIRS, ModBlocks.SQUARE_DEEPSLATE_BRICK_SLAB,
					ModBlocks.SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK, ModBlocks.SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK);
			cutPieces(recipe, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_STAIRS, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_SLAB,
					ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK);
			cutPieces(recipe, ModBlocks.LARGE_BRICKS, ModBlocks.LARGE_BRICK_STAIRS, null, null, null);
			cutPieces(recipe, ModBlocks.MOSSY_LARGE_BRICKS, ModBlocks.MOSSY_LARGE_BRICK_STAIRS, null, null, null);

			// catacombs. Ossuary bricks are stone bricks bleached with bone meal, and their large and
			// square bricks are cut from them
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.OSSUARY_BRICKS.get())
					.requires(Blocks.STONE_BRICKS)
					.requires(Items.BONE_MEAL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.BONE_MEAL))
					.save(recipe);
			// weathered as vanilla's stone bricks are: mossy with a vine or a moss block, cracked in a furnace
			mossy(recipe, ModBlocks.MOSSY_OSSUARY_BRICKS, ModBlocks.OSSUARY_BRICKS.get());
			mossy(recipe, ModBlocks.MOSSY_HEWN_TUFA, ModBlocks.HEWN_TUFA.get());
			mossy(recipe, ModBlocks.MOSSY_HERRINGBONE_BRICKS, ModBlocks.HERRINGBONE_BRICKS.get());
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.OSSUARY_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS,
							ModBlocks.CRACKED_OSSUARY_BRICKS.get(), 0.1F, 200)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.OSSUARY_BRICKS.get()))
					.save(recipe);
			for (DeferredHolder<Block, Block> brick : List.of(ModBlocks.SQUARE_OSSUARY_BRICK, ModBlocks.LEFT_LARGE_OSSUARY_BRICK, ModBlocks.RIGHT_LARGE_OSSUARY_BRICK)) {
				SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.OSSUARY_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, brick.get())
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.OSSUARY_BRICKS.get()))
						.save(recipe);
			}
			cutPiece(recipe, ModBlocks.OSSUARY_BRICKS, ModBlocks.OSSUARY_BRICK_STAIRS, 1);
			cutPiece(recipe, ModBlocks.MOSSY_OSSUARY_BRICKS, ModBlocks.MOSSY_OSSUARY_BRICK_STAIRS, 1);
			cutPiece(recipe, ModBlocks.HEWN_TUFA, ModBlocks.HEWN_TUFA_STAIRS, 1);
			slabTable(recipe, ModBlocks.OSSUARY_BRICKS_SLAB_TABLE, ModBlocks.OSSUARY_BRICKS.get(), ModBlocks.OSSUARY_BRICKS.get());
			// hewn tufa is cut from tuff, and the grave slabs from that
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.TUFF), RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEWN_TUFA.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.TUFF))
					.save(recipe);
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModBlocks.HEWN_TUFA.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.GRAVE_SLAB_PAVEMENT.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.HEWN_TUFA.get()))
					.save(recipe);
			// herringbone bricks are relaid bricks
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), RecipeCategory.BUILDING_BLOCKS, ModBlocks.HERRINGBONE_BRICKS.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BRICKS))
					.save(recipe);
			// bone walls are hewn tufa packed with a bone block for the skulls, as on the skull
			// pike, or with bones; knucklebone paving is cobblestone set with a bone
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PACKED_SKULLS.get())
					.requires(ModBlocks.HEWN_TUFA.get())
					.requires(Blocks.BONE_BLOCK)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.HEWN_TUFA.get()))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.JUMBLED_FEMURS.get())
					.requires(ModBlocks.HEWN_TUFA.get())
					.requires(Items.BONE, 3)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.HEWN_TUFA.get()))
					.save(recipe);
			// the crumbling bone wall: femurs and gravel, as the crumbling floors are their stone and gravel
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRUMBLING_BONE_WALL.get())
					.requires(ModBlocks.JUMBLED_FEMURS.get())
					.requires(Blocks.GRAVEL)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.JUMBLED_FEMURS.get()))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.KNUCKLEBONE_PAVING.get())
					.requires(Blocks.COBBLESTONE)
					.requires(Items.BONE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.BONE))
					.save(recipe);

			// large candles, of vanilla's plain candles (their wax): two make a Large Candle, three of
			// those a cluster, and two stacked a Huge Candle
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.LARGE_CANDLE.get())
					.requires(Items.CANDLE, 2)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.CANDLE))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CANDLE_CLUSTER.get())
					.requires(ModBlocks.LARGE_CANDLE.get(), 3)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.LARGE_CANDLE.get()))
					.save(recipe);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.HUGE_CANDLE.get())
					.pattern("c")
					.pattern("c")
					.define('c', ModBlocks.LARGE_CANDLE.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.LARGE_CANDLE.get()))
					.save(recipe);

			// flagstones: stone bricks, cut to slabs of floor
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLAGSTONES.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE_BRICKS))
					.save(recipe);
			// their weathered twins as vanilla makes its stone bricks': cracked in a furnace,
			// mossy with a vine or a moss block
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.FLAGSTONES.get()), RecipeCategory.BUILDING_BLOCKS,
							ModBlocks.CRACKED_FLAGSTONES.get(), 0.1F, 200)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.FLAGSTONES.get()))
					.save(recipe);
			for (Block moss : List.of(Blocks.VINE, Blocks.MOSS_BLOCK)) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_FLAGSTONES.get())
						.requires(ModBlocks.FLAGSTONES.get())
						.requires(moss)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(moss))
						.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "mossy_flagstones_from_"
								+ BuiltInRegistries.BLOCK.getKey(moss).getPath()));
			}
			// fan cobbles: cobblestone, cut to small setts
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), RecipeCategory.BUILDING_BLOCKS, ModBlocks.FAN_COBBLES.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
					.save(recipe);
			// mossy: as vanilla makes mossy cobblestone, with a vine or a moss block; or cut from
			// mossy cobblestone
			for (Block moss : List.of(Blocks.VINE, Blocks.MOSS_BLOCK)) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_FAN_COBBLES.get())
						.requires(ModBlocks.FAN_COBBLES.get())
						.requires(moss)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(moss))
						.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "mossy_fan_cobbles_from_"
								+ BuiltInRegistries.BLOCK.getKey(moss).getPath()));
			}
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_FAN_COBBLES.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.MOSSY_COBBLESTONE))
					.save(recipe);
			// sandstone ashlar: sandstone, squared into blocks
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SANDSTONE_ASHLAR.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.SANDSTONE))
					.save(recipe);
			// drifted: the ashlar and sand, as mossy blocks take a vine
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIFTED_SANDSTONE_ASHLAR.get())
					.requires(ModBlocks.SANDSTONE_ASHLAR.get())
					.requires(Blocks.SAND)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.SANDSTONE_ASHLAR.get()))
					.save(recipe);

			// gargoyles: the perched one and the bust are carved in a stonecutter; the statue stands
			// on a smooth stone plinth, a block of chiseled stone bricks for its body
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, ModBlocks.PERCHED_GARGOYLE.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE))
					.save(recipe);
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, ModBlocks.GARGOYLE_BUST.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE))
					.save(recipe);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GARGOYLE_STATUE.get())
					.pattern("c")
					.pattern("c")
					.pattern("s")
					.define('c', Blocks.CHISELED_STONE_BRICKS)
					.define('s', Blocks.SMOOTH_STONE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHISELED_STONE_BRICKS))
					.save(recipe);
			// the deepslate ones the same way: cut from cobbled deepslate, as vanilla's deepslate
			// pieces are; the statue of chiseled deepslate on a polished deepslate plinth
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLED_DEEPSLATE), RecipeCategory.DECORATIONS, ModBlocks.PERCHED_DEEPSLATE_GARGOYLE.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLED_DEEPSLATE))
					.save(recipe);
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLED_DEEPSLATE), RecipeCategory.DECORATIONS, ModBlocks.DEEPSLATE_GARGOYLE_BUST.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLED_DEEPSLATE))
					.save(recipe);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DEEPSLATE_GARGOYLE_STATUE.get())
					.pattern("c")
					.pattern("c")
					.pattern("s")
					.define('c', Blocks.CHISELED_DEEPSLATE)
					.define('s', Blocks.POLISHED_DEEPSLATE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHISELED_DEEPSLATE))
					.save(recipe);

			// the secret passage: a torch sconce with a lever in it; a door of the wall's own stone,
			// its redstone set in; a pedestal of polished andesite
			ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.LEVER_SCONCE.get())
					.requires(ModBlocks.TORCH_SCONCE.get())
					.requires(Blocks.LEVER)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.TORCH_SCONCE.get()))
					.save(recipe);
			ModBlocks.HIDDEN_DOORS.forEach((door, source) ->
					ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, door.get())
							.pattern("ss")
							.pattern("sr")
							.pattern("ss")
							.define('s', source.get())
							.define('r', Items.REDSTONE)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(source.get()))
							.save(recipe));
			for (Map.Entry<DeferredHolder<Block, Block>, Block> pedestal : Map.of(ModBlocks.PEDESTAL, Blocks.POLISHED_ANDESITE,
					ModBlocks.BLACKSTONE_PEDESTAL, Blocks.POLISHED_BLACKSTONE).entrySet()) {
				ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pedestal.getKey().get())
						.pattern("sss")
						.pattern(" s ")
						.pattern("sss")
						.define('s', pedestal.getValue())
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(pedestal.getValue()))
						.save(recipe);
			}

			// tapestries: wool woven on a rod of sticks, a dye for the scene; a pristine one aged with
			// coarse dirt
			tapestry(recipe, ModBlocks.DRAGON_TAPESTRY, ModBlocks.WORN_DRAGON_TAPESTRY, Items.RED_DYE);
			tapestry(recipe, ModBlocks.HUNT_TAPESTRY, ModBlocks.WORN_HUNT_TAPESTRY, Items.GREEN_DYE);
			tapestry(recipe, ModBlocks.NECROMANCER_TAPESTRY, ModBlocks.WORN_NECROMANCER_TAPESTRY, Items.PURPLE_DYE);
			tapestry(recipe, ModBlocks.SUMMONING_TAPESTRY, ModBlocks.WORN_SUMMONING_TAPESTRY, Items.BLACK_DYE);
			// crumbling floors: the stone laid over gravel
			ModBlocks.CRUMBLING_FLOORS.forEach((floor, source) ->
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, floor.get())
							.requires(source.get())
							.requires(Blocks.GRAVEL)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(source.get()))
							.save(recipe));
			// the rubble scatter is a Rubble block broken up
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.RUBBLE_SCATTER.get(), 4)
					.requires(ModBlocks.RUBBLE.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.RUBBLE.get()))
					.save(recipe);

			// chain fixtures. Manacles: two iron cuffs on a chain
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.MANACLES.get())
					.pattern(" c ")
					.pattern("i i")
					.define('c', Blocks.CHAIN)
					.define('i', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHAIN))
					.save(recipe);
			// meat hook: an ingot drawn out, and bent
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.MEAT_HOOK.get())
					.requires(Items.IRON_INGOT)
					.requires(Items.IRON_NUGGET, 2)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);
			// censer: an iron bowl of coal on a chain
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CENSER.get())
					.pattern(" c ")
					.pattern("iki")
					.pattern(" i ")
					.define('c', Blocks.CHAIN)
					.define('i', Items.IRON_INGOT)
					.define('k', ItemTags.COALS)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CHAIN))
					.save(recipe);

			// fire basket: a brazier hung on chains
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.FIRE_BASKET.get())
					.pattern("c c")
					.pattern(" c ")
					.pattern(" b ")
					.define('c', Blocks.CHAIN)
					.define('b', ModBlocks.BRAZIER.get())
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BRAZIER.get()))
					.save(recipe);

			// bubbling cauldron: a cauldron, water, and nether wart for the brew; it starts green
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BUBBLING_CAULDRON.get())
					.requires(Blocks.CAULDRON)
					.requires(Items.WATER_BUCKET)
					.requires(Items.NETHER_WART)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CAULDRON))
					.save(recipe);

			// grate trapdoors
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.get(AgedIronFamily.Age.PLAIN).get())
					.requires(Blocks.IRON_TRAPDOOR)
					.requires(Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.COPPER_HEAVY_TRAPDOORS.get(WeatherState.WEATHERED).get())
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

			// slab tables — a trestle: three slabs of top laid across two uprights
			slabTable(recipe, ModBlocks.STONE_SLAB_TABLE, Blocks.STONE_SLAB, Blocks.STONE);
			slabTable(recipe, ModBlocks.STONE_BRICKS_SLAB_TABLE, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS);
			slabTable(recipe, ModBlocks.MOSSY_STONE_BRICKS_SLAB_TABLE, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICKS);
			slabTable(recipe, ModBlocks.SMOOTH_STONE_SLAB_TABLE, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
			slabTable(recipe, ModBlocks.SMOOTH_SANDSTONE_SLAB_TABLE, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE);

			// wall ring
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WALL_RING.get())
					.pattern(" x ")
					.pattern("x x")
					.pattern(" x ")
					.define('x', Items.IRON_INGOT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// dungeon doors: a vanilla door reinforced with iron
			ModBlocks.DUNGEON_DOORS.stream().filter(door -> door.height() == 2).forEach(door ->
					ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, door.block().get())
							.requires(door.vanillaDoor())
							.requires(Items.IRON_INGOT)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
							.save(recipe));

			// hay patches
			ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.HAY_BLOCK)
					.pattern("xxx")
					.pattern("xxx")
					.pattern("xxx")
					.define('x', Ingredient.of(ModBlocks.HAY_PATCH.get(), ModBlocks.DIRTY_HAY_PATCH.get()))
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.HAY_PATCH.get()))
					// its own id: saved under the result's default id it was minecraft:hay_block,
					// which REPLACED vanilla's nine-wheat hay bale recipe
					.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "hay_block_from_hay_patches"));

			// barred windows: the material and a dark iron grate (or heavy trapdoor) make a window,
			// and a window makes two window facades
			Block grate = ModBlocks.DARK_IRON_GRATES.get(AgedIronFamily.Age.PLAIN).get();
			Block heavyTrapdoor = ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.get(AgedIronFamily.Age.PLAIN).get();
			for (String material : ModMaterials.BARRED_WINDOWS) {
				Block window = ModBlocks.decor(material, DecorType.BARRED_WINDOW).get();
				Block facade = ModBlocks.decor(material, DecorType.BARRED_WINDOW_FACADE).get();
				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, window)
						.requires(ModMaterials.get(material).ingredient().get())
						.requires(Ingredient.of(grate, heavyTrapdoor))
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(grate, heavyTrapdoor))
						.save(recipe);
				ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, facade, 2)
						.requires(window)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(facade))
						.save(recipe);
			}

			// corbels and ledges are stonecut from their material
			ModBlocks.DECOR.stream()
					.filter(d -> d.type() == DecorType.CORBEL || d.type() == DecorType.LEDGE)
					.forEach(d -> {
						Block ingredient = d.material().ingredient().get();
						SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, d.block().get())
								.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
								.save(recipe);
					});

			// the stone types are too, in as many as the piece's volume makes
			ModBlocks.DECOR.stream()
					.filter(d -> DecorType.STONE_TYPES.contains(d.type()))
					.forEach(d -> {
						Block ingredient = d.material().ingredient().get();
						SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, d.block().get(), stonecutCount(d.type()))
								.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
								.save(recipe);
					});

			// the mod's mossy stones those are cut from: the plain stone with a vine or a moss block
			mossy(recipe, ModBlocks.MOSSY_BRICKS, Blocks.BRICKS);
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BRICKS), RecipeCategory.BUILDING_BLOCKS,
							ModBlocks.CRACKED_BRICKS.get(), 0.1F, 200)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.BRICKS))
					.save(recipe);
			mossy(recipe, ModBlocks.MOSSY_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICKS);
			mossy(recipe, ModBlocks.MOSSY_DEEPSLATE_TILES, Blocks.DEEPSLATE_TILES);
			mossy(recipe, ModBlocks.MOSSY_COBBLED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
			mossy(recipe, ModBlocks.MOSSY_TUFF, Blocks.TUFF);
		}

		private static int stonecutCount(DecorType type) {
			return switch (type) {
				case QUARTER_FACADE -> 4;
				case FACADE, FLUTED_FACADE, CORNICE, CROWN_MOLDING -> 2;
				default -> 1;
			};
		}

		private static void mossy(RecipeOutput recipe, DeferredHolder<Block, Block> mossy, Block plain) {
			for (Block moss : List.of(Blocks.VINE, Blocks.MOSS_BLOCK)) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossy.get())
						.requires(plain)
						.requires(moss)
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(moss))
						.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, mossy.getId().getPath() + "_from_"
								+ BuiltInRegistries.BLOCK.getKey(moss).getPath()));
			}
		}

		/**
		 * Each brick shape is cut from the base bricks. Its mossy twin is that shape with a vine or a
		 * moss block, or cut from the mossy base where the stone has one.
		 */
		private static void cutBricks(RecipeOutput recipe, Block base, Block mossyBase,
									  Map<DeferredHolder<Block, Block>, DeferredHolder<Block, Block>> shapes) {
			shapes.forEach((plain, mossy) -> {
				SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, plain.get())
						.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(base))
						.save(recipe);
				for (Block moss : List.of(Blocks.VINE, Blocks.MOSS_BLOCK)) {
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossy.get())
							.requires(plain.get())
							.requires(moss)
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(moss))
							.save(recipe, ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, mossy.getId().getPath() + "_from_"
									+ BuiltInRegistries.BLOCK.getKey(moss).getPath()));
				}
				if (mossyBase != null) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(mossyBase), RecipeCategory.BUILDING_BLOCKS, mossy.get())
							.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(mossyBase))
							.save(recipe);
				}
			});
		}

		/** One stair, two slabs, two facades or four quarter facades to a brick. A family lacking a piece passes null. */
		private static void cutPieces(RecipeOutput recipe, DeferredHolder<Block, Block> brick, DeferredHolder<Block, ? extends Block> stairs,
									  DeferredHolder<Block, ? extends Block> slab, DeferredHolder<Block, Block> facade, DeferredHolder<Block, Block> quarterFacade) {
			cutPiece(recipe, brick, stairs, 1);
			cutPiece(recipe, brick, slab, 2);
			cutPiece(recipe, brick, facade, 2);
			cutPiece(recipe, brick, quarterFacade, 4);
		}

		private static void cutPiece(RecipeOutput recipe, DeferredHolder<Block, Block> brick, DeferredHolder<Block, ? extends Block> piece, int count) {
			if (piece == null) {
				return;
			}
			SingleItemRecipeBuilder.stonecutting(Ingredient.of(brick.get()), RecipeCategory.BUILDING_BLOCKS, piece.get(), count)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(brick.get()))
					.save(recipe);
		}

		/**
		 * One item places the whole two-block table, so the recipe yields a single output for what
		 * reads as a trestle: a run of slabs over two uprights.
		 */
		private static void slabTable(RecipeOutput recipe, DeferredHolder<Block, Block> table, Block slab, Block base) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, table.get())
					.pattern("xxx")
					.pattern("y y")
					.define('x', slab)
					.define('y', base)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(base))
					.save(recipe);
		}

		/** A lid of slabs over a chest of the carved block. */
		private static void sarcophagus(RecipeOutput recipe, DeferredHolder<Block, Block> sarcophagus, Block slab, Block carved) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sarcophagus.get())
					.pattern("sss")
					.pattern("ccc")
					.define('s', slab)
					.define('c', carved)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(carved))
					.save(recipe);
		}

		/** Wool on a rod of sticks with the scene's dye; the worn one is the pristine one and coarse dirt. */
		private static void tapestry(RecipeOutput recipe, DeferredHolder<Block, Block> pristine,
				DeferredHolder<Block, Block> worn, Item dye) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pristine.get())
					.pattern("sss")
					.pattern("wdw")
					.pattern("www")
					.define('s', Items.STICK)
					.define('w', ItemTags.WOOL)
					.define('d', dye)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(dye))
					.save(recipe);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, worn.get())
					.requires(pristine.get())
					.requires(Blocks.COARSE_DIRT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(pristine.get()))
					.save(recipe);
		}

		/** A lid of the wood's slabs over its planks, with a bone laid in. */
		private static void coffin(RecipeOutput recipe, DeferredHolder<Block, Block> coffin, Block slab, Block planks) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, coffin.get())
					.pattern("sss")
					.pattern("pbp")
					.define('s', slab)
					.define('p', planks)
					.define('b', Items.BONE)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(planks))
					.save(recipe);
		}

		/**
		 * Shapeless: the stripped block plus flint to sharpen it. The flint is what keeps this clear
		 * of vanilla's one-log-to-planks recipe, which a lone stripped log would collide with.
		 */
		private static void sharpened(RecipeOutput recipe, DeferredHolder<Block, Block> sharpened, Block stripped) {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, sharpened.get())
					.requires(stripped)
					.requires(Items.FLINT)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(stripped))
					.save(recipe);
		}
}
