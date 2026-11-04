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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.function.Function;

/**
 * The bone chandelier grown to a room's centrepiece: eight skulls round a ring about two blocks
 * across, each with a candle on its crown, on femur spokes from a vertebra column, a femur rim from
 * skull to skull and ribs leaning in to the chain.
 *
 * <p><b>One block.</b> The model overhangs the eight neighbouring cells (a block model may reach
 * -16..32), so there is no block entity and no multi-block bookkeeping; a structure places it like
 * any block. Only the cell's own part can be clicked, and blocks can be placed in the overhang.
 *
 * <p>It hangs from a sturdy ceiling or a chain, or from a swinging chain as a fixture, and lights
 * and goes out as {@link ChandelierBlock} does. The models are
 * blockbench/medium_bone_chandelier.bbmodel, lit and unlit.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class MediumBoneChandelierBlock extends ChandelierBlock {
    /** The spokes across the cell, the vertebra column and the chain. */
    static final VoxelShape SHAPE = Shapes.or(Block.box(0, 1.5, 0, 16, 3.5, 16), Block.box(6, 0, 6, 10, 12.5, 10),
            Block.box(7, 12.5, 7, 9, 16, 9));
    /** Each candle's wick, on its skull (MEDIUM_BONE_CHANDELIER_CANDLES in tools/gen_bbmodels.py). */
    private static final double[][] FLAMES = {{22, 8}, {17.9, 17.9}, {8, 22}, {-1.9, 17.9}, {-6, 8},
            {-1.9, -1.9}, {8, -6}, {17.9, -1.9}};

    public MediumBoneChandelierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            flames(level, random, at -> new Vec3(pos.getX() + at.x, pos.getY() + at.y, pos.getZ() + at.z));
        }
    }

    /** One tick's flames and smoke, on this chandelier's eight wicks: see {@link ChandelierBlock#flames}. */
    static void flames(Level level, RandomSource random, Function<Vector3f, Vec3> place) {
        ChandelierBlock.flames(FLAMES, level, random, place);
    }
}
