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

import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

/**
 * Skeleton Remains: a skeleton laid out as set dressing, posed, that rests wherever it is put rather
 * than on the block grid - on a slab, a bed, a bench, half a block from a wall - and turns to any
 * 22.5 degrees. The skeleton block's alternative where placement matters; the block stays the
 * cheap choice for filling a catacomb, since an entity is drawn every frame it is in view.
 *
 * <p>No AI. It falls onto whatever is under it and then lies still. A bone cycles its {@link Posture};
 * punching it drops it as an item. Drawn by {@code SkeletonRemainsRenderer} from vanilla's
 * skeleton model in the mod's warm bone colour.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class SkeletonRemainsEntity extends Entity {
    private static final EntityDataAccessor<Byte> DATA_POSTURE =
            SynchedEntityData.defineId(SkeletonRemainsEntity.class, EntityDataSerializers.BYTE);
    private static final double GRAVITY = 0.04D;

    /**
     * How the remains lie. Saved by ordinal: append new ones, never reorder. Vanilla's skeleton has
     * no knees or elbows, so every pose is whole limbs turned at the shoulder and hip.
     */
    public enum Posture {
        /** Flat on its back, head turned aside, the way a body is laid out. */
        LYING,
        /** Slumped against a wall, legs out in front, head fallen forward. */
        SITTING,
        /** Curled on its side, arms in front, legs drawn up. */
        ON_SIDE,
        /** Sprawled face down, one arm flung out past its head. */
        FACE_DOWN,
        /** Shackled by the wrists to the ceiling, arms straight up, head fallen forward. Placed on a ceiling only. */
        HANGING;

        /** The order a bone cycles the poses that rest on something; hanging is not among them. */
        private static final Posture[] RESTING = {LYING, ON_SIDE, FACE_DOWN, SITTING};

        public Posture next() {
            for (int i = 0; i < RESTING.length; i++) {
                if (RESTING[i] == this) {
                    return RESTING[(i + 1) % RESTING.length];
                }
            }
            return this;
        }

        static Posture byId(int id) {
            return id >= 0 && id < values().length ? values()[id] : LYING;
        }
    }

    /**
     * How far a hanging skeleton's raised hands reach above its feet, in blocks: 36px, its shoulders
     * pulled 4px up by its weight (SkeletonRemainsRenderer.SHRUG). Vanilla's raised arms reach only
     * the top of the head (32px), which put the cuffs beside the skull.
     */
    public static final double HANDS_UP = 2.25D;
    /**
     * How far below the ceiling a hanging skeleton's feet are: its raised hands, and above them the
     * shackles' half block of chain (tools/gen_bbmodels.py skeleton_remains_shackles).
     */
    public static final double HANGING_REACH = HANDS_UP + 0.5D;
    /** A hanging one's box: as wide as its spread arms (the wrists 5.9px out), as tall as its raised hands. */
    private static final EntityDimensions HANGING_SIZE = EntityDimensions.fixed(0.75F, (float) HANDS_UP);

    public SkeletonRemainsEntity(EntityType<? extends SkeletonRemainsEntity> type, Level level) {
        super(type, level);
    }

    public Posture getPosture() {
        return Posture.byId(this.entityData.get(DATA_POSTURE));
    }

    public void setPosture(Posture posture) {
        this.entityData.set(DATA_POSTURE, (byte) posture.ordinal());
    }

    /**
     * A hanging one's box is its whole body, feet to raised hands, so it can be punched anywhere on
     * it; the type's small box at its feet was hard to hit. The resting poses keep the small box,
     * the ground they rest on.
     */
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getPosture() == Posture.HANGING ? HANGING_SIZE : super.getDimensions(pose);
    }

    /** The box follows the posture, on both sides: setting synched data calls this on the server too. */
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_POSTURE.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_POSTURE, (byte) Posture.LYING.ordinal());
    }

    /** Falls until it rests on something, then lies still. A hanging one has no gravity, and never moves. */
    @Override
    public void tick() {
        super.tick();
        boolean still = this.getDeltaMovement().lengthSqr() == 0;
        if (this.level().isClientSide || (still && (this.onGround() || this.isNoGravity()))) {
            return;
        }
        if (!this.onGround() && !this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.onGround() ? this.getDeltaMovement().multiply(0.0D, 0.0D, 0.0D)
                : this.getDeltaMovement().multiply(0.9D, 0.98D, 0.9D));
    }

    /** A bone in hand cycles the resting postures. A hanging one stays hanging. */
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!player.getItemInHand(hand).is(Items.BONE) || this.getPosture() == Posture.HANGING) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            this.setPosture(this.getPosture().next());
            this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 0.8F);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    /** Punched, it drops as an item (none in creative). */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.isRemoved() || this.level().isClientSide) {
            return false;
        }
        if (!(source.getEntity() instanceof Player player && player.getAbilities().instabuild)) {
            this.spawnAtLocation(this.getPickResult());
        }
        this.playSound(SoundEvents.SKELETON_HURT, 0.6F, 0.7F);
        this.gameEvent(GameEvent.ENTITY_DIE, source.getEntity());
        this.discard();
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.SKELETON_REMAINS.get());
    }

    /**
     * The whole body, not just the small box it rests on, so it is not culled while part shows: a
     * lying one reaches about 1.25 blocks out, a hanging one 2.5 blocks up to its shackles.
     */
    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.25D, 0.0D, 1.25D).expandTowards(0.0D, HANGING_REACH, 0.0D);
    }

    /** Drawn as far as a full-size mob, not as far as its small resting box would allow. */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = 64.0D * getViewScale();
        return distance < range * range;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setPosture(Posture.byId(tag.getByte("Posture")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Posture", (byte) this.getPosture().ordinal());
    }
}
