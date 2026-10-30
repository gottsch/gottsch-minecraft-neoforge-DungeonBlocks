/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.core.state.properties;

import net.minecraft.util.StringRepresentable;

/**
 * Vertical position of a single-block segment within a {@link mod.gottsch.forge.dungeonblocks.core.block.TallDoorBlock}
 * stack. Unlike vanilla's two-value {@code DoubleBlockHalf}, any number of interior segments
 * all share the single MIDDLE value - the middle texture is expected to tile, so a 3-tall and
 * a 4-tall door (or taller) need no additional blockstate values beyond these three.
 *
 * @author Mark Gottschling on Jul 19, 2026
 */
public enum DoorSegment implements StringRepresentable {
    BOTTOM("bottom"),
    MIDDLE("middle"),
    TOP("top");

    private final String name;

    DoorSegment(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return getName();
    }
}
