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

import mod.gottsch.forge.dungeonblocks.core.blockentity.LargeChandelierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A two-tier chandelier about four blocks across and two tall: the iron one (two octagonal wheels,
 * 8 candles and 16) and the bone one (the medium bone chandelier over a ring of 16 skulls). It hangs
 * from the block above as the others do, its upper tier in its own cell and its lower tier a block
 * down, and lights and goes out as {@link ChandelierBlock} does; light 15 from this one block only.
 *
 * <p><b>Drawn by a renderer.</b> A block model may only reach -16..32 px, and these reach about
 * -25..41, so the block's own model is empty (its break particle) and
 * {@code LargeChandelierRenderer} draws the chandelier from nine part models. Nothing ticks and no
 * data is kept; the block entity is only there to be rendered. Only the core cell can be clicked,
 * blocks can be placed inside the overhang, and there is no swinging-chain fixture for it.
 *
 * <p>The models are blockbench/large_chandelier.bbmodel and large_bone_chandelier.bbmodel, split by
 * tools/bbmodel_to_block_models.py.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class LargeChandelierBlock extends ChandelierBlock implements EntityBlock {
    /** The iron one's upper wheel across the cell, its hub and the chain. */
    public static final VoxelShape IRON_SHAPE = Shapes.or(Block.box(0, 3, 0, 16, 4.5, 16),
            Block.box(6.5, 0, 6.5, 9.5, 6, 9.5), Block.box(7, 6, 7, 9, 16, 9));
    /** The bone one's upper spokes across the cell, the vertebra column and the chain. */
    public static final VoxelShape BONE_SHAPE = Shapes.or(Block.box(0, 1.5, 0, 16, 3.5, 16),
            Block.box(6, 0, 6, 10, 12.5, 10), Block.box(7, 12.5, 7, 9, 16, 9));

    /**
     * The iron one's wicks, {x, y, z} px: the upper wheel's eight corners (16 px out) and, a block
     * down, the lower wheel's eight corners (30 px out) and the middles of its sides
     * (LARGE_IRON_* in tools/gen_bbmodels.py; a candle's flame is 5.5 px over its foot).
     */
    public static final double[][] IRON_WICKS = concat(ring(16, 8, 22.5, FLAME_Y),
            ring(30, 8, 22.5, -4.5), ring(30 * Math.cos(Math.toRadians(22.5)), 8, 0, -4.5));
    /**
     * The bone one's wicks: the medium bone chandelier's eight (14 px out, turned 22.5) and, a block down, the
     * sixteen on the lower skulls, at the octagon's corners (30 px out) and the middles of its sides
     * (LARGE_BONE_* in tools/gen_bbmodels.py).
     */
    public static final double[][] BONE_WICKS = concat(ring(14, 8, 22.5, FLAME_Y),
            ring(30, 8, 0, -0.5), ring(30 * Math.cos(Math.toRadians(22.5)), 8, 22.5, -0.5));

    private final VoxelShape shape;
    private final double[][] wicks;

    public LargeChandelierBlock(Properties properties, VoxelShape shape, double[][] wicks) {
        super(properties);
        this.shape = shape;
        this.wicks = wicks;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LargeChandelierBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            flamesAt(wicks, level, random, at -> new Vec3(pos.getX() + at.x, pos.getY() + at.y, pos.getZ() + at.z));
        }
    }
}
