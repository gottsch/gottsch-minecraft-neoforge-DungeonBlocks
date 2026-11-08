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
 * A chandelier of the dead, in the manner of the Sedlec ossuary: four skulls looking outward, each
 * with a candle on its crown, joined by femurs and hung from a chain on four ribs. It hangs, lights
 * and goes out as the iron {@link ChandelierBlock} does, on a ceiling, a chain or a swinging chain.
 * The models are blockbench/bone_chandelier.bbmodel, lit and unlit.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class BoneChandelierBlock extends ChandelierBlock {
    /** The skulls and femurs, and the chain up to the ceiling. */
    static final VoxelShape SHAPE = Shapes.or(Block.box(0.5, 0, 0.5, 15.5, 10, 15.5), Block.box(7, 10, 7, 9, 16, 9));
    /** Each candle's wick, on its skull (BONE_CHANDELIER_CANDLES in tools/gen_bbmodels.py). */
    private static final double[][] FLAMES = {{8, 3}, {8, 13}, {3, 8}, {13, 8}};

    public BoneChandelierBlock(Properties properties) {
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

    /** One tick's flames and smoke, on this chandelier's four wicks: see {@link ChandelierBlock#flames}. */
    static void flames(Level level, RandomSource random, Function<Vector3f, Vec3> place) {
        ChandelierBlock.flames(FLAMES, level, random, place);
    }
}
