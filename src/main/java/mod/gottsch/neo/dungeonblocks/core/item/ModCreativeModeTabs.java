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
package mod.gottsch.neo.dungeonblocks.core.item;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 
 * @author Mark Gottschling Feb 17, 2023
 *
 */
public class ModCreativeModeTabs {
//	public static CreativeModeTab MOD_TAB;

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DungeonBlocks.MOD_ID);

	public static final Supplier<CreativeModeTab> MOD_TAB = TABS.register("treasure_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.dungeonblocks"))
					.icon(ModItems.LOGO.get()::getDefaultInstance)
					.displayItems((displayParams, output) -> {
						// add all items
						Registration.ITEMS.getEntries().forEach(item -> {
							if (!item.equals(ModItems.LOGO)) {
								output.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
							}
						});
					})
					.build()
	);

//	@SubscribeEvent
//	public static void registerTab(CreativeModeTabEvent.Register event) {
//		MOD_TAB = event.registerCreativeModeTab(new ResourceLocation(DungeonBlocks.MOD_ID, "dungeon_blocks_tab"),
//				builder -> builder.icon(() -> new ItemStack(ModItems.LOGO.get())).title(Component.translatable("itemGroup.dungeonblocks")));
//	}
}
