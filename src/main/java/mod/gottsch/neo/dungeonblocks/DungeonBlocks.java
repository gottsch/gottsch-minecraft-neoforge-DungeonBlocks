/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2020 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.dungeonblocks;

import mod.gottsch.neo.dungeonblocks.core.item.ModCreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.gottsch.neo.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.neo.dungeonblocks.core.config.DungeonBlocksConfig;
import mod.gottsch.neo.dungeonblocks.core.item.ModItems;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Mark Gottschling on Jan 1, 2020
 *
 */
@Mod(value = DungeonBlocks.MOD_ID)
public class DungeonBlocks {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(DungeonBlocks.class.getSimpleName());

	// constants
	public static final String MOD_ID = "dungeonblocks";
	public static DungeonBlocks instance;

	public DungeonBlocks() {
		DungeonBlocks.instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DungeonBlocksConfig.COMMON_CONFIG);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// register the deferred registries
		ModBlocks.register();
		ModItems.register();

		ModCreativeModeTabs.TABS.register(modEventBus);

		// Register the setup method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	@SubscribeEvent
	private void setup(final FMLCommonSetupEvent event) {
	}

}
