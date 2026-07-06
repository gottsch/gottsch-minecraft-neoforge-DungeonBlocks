/*
 * This file is part of  Dungeon Blocks
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.core.setup;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 *
 * @author Mark Gottschling on Aug 17, 2022
 *
 */
public class CommonSetup {
    
    public static void setup() {
    }
    
    public static void init(FMLCommonSetupEvent event) {
    }
    
//	@SubscribeEvent
//	public static void registemItemsToTab(BuildContents event) {
//		if (event.getTab() == ModCreativeModeTabs.MOD_TAB) {
//			ModBlocks.MAP.forEach((block, item) -> {
//				event.accept(item.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
//			});
//		}
//	}
}
