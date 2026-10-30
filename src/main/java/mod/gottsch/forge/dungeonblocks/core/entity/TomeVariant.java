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
 * The tome covers, and the shape of book each is. The STANDARD ones are icons from Pixeltier's
 * Ultimate Fantasy RPG Icons (magic tomes): the icon is the item sprite as it is, and
 * tools/gen_tome_textures.py lifts its cover onto the entity texture; the id is the icon's file
 * name. The icons' covers are wider than tall, which stood upright reads as a book half sunk in the
 * floor, so the TALL tome is drawn by the same script instead. The id names the item and textures.
 */
public enum TomeVariant {
	OLD_BINDER("old_binder", Shape.STANDARD),
	CRIMSON_MAGIC_BOOK("crimson_magic_book", Shape.STANDARD),
	GOLDEN_SKULL_TOME("golden_skull_tome", Shape.STANDARD),
	OCCULT_BIBLE("occult_bible", Shape.STANDARD),
	OMINOUS_MANUSCRIPT("ominous_manuscript", Shape.STANDARD),
	TALL_LEATHER_TOME("tall_leather_tome", Shape.TALL);

	/**
	 * A book's size in texels, at 32 to the block: its cover's width (spine to fore-edge) and height,
	 * and the thickness of each half's block of pages. A lid is always one texel.
	 */
	public enum Shape {
		STANDARD(13, 11, 2),
		TALL(11, 16, 3);

		public static final int LID = 1;
		public final int width;
		public final int height;
		public final int pages;

		Shape(int width, int height, int pages) {
			this.width = width;
			this.height = height;
			this.pages = pages;
		}

		/** Closed, both lids and both blocks of pages. */
		public int thickness() {
			return 2 * (LID + this.pages);
		}
	}

	private final String id;
	private final Shape shape;

	TomeVariant(String id, Shape shape) {
		this.id = id;
		this.shape = shape;
	}

	public String id() {
		return this.id;
	}

	public Shape shape() {
		return this.shape;
	}

	/** The variant with this id, or the plain leather one for an id no longer known. */
	public static TomeVariant byId(String id) {
		for (TomeVariant variant : values()) {
			if (variant.id.equals(id)) {
				return variant;
			}
		}
		return OLD_BINDER;
	}
}
