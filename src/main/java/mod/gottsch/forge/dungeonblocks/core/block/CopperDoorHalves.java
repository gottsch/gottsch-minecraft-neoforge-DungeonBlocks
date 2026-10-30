/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

/**
 * Keeps the two halves of a copper door the same block when one of them changes.
 * <p>
 * Weathering, waxing and scraping each replace only ONE half with a different block. In 1.20.1
 * {@link DoorBlock#updateShape} answers that by turning the other half to air (it accepts only a
 * partner of its own block), and then the first half to air as well: the door vanished, with no
 * drop. Instead the other half follows - it becomes the partner's block, as vanilla doors do from
 * 1.21 on.
 */
final class CopperDoorHalves {
    private CopperDoorHalves() {
    }

    /**
     * The state {@code state} should become because its partner half, {@code neighbor} in
     * direction {@code direction}, turned into a different copper door - or null if that is not
     * what happened, and the normal door update applies.
     */
    @Nullable
    static BlockState follow(BlockState state, Direction direction, BlockState neighbor) {
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean towardPartner = direction == (half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        if (towardPartner && neighbor.getBlock() != state.getBlock() && isCopperDoor(neighbor)
                && neighbor.getValue(DoorBlock.HALF) != half) {
            return neighbor.setValue(DoorBlock.HALF, half);
        }
        return null;
    }

    private static boolean isCopperDoor(BlockState state) {
        return state.getBlock() instanceof WeatheringCopperDoorBlock || state.getBlock() instanceof WaxedCopperDoorBlock;
    }
}
