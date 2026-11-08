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
 * The iron chandelier grown to a room's centrepiece: an octagonal wheel about two and a half blocks
 * across with a candle at each of its eight corners, on eight spokes, and a square inner ring of
 * four more, braced to the chain by four struts.
 *
 * <p>One block whose model overhangs its eight neighbours, as {@link MediumBoneChandelierBlock}'s
 * does: no block entity, and a structure places it like any block. It hangs from a sturdy ceiling or
 * a chain, or from a swinging chain as a fixture, and lights and goes out as {@link ChandelierBlock}
 * does. The models are blockbench/medium_chandelier.bbmodel, lit and unlit.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class MediumChandelierBlock extends ChandelierBlock {
    /** The spokes across the cell, the hub and the chain. */
    static final VoxelShape SHAPE = Shapes.or(Block.box(0, 3, 0, 16, 4.5, 16), Block.box(6.5, 2, 6.5, 9.5, 6, 9.5),
            Block.box(7, 6, 7, 9, 16, 9));
    /**
     * Each candle's wick: the wheel's eight corners, 20 px out at 22.5 + 45n degrees, and the inner
     * ring's four, 9 px out (MEDIUM_IRON_CORNERS and MEDIUM_IRON_INNER in tools/gen_bbmodels.py).
     */
    private static final double[][] FLAMES = concat(ring(20, 8, 22.5, FLAME_Y), ring(9, 4, 0, FLAME_Y));

    public MediumChandelierBlock(Properties properties) {
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

    /** One tick's flames and smoke, on this chandelier's twelve wicks: see {@link ChandelierBlock#flames}. */
    static void flames(Level level, RandomSource random, Function<Vector3f, Vec3> place) {
        flamesAt(FLAMES, level, random, place);
    }
}
