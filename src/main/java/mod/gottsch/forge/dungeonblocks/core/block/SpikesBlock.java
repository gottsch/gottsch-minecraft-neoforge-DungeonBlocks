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

import mod.gottsch.forge.dungeonblocks.core.damage.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A bed of iron spikes (models/block/spikes.obj): a 1px plate carrying a grid of spikes 11px tall,
 * pointing away from the face it was placed on - so it works on floors, walls and ceilings.
 *
 * <p>Only the plate collides. A mob walks INTO the spikes, which is what lets entityInside hurt it,
 * the way a sweet berry bush does: slowed while it stands there, and hurt whenever it moves. A fall
 * onto upward spikes adds to the fall damage, the way pointed dripstone does.
 */
public class SpikesBlock extends PyramidBlock {
    private static final Map<Direction, VoxelShape> PLATE = slabs(1);
    private static final Map<Direction, VoxelShape> OUTLINE = slabs(12);

    public SpikesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PLATE.get(state.getValue(FACING));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        entity.makeStuckInBlock(state, new Vec3(0.8D, 0.75D, 0.8D));
        if (!level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
            double dx = Math.abs(entity.getX() - entity.xOld);
            double dz = Math.abs(entity.getZ() - entity.zOld);
            if (dx >= 0.003D || dz >= 0.003D) {
                entity.hurt(ModDamageTypes.spikes(level), 1.0F);
            }
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.getValue(FACING) == Direction.UP) {
            entity.causeFallDamage(fallDistance + 2.0F, 2.0F, ModDamageTypes.spikes(level));
        } else {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
}
