/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.dungeonblocks.core.setup;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.dungeonblocks.core.block.BarredWindows;
import mod.gottsch.neo.dungeonblocks.core.block.CorbelBlocks;
import mod.gottsch.neo.dungeonblocks.core.block.LedgeBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;


/**
 * 
 * @author Mark Gottschling on Aug 28, 2022
 *
 */
public class Registration {
	/*
	 * deferred registries
	 */
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DungeonBlocks.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DungeonBlocks.MOD_ID);
		
	/**
	 * 
	 */
	public static void registerBlocks() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		BarredWindows.register();
		//KeystoneBlocks.register();
		LedgeBlocks.register();
		CorbelBlocks.register();

		BLOCKS.register(eventBus);
	}
	
	/**
	 * 
	 */
	public static void registerItems() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(eventBus);
	}
}
