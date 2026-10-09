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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import mod.gottsch.forge.dungeonblocks.core.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A decorative, physics-driven prop entity: gravity pulls it down, it can be
 * shoved around by living entities ({@link #isPushable()}), and it shatters
 * into a non-explosive spray of shard items on a hard enough hit, collision,
 * or fall. A moderate-speed collision instead tips it over ({@link #isTumbled()}).
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotEntity extends Entity {

	private static final EntityDataAccessor<Boolean> DATA_TUMBLED =
			SynchedEntityData.defineId(PotEntity.class, EntityDataSerializers.BOOLEAN);

	// exactly vanilla FallingBlockEntity (sand/gravel/anvil) — accelerates, so it reads as a real drop
	private static final float GRAVITY = 0.04F;
	private static final float AIR_DRAG = 0.98F;
	private static final double SETTLE_THRESHOLD = 0.003D;
	private static final float FALL_BREAK_DISTANCE = 2.0F;
	private static final int MIN_SHARDS = 3;
	private static final int MAX_SHARDS = 6;
	// walking is ~0.216 blocks/tick, sprinting ~0.28: tumble on a walk-into or a plain sprint-into,
	// reserve shattering for something faster still (sprint-jump, a mount, knockback).
	private static final double TUMBLE_SPEED = 0.1D;
	private static final double SHATTER_SPEED = 0.35D;

	// tracked independently of Entity's built-in fall-damage plumbing (that hook is geared toward
	// LivingEntity and isn't reliably invoked for a plain physics Entity) so a hard fall reliably breaks it.
	private double fallStartY = Double.NaN;

	private static final int TUMBLE_ANIM_TICKS = 8;

	// client-side only: tick the tumble flag flipped, so the renderer can ease into the tip instead of
	// snapping. -1 means "already tumbled when this entity appeared" (load/spawn) — render fully tipped,
	// no animation replay.
	private int tumbleStartTick = -1;
	private boolean tickedOnce;

	// per-pot loot override. Null means "use the EntityType's default table"
	// (dungeonblocks:entities/<id>), which is what a hand-placed pot gets. A structure or a
	// worldgen feature can point an individual pot at a richer table instead — same NBT keys
	// vanilla containers use, so existing tooling and /data commands work unchanged.
	@Nullable
	private ResourceLocation lootTable;
	private long lootTableSeed;

	// what this pot is made of. Fixed by the EntityType rather than synced or saved: every pot of a
	// given type is the same material, so the client already knows it from the type alone. Its only
	// runtime job is telling the shards what colour to be — see #shatter.
	private final PotMaterial material;

	/**
	 * Material is required rather than defaulted: every pot is built through its {@code EntityType}
	 * factory (see {@code ModEntityTypes#potOf}), and a no-material overload would be an easy way to
	 * end up with a stone pot that throws terracotta shards.
	 */
	public PotEntity(EntityType<? extends PotEntity> type, Level level, PotMaterial material) {
		super(type, level);
		this.material = material;
		this.blocksBuilding = false;
	}

	public PotMaterial getMaterial() {
		return this.material;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_TUMBLED, false);
	}

	public boolean isTumbled() {
		return this.entityData.get(DATA_TUMBLED);
	}

	/**
	 * Set before the entity is added to the world (e.g. placing against a wall) to spawn it already
	 * on its side — the tip animation is skipped in that case, since it only replays for a pot that
	 * tips over during play (see {@link #getTumbleProgress}).
	 */
	public void setTumbled(boolean tumbled) {
		this.entityData.set(DATA_TUMBLED, tumbled);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_TUMBLED.equals(key) && this.isTumbled()) {
			this.tumbleStartTick = this.tickedOnce ? this.tickCount : -1;
		}
	}

	/** Eased 0..1 progress through the tip animation; 1.0 immediately if already tumbled at spawn/load. */
	public float getTumbleProgress(float partialTicks) {
		if (!this.isTumbled()) {
			return 0.0F;
		}
		if (this.tumbleStartTick < 0) {
			return 1.0F;
		}
		float t = Mth.clamp((this.tickCount - this.tumbleStartTick + partialTicks) / (float) TUMBLE_ANIM_TICKS,
				0.0F, 1.0F);
		return 1.0F - (1.0F - t) * (1.0F - t);
	}

	@Override
	public void tick() {
		super.tick();
		this.tickedOnce = true;

		if (this.level().isClientSide) {
			return;
		}

		if (this.checkCollisionEffects()) {
			return;
		}

		if (!this.onGround() && Double.isNaN(this.fallStartY)) {
			this.fallStartY = this.getY();
		}

		// Gravity must go into the deltaMovement FIELD, not a local passed to move(): move() never
		// writes its argument back to deltaMovement (it only zeroes components on collision), so
		// accumulating into a local silently discards it every tick -- giving a constant-velocity
		// fall with no acceleration. This is the vanilla FallingBlockEntity idiom.
		if (!this.onGround() && !this.isNoGravity()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D));
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
		Vec3 motion = this.getDeltaMovement();

		if (this.onGround() && !Double.isNaN(this.fallStartY)) {
			double fallDistance = this.fallStartY - this.getY();
			this.fallStartY = Double.NaN;
			if (fallDistance > FALL_BREAK_DISTANCE) {
				this.breakAndDrop(this.damageSources().fall());
				return;
			}
		}

		double drag = AIR_DRAG;
		if (this.onGround()) {
			BlockPos posBelow = this.getBlockPosBelowThatAffectsMyMovement();
			drag = this.level().getBlockState(posBelow).getBlock().getFriction() * 0.91F;
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

	// No lerpTo override: the inherited Entity#lerpTo snaps straight to the server position, which is
	// what vanilla's own falling props do. Smoothing here was actively harmful — this type syncs
	// every tick (see ModEntityTypes#updateInterval), so each packet reset the smoothing window
	// before it could finish and the render settled a fixed two ticks behind the truth. During a
	// fast fall that is most of a block, and since the pot breaks at its real position the client
	// would draw it vanishing in mid-air short of the floor. Snapping loses nothing: the renderer
	// still interpolates xOld -> x across partial ticks, so movement stays smooth.

	@Override
	public boolean isPushable() {
		return true;
	}

	/**
	 * Scans for overlapping living entities and reacts to how fast they're moving,
	 * rather than relying on vanilla's {@code Entity#push} callback (which is only
	 * invoked from the pushing entity's own AI step, on its own schedule).
	 *
	 * @return true if the pot broke this tick (caller should skip further physics)
	 */
	private boolean checkCollisionEffects() {
		List<LivingEntity> colliders = this.level().getEntitiesOfClass(LivingEntity.class,
				this.getBoundingBox().inflate(0.05D), LivingEntity::isAlive);

		for (LivingEntity collider : colliders) {
			double speed = collider.getDeltaMovement().horizontalDistance();
			if (speed >= SHATTER_SPEED) {
				// attribute the break to whoever ran into it, so the loot table sees a killer/player
				this.breakAndDrop(collider instanceof Player player
						? this.damageSources().playerAttack(player)
						: this.damageSources().mobAttack(collider));
				return true;
			} else if (speed >= TUMBLE_SPEED && !this.isTumbled()) {
				this.setTumbled(true);
			}
		}
		return false;
	}

	/**
	 * Points this individual pot at a specific loot table instead of its type's default.
	 * Call before the pot is added to the world (structure placement, worldgen).
	 *
	 * @param lootTable table id, or null to fall back to the type default
	 * @param seed      fixed roll seed, or 0 for a random roll
	 */
	public void setLootTable(@Nullable ResourceLocation lootTable, long seed) {
		this.lootTable = lootTable;
		this.lootTableSeed = seed;
	}

	/** The table this pot rolls on shatter: its override if set, else the {@link EntityType} default. */
	public ResourceLocation getLootTableId() {
		return this.lootTable != null ? this.lootTable : this.getType().getDefaultLootTable().location();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource damageSource, float amount) {
		if (this.isInvulnerableTo(damageSource) || this.isRemoved()) {
			return false;
		}
		this.breakAndDrop(damageSource);
		return true;
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		if (distance >= FALL_BREAK_DISTANCE) {
			this.breakAndDrop(source);
		}
		return false;
	}

	/**
	 * What this prop leaves behind when it breaks. A plain pot rolls its loot table; subclasses that
	 * carry something other than items — see {@code PotionEntity} — override this instead of
	 * duplicating the shard/sound/particle work in {@link #breakAndDrop}, which is identical for
	 * everything in the family. Server side only.
	 */
	protected void releasePayload(DamageSource damageSource) {
		this.dropLoot(damageSource);
	}

	private void breakAndDrop(DamageSource damageSource) {
		if (this.isRemoved()) {
			return;
		}

		Level level = this.level();
		if (!level.isClientSide) {
			this.releasePayload(damageSource);

			// non-explosive shrapnel burst: shards spray outward in every direction
			// (dome-biased upward) rather than just tumbling out, no blast/damage/block breakage.
			int shardCount = MIN_SHARDS + this.random.nextInt(MAX_SHARDS - MIN_SHARDS + 1);
			for (int i = 0; i < shardCount; i++) {
				PotShardEntity shard = new PotShardEntity(level, this.getX(), this.getY() + this.getBbHeight() * 0.5D,
						this.getZ(), this.material);

				// GMM's Bloater flings its cosmetic arm shrapnel at ~0.28 blocks/tick total
				// speed ("low velocity -- they don't travel far") -- matching that scale here.
				double speed = 0.1D + this.random.nextDouble() * 0.12D;
				double yawAngle = this.random.nextDouble() * Math.PI * 2.0D;
				double pitchAngle = this.random.nextDouble() * (Math.PI / 4.0D);
				double horizontalSpeed = Math.cos(pitchAngle) * speed;

				shard.setDeltaMovement(
						Math.cos(yawAngle) * horizontalSpeed,
						Math.sin(pitchAngle) * speed + 0.02D,
						Math.sin(yawAngle) * horizontalSpeed);
				level.addFreshEntity(shard);
			}

			level.playSound(null, this.blockPosition(), SoundEvents.DECORATED_POT_SHATTER,
					SoundSource.BLOCKS, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);

			if (level instanceof ServerLevel serverLevel) {
				// a scaled-down POOF — see PotDustParticle. The low speed here is deliberate:
				// the particle adds its own +/-0.05 jitter on top.
				serverLevel.sendParticles(ModParticles.POT_DUST_PARTICLE.get(),
						this.getX(), this.getY() + this.getBbHeight() * 0.5D, this.getZ(),
						10, 0.15D, 0.15D, 0.15D, 0.02D);
			}
		}

		this.discard();
	}

	/**
	 * Rolls {@link #getLootTableId()} and spills the result on the ground. Uses the vanilla
	 * {@code ENTITY} param set, whose parameters are a superset of the {@code CHEST} set — so an
	 * override pointing at a plain chest-style dungeon table resolves here too.
	 */
	private void dropLoot(DamageSource damageSource) {
		// a creative-mode builder clearing props shouldn't carpet the floor with shards —
		// same exemption vanilla hanging entities make. The shatter effects still play.
		if (damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild) {
			return;
		}

		ResourceLocation tableId = this.getLootTableId();
		if (tableId == null || BuiltInLootTables.EMPTY.location().equals(tableId)
				|| !(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, tableId));
		LootParams.Builder builder = new LootParams.Builder(serverLevel)
				.withParameter(LootContextParams.ORIGIN, this.position())
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
				.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
				.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());

		if (damageSource.getEntity() instanceof Player player) {
			builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
					.withLuck(player.getLuck());
		}

		LootParams params = builder.create(LootContextParamSets.ENTITY);
		List<ItemStack> loot = this.lootTableSeed == 0L
				? table.getRandomItems(params)
				: table.getRandomItems(params, this.lootTableSeed);
		loot.forEach(this::spawnAtLocation);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.setTumbled(compound.getBoolean("Tumbled"));
		this.lootTable = compound.contains("LootTable", Tag.TAG_STRING)
				? ResourceLocation.parse(compound.getString("LootTable"))
				: null;
		this.lootTableSeed = compound.getLong("LootTableSeed");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putBoolean("Tumbled", this.isTumbled());
		if (this.lootTable != null) {
			compound.putString("LootTable", this.lootTable.toString());
			if (this.lootTableSeed != 0L) {
				compound.putLong("LootTableSeed", this.lootTableSeed);
			}
		}
	}
}
