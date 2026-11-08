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

import mod.gottsch.forge.dungeonblocks.core.block.SawTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * A {@link SawTrapBlock}'s block entity: no data of its own, only a place for
 * {@code SawTrapRenderer} to keep how far the blade is out while it rises and sinks. The trap's
 * state is all in its blockstate (RAISED, AXIS), so nothing is saved or synced here.
 *
 * @author Mark Gottschling on 11/05/2026
 */
public class SawTrapBlockEntity extends BlockEntity {
    /** Client only: the RAISED value the blade is moving toward; null until first drawn. */
    public Boolean shownRaised;
    /** Client only: where the current move started, 0 (down) to 1 (up). */
    public float raiseFrom;
    /** Client only: game time (with partial tick) the current move started. */
    public double moveStart = Double.NEGATIVE_INFINITY;
    /** Client only: how far out the blade was drawn last frame, 0 (down) to 1 (up). */
    public float raise;

    public SawTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SAW_TRAP.get(), pos, state);
    }

    /** The block and the one above it, where the blade comes out. */
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, 1, 0);
    }
}
