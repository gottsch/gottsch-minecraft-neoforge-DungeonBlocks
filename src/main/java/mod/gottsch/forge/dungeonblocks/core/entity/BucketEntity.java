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

import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * A wooden bucket: a pot-family prop that never breaks. It has the pots' gravity, pushing and
 * settling, and a knock tips it over as it does a pot, but a hard knock or a long fall only tips it
 * too - iron-bound timber does not shatter.
 *
 * <p>It holds water or nothing ({@link #isFilled()}, synced, saved as {@code Filled}). A bucket on
 * its side is always empty: tipping one over spills it - a splash and the vanilla bucket-emptying
 * sound - and one placed or loaded on its side starts empty. Standing up, it can be either:
 * <ul>
 * <li>an empty hand tips the water out of a full one; in creative it also fills an empty one, so a
 * builder can set either without a water bucket;</li>
 * <li>a vanilla water bucket fills an empty one and is left empty, and an empty vanilla bucket
 * scoops a full one out and comes away full, as they do with a cauldron.</li>
 * </ul>
 * A structure places an empty upright bucket with {@code Filled:0b}; without the key it stands full.
 *
 * <p>An attack tips an upright bucket. A player hitting one already on its side picks it up - it
 * drops as its item, as a painting does - so a survival player can clear one out of the way;
 * anything else hitting it does nothing more. A creative player removes it outright.
 *
 * <p>It carries no loot: {@link #releasePayload} is only reached through the break path, which a
 * bucket never takes.
 *
 * @author Mark Gottschling on 10/25/2026
 */
public class BucketEntity extends PotEntity {

	private static final EntityDataAccessor<Boolean> DATA_FILLED =
			SynchedEntityData.defineId(BucketEntity.class, EntityDataSerializers.BOOLEAN);

	public BucketEntity(EntityType<? extends PotEntity> type, Level level) {
		// the material only colours shatter debris, and a bucket never shatters
		super(type, level, PotMaterial.STONE);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_FILLED, true);
	}

	/** Whether it holds water. Never true while it lies on its side. */
	public boolean isFilled() {
		return this.entityData.get(DATA_FILLED);
	}

	public void setFilled(boolean filled) {
		this.entityData.set(DATA_FILLED, filled && !this.isTumbled());
	}

	/** On its side means empty, however it got there. */
	@Override
	public void setTumbled(boolean tumbled) {
		super.setTumbled(tumbled);
		if (tumbled) {
			this.entityData.set(DATA_FILLED, false);
		}
	}

	@Override
	protected boolean isBreakable() {
		return false;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (this.isTumbled()) {
			return InteractionResult.PASS;
		}
		ItemStack held = player.getItemInHand(hand);
		boolean creative = player.getAbilities().instabuild;

		if (held.isEmpty()) {
			if (this.isFilled()) {
				this.empty();
			} else if (creative) {
				this.fill();
			} else {
				return InteractionResult.PASS;
			}
		} else if (held.is(Items.WATER_BUCKET) && !this.isFilled()) {
			this.fill();
			player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, new ItemStack(Items.BUCKET)));
		} else if (held.is(Items.BUCKET) && this.isFilled()) {
			this.setFilledWithSound(false, SoundEvents.BUCKET_FILL);
			player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, new ItemStack(Items.WATER_BUCKET)));
		} else {
			return InteractionResult.PASS;
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	@Override
	public boolean hurt(DamageSource damageSource, float amount) {
		if (this.isInvulnerableTo(damageSource) || this.isRemoved()) {
			return false;
		}
		if (this.level().isClientSide) {
			return true;
		}
		if (damageSource.getEntity() instanceof Player player) {
			if (player.getAbilities().instabuild) {
				this.discard();
				return true;
			}
			if (this.isTumbled()) {
				this.spawnAtLocation(new ItemStack(ModItems.WOODEN_BUCKET.get()));
				this.discard();
				return true;
			}
		}
		if (!this.isTumbled()) {
			this.tipOver();
		}
		return true;
	}

	/** Spills as it goes over, if there was anything in it. */
	@Override
	protected void tipOver() {
		boolean spills = this.isFilled();
		super.tipOver();
		if (spills) {
			this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS,
					1.0F, 0.9F + this.random.nextFloat() * 0.2F);
			this.splash();
		}
	}

	private void empty() {
		this.setFilledWithSound(false, SoundEvents.BUCKET_EMPTY);
		this.splash();
	}

	private void fill() {
		this.setFilledWithSound(true, SoundEvents.BUCKET_EMPTY);
	}

	/** Server side only; the client sees the change through the synced flag. */
	private void setFilledWithSound(boolean filled, SoundEvent sound) {
		if (this.level().isClientSide) {
			return;
		}
		this.setFilled(filled);
		this.level().playSound(null, this.blockPosition(), sound, SoundSource.BLOCKS,
				1.0F, 0.9F + this.random.nextFloat() * 0.2F);
	}

	private void splash() {
		if (this.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.SPLASH,
					this.getX(), this.getY() + this.getBbHeight() * 0.5D, this.getZ(),
					24, 0.3D, 0.1D, 0.3D, 0.1D);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Filled")) {
			this.setFilled(compound.getBoolean("Filled"));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Filled", this.isFilled());
	}
}
