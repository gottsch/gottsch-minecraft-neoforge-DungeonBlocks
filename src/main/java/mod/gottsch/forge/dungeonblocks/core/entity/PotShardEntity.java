/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Purely cosmetic ceramic debris flung out when a {@link PotEntity} shatters —
 * physics-driven like a dropped item (falls, slides to a stop, rests on top of
 * whatever it lands on, no arrow-style embedding) but short-lived and not
 * pickable, so a burst of shards doesn't linger or clutter the ground the way
 * item drops would. Modeled after {@code BoneShard} from GMM, minus the
 * arrow-projectile base (this needs to rest on surfaces, not stick in them).
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotShardEntity extends Entity {

	public static final int VARIANTS = 3;

	private static final EntityDataAccessor<Byte> DATA_VARIANT =
			SynchedEntityData.defineId(PotShardEntity.class, EntityDataSerializers.BYTE);
	// which material's shard texture to draw. Synced rather than derived: a shard is its own entity
	// type regardless of what shattered, so the client has nothing else to read the colour from.
	private static final EntityDataAccessor<Byte> DATA_MATERIAL =
			SynchedEntityData.defineId(PotShardEntity.class, EntityDataSerializers.BYTE);
	// synced because the client never runs the physics step below, so its own onGround() can't be
	// trusted to tell the renderer when to stop the tumble.
	private static final EntityDataAccessor<Boolean> DATA_LANDED =
			SynchedEntityData.defineId(PotShardEntity.class, EntityDataSerializers.BOOLEAN);

	// arrow-ish gravity — slightly snappier than a falling block, so chips arc down promptly
	private static final float GRAVITY = 0.05F;
	private static final float AIR_DRAG = 0.98F;
	private static final double SETTLE_THRESHOLD = 0.003D;
	private static final int MIN_LIFE_TICKS = 60;
	private static final int MAX_LIFE_TICKS = 100;

	private int lifeTicks = MIN_LIFE_TICKS;

	/** Advances only while airborne, so the renderer freezes the tumble once the shard settles. */
	private int spinTicks;

	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private float lerpYRot;
	private float lerpXRot;
	private int lerpSteps;

	public PotShardEntity(EntityType<? extends PotShardEntity> type, Level level) {
		super(type, level);
	}

	public PotShardEntity(Level level, double x, double y, double z, PotMaterial material) {
		this(ModEntityTypes.POT_SHARD.get(), level);
		this.setPos(x, y, z);
		this.lifeTicks = MIN_LIFE_TICKS + this.random.nextInt(MAX_LIFE_TICKS - MIN_LIFE_TICKS + 1);
		if (!level.isClientSide) {
			this.setVariant((byte) this.random.nextInt(VARIANTS));
			this.setMaterial(material);
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_VARIANT, (byte) 0);
		builder.define(DATA_MATERIAL, (byte) PotMaterial.TERRACOTTA.ordinal());
		builder.define(DATA_LANDED, false);
	}

	public void setMaterial(PotMaterial material) {
		this.entityData.set(DATA_MATERIAL, (byte) material.ordinal());
	}

	public PotMaterial getMaterial() {
		return PotMaterial.byOrdinal(this.entityData.get(DATA_MATERIAL));
	}

	public void setVariant(byte variant) {
		this.entityData.set(DATA_VARIANT, (byte) Math.floorMod(variant, VARIANTS));
	}

	public int getVariant() {
		return this.entityData.get(DATA_VARIANT);
	}

	/** True once the shard has settled onto a surface; the renderer stops tumbling it. */
	public boolean isLanded() {
		return this.entityData.get(DATA_LANDED);
	}

	public int getSpinTicks() {
		return this.spinTicks;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.tickCount > this.lifeTicks) {
			this.discard();
			return;
		}

		// runs on both sides so the client has its own spin counter to render from
		if (!this.isLanded()) {
			this.spinTicks++;
		}

		if (this.level().isClientSide) {
			if (this.lerpSteps > 0) {
				this.lerpPositionAndRotation();
			}
			return;
		}

		// gravity into the deltaMovement FIELD so it actually accumulates — see the matching note in
		// PotEntity#tick: move() does not write its argument back, so adding to a local loses it.
		if (!this.onGround() && !this.isNoGravity()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D));
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
		Vec3 motion = this.getDeltaMovement();

		double drag = AIR_DRAG;
		if (this.onGround()) {
			if (!this.isLanded()) {
				this.entityData.set(DATA_LANDED, true);
			}
			// fixed, aggressive ground drag (not surface-friction-derived) -- small debris chips
			// should stop skidding almost immediately regardless of what they land on.
			drag = 0.4D;
			motion = new Vec3(motion.x, 0.0D, motion.z);
		}

		motion = motion.multiply(drag, 0.98D, drag);

		if (Math.abs(motion.x) < SETTLE_THRESHOLD) {
			motion = new Vec3(0.0D, motion.y, motion.z);
		}
		if (Math.abs(motion.z) < SETTLE_THRESHOLD) {
			motion = new Vec3(motion.x, motion.y, 0.0D);
		}

		this.setDeltaMovement(motion);
	}

	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYRot = yRot;
		this.lerpXRot = xRot;
		this.lerpSteps = 3;
	}

	private void lerpPositionAndRotation() {
		double newX = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
		double newY = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
		double newZ = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
		double yRotDelta = Mth.wrapDegrees(this.lerpYRot - this.getYRot());
		float newYRot = (float) (this.getYRot() + yRotDelta / this.lerpSteps);
		float newXRot = (float) (this.getXRot() + (this.lerpXRot - this.getXRot()) / this.lerpSteps);
		this.lerpSteps--;
		this.setPos(newX, newY, newZ);
		this.setRot(newYRot, newXRot);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.lifeTicks = compound.getInt("Life");
		this.setVariant(compound.getByte("Variant"));
		this.setMaterial(PotMaterial.byOrdinal(compound.getByte("Material")));
		this.entityData.set(DATA_LANDED, compound.getBoolean("Landed"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Life", this.lifeTicks);
		compound.putByte("Variant", (byte) this.getVariant());
		compound.putByte("Material", (byte) this.getMaterial().ordinal());
		compound.putBoolean("Landed", this.isLanded());
	}
}
