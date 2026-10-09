/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 *
 * @author Mark Gottschling Feb 17, 2023
 *
 */
public class ModCreativeModeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DungeonBlocks.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = TABS.register("treasure_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.dungeonblocks"))
					// a flat sprite keeps its contrast at 16px, where a 3D block item shrinks to a
					// smudge: the skull on black cloth reads as "dungeon" at a glance
					.icon(() -> new ItemStack(ModBlocks.UNDEAD_PENNANT.get()))
					.displayItems((displayParams, output) -> {
						// add all items except the logo and the decorative-entity props,
						// which live in ENTITIES_TAB
						Registration.ITEMS.getEntries().forEach(item -> {
							if (!item.equals(ModItems.LOGO) && !ModItems.ENTITY_ITEMS.contains(item)) {
								output.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
							}
						});
					})
					.build()
	);

	/**
	 * Home for the entity-backed decorative props: pots, potions, tomes and scrolls. Kept separate
	 * from the block tab because these are Entities, not Blocks, and behave
	 * differently in-world — and because the block tab is already large.
	 */
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENTITIES_TAB = TABS.register("entities_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.dungeonblocks.entities"))
					.icon(() -> new ItemStack(ModItems.POT.get()))
					.withTabsBefore(MOD_TAB.getKey())
					.displayItems((displayParams, output) ->
							ModItems.ENTITY_ITEMS.forEach(item ->
									output.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)))
					.build()
	);
}
