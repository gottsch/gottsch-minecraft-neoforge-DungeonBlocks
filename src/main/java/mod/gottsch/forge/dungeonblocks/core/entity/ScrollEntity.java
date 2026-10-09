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
import mod.gottsch.forge.dungeonblocks.core.item.ScrollItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

/**
 * A scroll lying on a table, a shelf or the floor: rolled up, tied and sealed, until a right-click
 * unrolls it flat to show what is written on it, and another rolls it back up. An entity, as the
 * tome is, so it lies anywhere on a surface; rolled scrolls pile, one used on another lying on top.
 * Open, a rune scroll gives off the odd enchanting glyph. Hitting one picks it up.
 *
 * <p>Only the variant and OPEN are synced; the unrolling is each client's own animation.
 */
public class ScrollEntity extends Entity {
	private static final EntityDataAccessor<String> DATA_VARIANT =
			SynchedEntityData.defineId(ScrollEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Boolean> DATA_OPEN =
			SynchedEntityData.defineId(ScrollEntity.class, EntityDataSerializers.BOOLEAN);

	private static final double GRAVITY = 0.04D;
	/** Unrolled in about half a second. */
	private static final float OPEN_SPEED = 0.1F;
	/** On average, ticks between glyphs over an open rune scroll: a tome gives one every six. */
	private static final int GLYPH_CHANCE = 20;

	// client side only: the animation
	private float openness;
	private float oOpenness;
	private boolean animationStarted;

	public ScrollEntity(EntityType<? extends ScrollEntity> type, Level level) {
		super(type, level);
		this.blocksBuilding = false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_VARIANT, ScrollVariant.PLAIN.id());
		builder.define(DATA_OPEN, false);
	}

	public ScrollVariant getVariant() {
		return ScrollVariant.byId(this.entityData.get(DATA_VARIANT));
	}

	public void setVariant(ScrollVariant variant) {
		this.entityData.set(DATA_VARIANT, variant.id());
	}

	public boolean isOpen() {
		return this.entityData.get(DATA_OPEN);
	}

	public void setOpen(boolean open) {
		this.entityData.set(DATA_OPEN, open);
	}

	/** 0 rolled up, 1 lying open. */
	public float getOpenness(float partialTicks) {
		return Mth.lerp(partialTicks, this.oOpenness, this.openness);
	}

	/** Other props lie on it; so can anything else, but it is only a roll of paper. */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	/** Whether another scroll lies on this one. */
	private boolean isCovered() {
		AABB box = this.getBoundingBox();
		return !this.level().getEntitiesOfClass(ScrollEntity.class,
				new AABB(box.minX, box.maxY, box.minZ, box.maxX, box.maxY + 0.05D, box.maxZ), scroll -> scroll != this).isEmpty();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			this.animate();
			return;
		}
		// gravity goes into the deltaMovement field: move() never writes its argument back
		if (!this.onGround() && !this.isNoGravity()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D));
		}
		this.move(MoverType.SELF, this.getDeltaMovement());
		this.setDeltaMovement(this.onGround() ? this.getDeltaMovement().multiply(0.0D, 0.0D, 0.0D)
				: this.getDeltaMovement().multiply(0.9D, 0.98D, 0.9D));
	}

	private void animate() {
		if (!this.animationStarted) {
			// one already open when it comes into view is simply open, not unrolling
			this.animationStarted = true;
			this.openness = this.oOpenness = this.isOpen() ? 1.0F : 0.0F;
		}
		this.oOpenness = this.openness;
		this.openness = Mth.clamp(this.openness + (this.isOpen() ? OPEN_SPEED : -OPEN_SPEED), 0.0F, 1.0F);
		if (this.openness >= 1.0F && this.getVariant().isRune() && this.random.nextInt(GLYPH_CHANCE) == 0) {
			// as the tome's: an enchant glyph runs from (start + velocity) back to start, rising
			// about half a block from the sheet and settling back into it
			this.level().addParticle(ParticleTypes.ENCHANT,
					this.getX(), this.getY() + 1.2D, this.getZ(),
					(this.random.nextFloat() - 0.5D) * 0.4D, -1.2D, (this.random.nextFloat() - 0.5D) * 0.4D);
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (held.getItem() instanceof ScrollItem scroll && !this.isOpen()) {
			// laid on top, turned a little, as a pile of scrolls lies
			float yaw = this.getYRot() + (this.random.nextFloat() - 0.5F) * 40.0F;
			return scroll.lay(this.level(), this.position().add(0.0D, this.getBbHeight(), 0.0D), yaw, player, held);
		}
		if (!this.isOpen() && this.isCovered()) {
			return InteractionResult.PASS;
		}
		if (!this.level().isClientSide) {
			this.setOpen(!this.isOpen());
			this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BOOK_PAGE_TURN,
					SoundSource.BLOCKS, 1.0F, this.isOpen() ? 1.2F : 0.9F);
			this.gameEvent(GameEvent.BLOCK_CHANGE, player);
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	/** Any hit picks it up: it drops as its item, except to a player in creative. */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source) || this.isRemoved() || this.level().isClientSide) {
			return false;
		}
		boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
		if (!creative) {
			this.spawnAtLocation(this.getPickResult());
		}
		this.playSound(SoundEvents.BOOK_PUT, 1.0F, 1.3F);
		this.gameEvent(GameEvent.ENTITY_DIE, source.getEntity());
		this.discard();
		return true;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(ModItems.SCROLLS.get(this.getVariant()).get());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.setVariant(ScrollVariant.byId(compound.getString("Variant")));
		this.setOpen(compound.getBoolean("Open"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("Variant", this.getVariant().id());
		compound.putBoolean("Open", this.isOpen());
	}
}
