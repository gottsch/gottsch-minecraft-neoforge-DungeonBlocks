/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.datagen;

/**
 * Wood naming for datagen: the palisade pieces are named by wood, their textures by log.
 * (This once also held the id-substring lists datagen dispatched on; those are gone - datagen
 * now reads ModBlocks.DECOR, ModBlocks.STONE_BLOCKS and the block families instead.)
 */
public final class DataGenMaps {
    private DataGenMaps() {
    }

    /**
     * Vanilla's word for a wood's log block, from the wood's name: the nether woods grow stems and
     * bamboo a block. The palisade pieces are named by wood, their textures by log.
     */
    public static String logOf(String wood) {
        return switch (wood) {
            case "crimson", "warped" -> wood + "_stem";
            case "bamboo" -> "bamboo_block";
            default -> wood + "_log";
        };
    }

    /** The wood a palisade piece is made of: its id, less the piece's own name. */
    public static String woodOf(String id, String piece) {
        return id.substring(0, id.length() - piece.length() - 1);
    }
}
