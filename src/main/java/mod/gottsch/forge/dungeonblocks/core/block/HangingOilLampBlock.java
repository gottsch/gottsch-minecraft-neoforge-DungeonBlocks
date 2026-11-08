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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A terracotta oil lamp with a spout, hung from a dark iron bail on a chain, as a lantern hangs. Lit
 * and put out as the censer is (a torch or flint and steel lights it, an empty hand or water puts it
 * out), but it is a lamp: a small flame burns at the spout's tip and it gives light 12.
 *
 * <p>On a swinging chain it is the {@code ChainFixture.OIL_LAMP} fixture, drawn from the same models
 * (blockbench/hanging_oil_lamp.bbmodel).
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class HangingOilLampBlock extends CenserBlock {
    /** A lamp, a little dimmer than a lantern. The same on a swinging chain (ChainFixture). */
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIT) ? 12 : 0;
    /** The bowl and spout, and the bail and chain above. */
    public static final VoxelShape SHAPE = Shapes.or(Block.box(4.5, 1, 3, 11.5, 5, 13.5), Block.box(7, 5, 7, 9, 16, 9));
    /** The wick's tip, in the lamp's block space: where the flame burns. */
    private static final Vector3f FLAME = new Vector3f(8.0F / 16.0F, 5.9F / 16.0F, 12.5F / 16.0F);

    public HangingOilLampBlock(Properties properties) {
        super(properties, SHAPE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            flame(level, random, at -> new Vec3(pos.getX() + at.x, pos.getY() + at.y, pos.getZ() + at.z));
        }
    }

    /**
     * One tick's flame at the wick, and now and then a wisp of smoke. {@code place} takes a point in
     * the lamp's block space (0-1) to the world: straight across for the block, along the swing on a
     * swinging chain.
     */
    public static void flame(Level level, RandomSource random, Function<Vector3f, Vec3> place) {
        Vec3 at = place.apply(new Vector3f(FLAME));
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.SMALL_FLAME, at.x, at.y, at.z, 0.0D, 0.0D, 0.0D);
        }
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.SMOKE, at.x, at.y + 0.05D, at.z, 0.0D, 0.01D, 0.0D);
        }
    }
}
