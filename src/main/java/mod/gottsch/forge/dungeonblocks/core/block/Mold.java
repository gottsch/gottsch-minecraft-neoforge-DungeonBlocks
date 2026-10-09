/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import mod.gottsch.forge.dungeonblocks.core.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * @author by Mark Gottschling on 10/9/2025
 */
public class Mold extends GlowLichenBlock {

    public Mold(Properties properties) {
        super(properties);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext placeContext) {
        return !placeContext.getItemInHand().is(ModItems.MOLD.get()) || super.canBeReplaced(state, placeContext);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {

        if (random.nextDouble() < 0.9) {
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // initial positions - has a spread area of up to 1.5 blocks
        double xPos = (x + 0.5D);
        double yPos = y - 0.1D;
        double zPos = (z + 0.5D);
        double velocityX = 0;
        double velocityY = -0.1; //0
        double velocityZ = 0;

        SimpleParticleType particle = ModParticles.BLACK_SPORE_PARTICLE.get();

        try {
            world.addParticle(particle, false, xPos, yPos, zPos, velocityX, velocityY, velocityZ);
        }
        catch(Exception e) {
            DungeonBlocks.LOGGER.error("error with particle:", e);
        }
    }
}
