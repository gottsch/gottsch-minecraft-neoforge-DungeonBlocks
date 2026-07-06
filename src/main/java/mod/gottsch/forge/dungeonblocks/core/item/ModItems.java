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
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * @author Mark Gottschling on Jan 13, 2020
 * This class has the register event handler for all custom items.
 */
public class ModItems {

	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();
	public static final Supplier<Item.Properties> ITEM_PROPS_SUPPLIER = Item.Properties::new;

	public static final DeferredHolder<Item, Item> LOGO = Registration.ITEMS.register("dungeonblocks_logo", () -> new Item(new Item.Properties()));

	static {
		// create items
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

	/**
	 *
	 */
	public static void register(IEventBus bus) {
		// cycle through all block and create items
		Registration.registerItems(bus);
	}

	// convenience method: take a DeferredHolder<Block> and make a corresponding DeferredHolder<Item> from it
	public static <B extends Block> DeferredHolder<Item, BlockItem> fromBlock(DeferredHolder<Block, B> block, Item.Properties itemProperties) {
		return Registration.ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), itemProperties));
	}
}
