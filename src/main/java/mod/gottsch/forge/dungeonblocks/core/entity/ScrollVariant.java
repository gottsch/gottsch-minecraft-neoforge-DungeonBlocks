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
package mod.gottsch.forge.dungeonblocks.core.entity;

/**
 * The scroll designs. Each is one icon from Pixeltier's Ultimate Fantasy RPG Icons (magic
 * scrolls): the icon is the item sprite as it is, and tools/gen_scroll_textures.py lifts its sheet
 * and roll onto the entity texture. The id names the item and textures; the icon it came from is
 * "scroll_" and the id's first word (fire_scroll from scroll_fire).
 */
public enum ScrollVariant {
	FIRE("fire_scroll", true),
	HAUNTED("haunted_scroll", true),
	HEALTH("health_scroll", true),
	HEARTS("hearts_scroll", true),
	ORB("orb_scroll", true),
	PLAIN("plain_scroll", false),
	RAIN("rain_scroll", true),
	SKULL("skull_scroll", true),
	STAR("star_scroll", true),
	WIND("wind_scroll", true);

	private final String id;
	private final boolean rune;

	ScrollVariant(String id, boolean rune) {
		this.id = id;
		this.rune = rune;
	}

	public String id() {
		return this.id;
	}

	/** A rune scroll gives off the odd enchanting glyph while open; a plain written one does not. */
	public boolean isRune() {
		return this.rune;
	}

	/** The variant with this id, or the plain scroll for an id no longer known. */
	public static ScrollVariant byId(String id) {
		for (ScrollVariant variant : values()) {
			if (variant.id.equals(id)) {
				return variant;
			}
		}
		return PLAIN;
	}
}
