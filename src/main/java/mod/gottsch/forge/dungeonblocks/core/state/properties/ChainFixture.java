/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.core.state.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

/**
 * What, if anything, hangs off the bottom of a swinging chain.
 *
 * <p>The lanterns are rendered from their <em>existing</em> block models via
 * {@code BlockRenderDispatcher#renderSingleBlock}, so they cost no new geometry and inherit the
 * player's resource pack. The mod's own fixtures - manacles, a meat hook, a censer - are blocks
 * too ({@code HangingFixtureBlock}, {@code CenserBlock}), which hang under a vanilla chain or a
 * ceiling; on a swinging chain they are drawn from the same model ({@link #model}), built from
 * blockbench/chain_fixture_*.bbmodel. Adding one is an entry here, its block, and a case in the
 * chain's item mapping.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
public enum ChainFixture implements StringRepresentable {

	NONE("none"),
	LANTERN("lantern"),
	SOUL_LANTERN("soul_lantern"),
	DUNGEON_LANTERN("dungeon_lantern"),
	MANACLES("manacles"),
	MEAT_HOOK("meat_hook"),
	CENSER("censer");

	private final String name;

	ChainFixture(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public boolean isPresent() {
		return this != NONE;
	}

	/**
	 * A fixture heavy enough to change how the chain swings. A mass concentrated at the free end
	 * makes a distributed-mass chain behave more like a simple pendulum — slightly longer period —
	 * and raises inertia relative to damping, so it keeps swinging noticeably longer.
	 */
	public boolean isWeighted() {
		// a pair of cuffs is too light to change how the chain moves
		return this != NONE && this != MANACLES;
	}

	/**
	 * True for fixtures that can be lit and extinguished. The mod's own dungeon lantern is placed
	 * unlit and lit with a torch or flint and steel ({@code DungeonLanternBlock#use}); the chain
	 * mirrors that rather than silently forcing it alight.
	 */
	public boolean isLightable() {
		return this == DUNGEON_LANTERN || this == CENSER;
	}

	/** Block light emitted while attached. Matches each source's own vanilla/mod value. */
	public int lightLevel(boolean lit) {
		return switch (this) {
			case LANTERN -> 15;
			case SOUL_LANTERN -> 10;
			case DUNGEON_LANTERN -> lit ? 15 : 0;
			// smouldering incense: a glow, not a lamp. CenserBlock gives the same.
			case CENSER -> lit ? 7 : 0;
			case NONE, MANACLES, MEAT_HOOK -> 0;
		};
	}

	/**
	 * The model a fixture of the mod's own is drawn from, as a path under models/block, or null for
	 * the lanterns, which are drawn from their blocks. ClientSetup registers each of these.
	 */
	@Nullable
	public String model(boolean lit) {
		return switch (this) {
			case MANACLES -> "chain_fixture_manacles";
			case MEAT_HOOK -> "chain_fixture_meat_hook";
			case CENSER -> lit ? "chain_fixture_censer_lit" : "chain_fixture_censer";
			case NONE, LANTERN, SOUL_LANTERN, DUNGEON_LANTERN -> null;
		};
	}
}
