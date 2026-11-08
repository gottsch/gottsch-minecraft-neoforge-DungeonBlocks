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
 * Which part of a run of stone benches a block is, from its neighbours along the bench's axis:
 * alone, the negative (west or north) end, between two others, or the positive (east or south) end.
 * The ends carry a support; the middle is seat only.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public enum BenchPart implements StringRepresentable {
    SINGLE("single"),
    START("start"),
    MIDDLE("middle"),
    END("end");

    private final String name;

    BenchPart(String name) {
        this.name = name;
    }

    /** The part a bench is, given whether it joins a bench on its negative and on its positive side. */
    public static BenchPart of(boolean negative, boolean positive) {
        if (negative && positive) {
            return MIDDLE;
        }
        if (negative) {
            return END;
        }
        return positive ? START : SINGLE;
    }

    public boolean hasNegativeSupport() {
        return this == SINGLE || this == START;
    }

    public boolean hasPositiveSupport() {
        return this == SINGLE || this == END;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
