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
package mod.gottsch.forge.dungeonblocks.core.config;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.gottschcore.config.AbstractConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 *
 * @author Mark Gottschling on Jan 5, 2020
 *
 */
public class DungeonBlocksConfig extends AbstractConfig {
	protected static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
	protected static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

	public static ModConfigSpec COMMON_CONFIG;
	public static ModConfigSpec CLIENT_CONFIG;

	public static DungeonBlocksConfig instance = new DungeonBlocksConfig();
	
	static {
		COMMON_CONFIG = COMMON_BUILDER.build();
	}

	/**
	 * 
	 * @param mod
	 */
	public DungeonBlocksConfig() {
	}
}
