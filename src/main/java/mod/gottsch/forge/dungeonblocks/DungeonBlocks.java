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
package mod.gottsch.forge.dungeonblocks;

import mod.gottsch.forge.dungeonblocks.core.item.ModCreativeModeTabs;
import mod.gottsch.forge.dungeonblocks.core.particle.ModParticles;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.config.DungeonBlocksConfig;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

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

	public DungeonBlocks(IEventBus modEventBus, ModContainer modContainer) {
		DungeonBlocks.instance = this;
		modContainer.registerConfig(ModConfig.Type.COMMON, DungeonBlocksConfig.COMMON_CONFIG);

		// register the deferred registries
		ModBlocks.register(modEventBus);
		ModItems.register(modEventBus);
		ModParticles.register(modEventBus);

		ModCreativeModeTabs.TABS.register(modEventBus);

		// Register the setup method for modloading
		modEventBus.addListener(this::setup);
	}

	/**
	 * ie. preint
	 *
	 * @param event
	 */
	private void setup(final FMLCommonSetupEvent event) {
	}

}
