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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A wooden coffin, the tapered "toe-pincher", two blocks long with an iron cross on its lid and a
 * red lining. Everything but its looks is the sarcophagus's: placed and broken as a pair, opened by
 * a right-click, and - when a structure or command has sealed it - holding loot or a guardian for
 * its first opening (see {@link mod.gottsch.forge.dungeonblocks.core.blockentity.SarcophagusBlockEntity}).
 *
 * <p>The lid swings up on a hinge along its long east edge rather than grinding aside. Like the
 * sarcophagus lid it is never baked: SarcophagusRenderer always draws it. The models are OBJs
 * (tools/gen_obj_models.py), because JSON models cannot taper.
 */
public class CoffinBlock extends SarcophagusBlock {
    /** The closed coffin, lid and cross included, in its facing's two orientations. */
    private static final VoxelShape ALONG_Z = Block.box(1, 0, 0, 15, 10, 16);
    private static final VoxelShape ALONG_X = Block.box(0, 0, 1, 16, 10, 15);

    public CoffinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? ALONG_Z : ALONG_X;
    }

    @Override
    public LidMotion lidMotion() {
        return LidMotion.HINGE;
    }

    @Override
    protected double lidTop() {
        return 10.0 / 16.0;
    }

    /** A chest's creak, pitched down. */
    @Override
    protected void playLidSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS,
                0.8F, 0.6F);
    }
}
