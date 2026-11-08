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
package mod.gottsch.forge.dungeonblocks.core.blockentity;

import mod.gottsch.forge.dungeonblocks.core.block.TombDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * The lower half of a {@link TombDoorBlock}: no data of its own, only a place for
 * {@code TombDoorRenderer} to keep where the slab is drawn while it slides. The door's state is all
 * in its blockstate (OPEN, FACING), so nothing is saved or synced here.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class TombDoorBlockEntity extends BlockEntity {
    /** Client only: the OPEN value the slab is animating toward; null until first drawn. */
    public Boolean shownOpen;
    /** Client only: where the current slide started, 0 (shut) to 1 (open). */
    public float slideFrom;
    /** Client only: game time (with partial tick) the current slide started. */
    public double moveStart = Double.NEGATIVE_INFINITY;
    /** Client only: where the slab was drawn last frame, 0 (shut) to 1 (open). */
    public float slide;

    public TombDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.TOMB_DOOR.get(), pos, state);
    }

    /** Both halves, and a block either side, for the slab sliding into the wall. */
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1, 0, 1).expandTowards(0, 1, 0);
    }
}
