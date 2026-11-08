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
package mod.gottsch.forge.dungeonblocks.core.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import mod.gottsch.forge.dungeonblocks.core.block.StoneBenchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * What a player sits on on a stone bench: an invisible entity they ride, one per block of bench.
 * {@link #sit} makes one and seats the player; it removes itself as soon as no one is on it or its
 * block is no longer a bench, and it is never saved, so no empty seats are left about the world.
 * Standing up puts the player on top of the bench, or beside it if there is no room.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class SeatEntity extends Entity {
    /**
     * How far above the block the seat is. A rider's own offset (-0.35 for a player) puts their feet
     * below it, and a sitting player's seat is about 0.6 above their feet, so this sits them on the
     * bench's top at 9px. Tune in game if they float or sink.
     */
    private static final double SEAT_Y = 0.29;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Seats the player on the bench at `pos`, unless someone already sits there. */
    public static boolean sit(Level level, BlockPos pos, Player player) {
        if (!level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty()) {
            return false;
        }
        SeatEntity seat = new SeatEntity(ModEntityTypes.SEAT.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + SEAT_Y, pos.getZ() + 0.5);
        if (!level.addFreshEntity(seat)) {
            return false;
        }
        if (!player.startRiding(seat)) {
            seat.discard();
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && (getPassengers().isEmpty()
                || !(level().getBlockState(blockPosition()).getBlock() instanceof StoneBenchBlock))) {
            ejectPassengers();
            discard();
        }
    }

    // No passenger offset: 1.21 seats a rider at the top of this 0.25 box less the player's 0.6 vehicle
    // attachment, -0.35 in all, which is what Forge's 0 plus the player's own -0.35 riding offset gave.

    /** Stand up on the bench where there is headroom, else on a free spot beside it. */
    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        BlockPos pos = blockPosition();
        Vec3 on = DismountHelper.findSafeDismountLocation(passenger.getType(), level(), pos, false);
        if (on != null) {
            return on;
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            Vec3 beside = DismountHelper.findSafeDismountLocation(passenger.getType(), level(), pos.relative(side), false);
            if (beside != null) {
                return beside;
            }
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    /** Never saved: a seat only exists while someone sits on it. */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
