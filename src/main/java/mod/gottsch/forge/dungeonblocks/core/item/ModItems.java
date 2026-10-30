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
package mod.gottsch.forge.dungeonblocks.core.item;

import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.ModEntityTypes;
import mod.gottsch.forge.dungeonblocks.core.entity.ScrollVariant;
import mod.gottsch.forge.dungeonblocks.core.entity.TomeVariant;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Mark Gottschling on Jan 13, 2020
 * This class has the register event handler for all custom items.
 */
public class ModItems {

	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();
	public static final Supplier<Item.Properties> ITEM_PROPS_SUPPLIER = Item.Properties::new;

	public static final DeferredHolder<Item, Item> LOGO = Registration.ITEMS.register("dungeonblocks_logo", () -> new Item(new Item.Properties()));

	static {
		Registration.BLOCKS.getEntries().forEach(block -> {
			if (block != ModBlocks.MOLD && block != ModBlocks.LICHEN
			&& block != ModBlocks.SKELETON) {
				ModBlocks.MAP.put(block, fromBlock(block, ModItems.ITEM_PROPERTIES));
			}
		});
	}

	// static referenced items
	public static DeferredHolder<Item, BlockItem> MOLD = fromBlock(ModBlocks.MOLD, ITEM_PROPERTIES);
	public static DeferredHolder<Item, BlockItem> LICHEN = fromBlock(ModBlocks.LICHEN, ITEM_PROPERTIES);
	public static DeferredHolder<Item, SkeletonItem> SKELETON = Registration.ITEMS.register("skeleton", () -> new SkeletonItem(ModBlocks.SKELETON.get(), new Item.Properties()));

	// entity-backed decorative props. The lambdas defer ModEntityTypes' class init so it isn't
	// forced during ModItems' own static initialization.
	public static final DeferredHolder<Item, Item> POT = Registration.ITEMS.register("pot",
			() -> new PotItem(() -> ModEntityTypes.POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> SQUAT_CLAY_POT = Registration.ITEMS.register("squat_clay_pot",
			() -> new PotItem(() -> ModEntityTypes.SQUAT_CLAY_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> THIN_CLAY_POT = Registration.ITEMS.register("thin_clay_pot",
			() -> new PotItem(() -> ModEntityTypes.THIN_CLAY_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> STONE_POT = Registration.ITEMS.register("stone_pot",
			() -> new PotItem(() -> ModEntityTypes.STONE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> SQUAT_STONE_POT = Registration.ITEMS.register("squat_stone_pot",
			() -> new PotItem(() -> ModEntityTypes.SQUAT_STONE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> THIN_STONE_POT = Registration.ITEMS.register("thin_stone_pot",
			() -> new PotItem(() -> ModEntityTypes.THIN_STONE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> RED_POT = Registration.ITEMS.register("red_pot",
			() -> new PotItem(() -> ModEntityTypes.RED_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> SQUAT_RED_POT = Registration.ITEMS.register("squat_red_pot",
			() -> new PotItem(() -> ModEntityTypes.SQUAT_RED_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> THIN_RED_POT = Registration.ITEMS.register("thin_red_pot",
			() -> new PotItem(() -> ModEntityTypes.THIN_RED_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BLUE_POT = Registration.ITEMS.register("blue_pot",
			() -> new PotItem(() -> ModEntityTypes.BLUE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> SQUAT_BLUE_POT = Registration.ITEMS.register("squat_blue_pot",
			() -> new PotItem(() -> ModEntityTypes.SQUAT_BLUE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> THIN_BLUE_POT = Registration.ITEMS.register("thin_blue_pot",
			() -> new PotItem(() -> ModEntityTypes.THIN_BLUE_POT.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BIG_RED_POTION = Registration.ITEMS.register("big_red_potion",
			() -> new PotItem(() -> ModEntityTypes.BIG_RED_POTION.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> RED_FLASK = Registration.ITEMS.register("red_flask",
			() -> new PotItem(() -> ModEntityTypes.RED_FLASK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BIG_YELLOW_POTION = Registration.ITEMS.register("big_yellow_potion",
			() -> new PotItem(() -> ModEntityTypes.BIG_YELLOW_POTION.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> YELLOW_FLASK = Registration.ITEMS.register("yellow_flask",
			() -> new PotItem(() -> ModEntityTypes.YELLOW_FLASK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BIG_BLUE_POTION = Registration.ITEMS.register("big_blue_potion",
			() -> new PotItem(() -> ModEntityTypes.BIG_BLUE_POTION.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BLUE_FLASK = Registration.ITEMS.register("blue_flask",
			() -> new PotItem(() -> ModEntityTypes.BLUE_FLASK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> BIG_GREEN_POTION = Registration.ITEMS.register("big_green_potion",
			() -> new PotItem(() -> ModEntityTypes.BIG_GREEN_POTION.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> GREEN_FLASK = Registration.ITEMS.register("green_flask",
			() -> new PotItem(() -> ModEntityTypes.GREEN_FLASK.get(), new Item.Properties()));

	/** A tome item per cover, each laying down a TomeEntity with that cover. */
	public static final Map<TomeVariant, DeferredHolder<Item, Item>> TOMES = tomes();

	private static Map<TomeVariant, DeferredHolder<Item, Item>> tomes() {
		Map<TomeVariant, DeferredHolder<Item, Item>> tomes = new EnumMap<>(TomeVariant.class);
		for (TomeVariant variant : TomeVariant.values()) {
			tomes.put(variant, Registration.ITEMS.register(variant.id(),
					() -> new TomeItem(variant, new Item.Properties().stacksTo(16))));
		}
		return tomes;
	}

	/** A scroll item per design, each laying down a ScrollEntity with that design. */
	public static final Map<ScrollVariant, DeferredHolder<Item, Item>> SCROLLS = scrolls();

	private static Map<ScrollVariant, DeferredHolder<Item, Item>> scrolls() {
		Map<ScrollVariant, DeferredHolder<Item, Item>> scrolls = new EnumMap<>(ScrollVariant.class);
		for (ScrollVariant variant : ScrollVariant.values()) {
			scrolls.put(variant, Registration.ITEMS.register(variant.id(),
					() -> new ScrollItem(variant, new Item.Properties().stacksTo(16))));
		}
		return scrolls;
	}

	/**
	 * Items belonging to the decorative-entity subsystem. These are pulled out of the main
	 * DungeonBlocks tab (which otherwise sweeps up every registered item) and shown in the
	 * DungeonBlocks Entities tab instead — see {@link ModCreativeModeTabs}.
	 */
	public static final List<DeferredHolder<Item, Item>> ENTITY_ITEMS = Stream.concat(Stream.of(
			POT, SQUAT_CLAY_POT, THIN_CLAY_POT,
			STONE_POT, SQUAT_STONE_POT, THIN_STONE_POT,
			RED_POT, SQUAT_RED_POT, THIN_RED_POT,
			BLUE_POT, SQUAT_BLUE_POT, THIN_BLUE_POT,
			BIG_RED_POTION, RED_FLASK,
			BIG_YELLOW_POTION, YELLOW_FLASK,
			BIG_BLUE_POTION, BLUE_FLASK,
			BIG_GREEN_POTION, GREEN_FLASK), Stream.concat(TOMES.values().stream(), SCROLLS.values().stream())).toList();

	public static void register(IEventBus bus) {
		// cycle through all block and create items
		Registration.registerItems(bus);
	}

	// convenience method: take a DeferredHolder<Block> and make a corresponding DeferredHolder<Item> from it
	public static <B extends Block> DeferredHolder<Item, BlockItem> fromBlock(DeferredHolder<Block, B> block, Item.Properties itemProperties) {
		return Registration.ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), itemProperties));
	}
}
