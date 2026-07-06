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
package mod.gottsch.forge.dungeonblocks.core.setup;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.BarredWindows;
import mod.gottsch.forge.dungeonblocks.core.block.CorbelBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.LedgeBlocks;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
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
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, DungeonBlocks.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, DungeonBlocks.MOD_ID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, DungeonBlocks.MOD_ID);

	/**
	 *
	 */
	public static void registerBlocks(IEventBus bus) {
		BarredWindows.register();
		//KeystoneBlocks.register();
		LedgeBlocks.register();
		CorbelBlocks.register();

		BLOCKS.register(bus);
	}

	/**
	 *
	 */
	public static void registerItems(IEventBus bus) {
		ITEMS.register(bus);
	}

	public static void registerParticles(IEventBus bus) {
		PARTICLES.register(bus);
	}
}
