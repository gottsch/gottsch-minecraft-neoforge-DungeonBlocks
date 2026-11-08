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

import mod.gottsch.forge.dungeonblocks.core.block.LargeChandelierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * A {@link LargeChandelierBlock}'s block entity: no data, no ticking. It exists so
 * {@code LargeChandelierRenderer} can draw a chandelier wider than a block model may be; the
 * chandelier's state (LIT) is all in its blockstate.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class LargeChandelierBlockEntity extends BlockEntity {

    public LargeChandelierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LARGE_CHANDELIER.get(), pos, state);
    }

    /**
     * Everything the chandelier reaches - about 2.6 blocks out each way and a block down - so it is
     * not culled while its core block is off screen.
     */
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2, 0, 2).expandTowards(0, -1, 0);
    }
}
