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
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * A secret door: a vanilla door in the texture of a wall, so a shut one set in a wall of its stone
 * is just more wall - its face lies flush with the wall's, where a door's panel always sits. It
 * opens to redstone only, like an iron door: a hand does nothing, so clicking along a wall does
 * not give it away. A Lever Sconce on the wall beside it is the intended key.
 *
 * <p>Its block set type makes it an iron door to the rest of the game: villagers cannot open it,
 * zombies cannot break it down, and pathfinding treats a shut one as wall.
 */
public class HiddenDoorBlock extends DoorBlock {
    /** Redstone only, with a heavy stone door's sounds: a mechanism grinding it open and shut. */
    public static final BlockSetType HIDDEN_STONE = BlockSetType.register(new BlockSetType("dungeonblocks:hidden_stone",
            false, false, false, BlockSetType.PressurePlateSensitivity.MOBS, SoundType.STONE, SoundEvents.PISTON_CONTRACT, SoundEvents.PISTON_EXTEND,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));

    public HiddenDoorBlock(Properties properties) {
        super(HIDDEN_STONE, properties);
    }
}
