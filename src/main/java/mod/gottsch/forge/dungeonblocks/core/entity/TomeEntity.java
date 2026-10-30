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
import mod.gottsch.forge.dungeonblocks.core.item.TomeItem;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

/**
 * A tome lying on a table, a shelf or the floor. An entity rather than a block so it can lie
 * anywhere on a surface and several can share one shelf. Right-click opens and closes it; open, it
 * turns a page now and then and draws enchanting glyphs out of the air, as the enchanting table's
 * book does. Hitting it picks it up.
 *
 * <p>Tomes pile: they are solid to one another, so one laid on another rests on it (a tome in hand
 * used on a closed one lays it there), and falls when what it lies on goes. One laid down while
 * sneaking stands upright on its foot instead, spine out, as on a shelf; an upright tome stays
 * shut and takes nothing on top.
 *
 * <p>Only the variant and OPEN are synced. The opening, the page turns and the glyphs are each
 * client's own, as the enchanting table's are, so no two tomes turn their pages together.
 */
public class TomeEntity extends Entity {
	private static final EntityDataAccessor<String> DATA_VARIANT =
			SynchedEntityData.defineId(TomeEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Boolean> DATA_OPEN =
			SynchedEntityData.defineId(TomeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_UPRIGHT =
			SynchedEntityData.defineId(TomeEntity.class, EntityDataSerializers.BOOLEAN);

	private static final double GRAVITY = 0.04D;
	/** How far the cover swings per tick: the enchanting table's book's rate, open in ten ticks. */
	private static final float OPEN_SPEED = 0.1F;
	/** Ticks one page takes to turn, and the lag before a second page follows it. */
	private static final int FLIP_TICKS = 10;
	private static final int SECOND_PAGE_LAG = 4;
	/** On average, ticks between page turns while open. */
	private static final int FLIP_CHANCE = 80;
	/** Texels to blocks. */
	private static final float TEXEL = 1.0F / 32.0F;

	// client side only: the animation
	private float openness;
	private float oOpenness;
	private boolean animationStarted;
	private int flipStart = -1000;

	public TomeEntity(EntityType<? extends TomeEntity> type, Level level) {
		super(type, level);
		this.blocksBuilding = false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_VARIANT, TomeVariant.OLD_BINDER.id());
		builder.define(DATA_OPEN, false);
		builder.define(DATA_UPRIGHT, false);
	}

	public TomeVariant getVariant() {
		return TomeVariant.byId(this.entityData.get(DATA_VARIANT));
	}

	public void setVariant(TomeVariant variant) {
		this.entityData.set(DATA_VARIANT, variant.id());
	}

	public boolean isOpen() {
		return this.entityData.get(DATA_OPEN);
	}

	public void setOpen(boolean open) {
		this.entityData.set(DATA_OPEN, open);
	}

	public boolean isUpright() {
		return this.entityData.get(DATA_UPRIGHT);
	}

	/** Stands it on its foot. Set before it is added to the world; an upright tome stays shut. */
	public void setUpright(boolean upright) {
		this.entityData.set(DATA_UPRIGHT, upright);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_UPRIGHT.equals(key) || DATA_VARIANT.equals(key)) {
			this.refreshDimensions();
		}
	}

	/** The closed book, lying or standing on its foot. */
	@Override
	public EntityDimensions getDimensions(Pose pose) {
		TomeVariant.Shape shape = this.getVariant().shape();
		return this.isUpright()
				? EntityDimensions.scalable(Math.max(shape.width, shape.thickness()) * TEXEL, shape.height * TEXEL)
				: EntityDimensions.scalable(Math.max(shape.width, shape.height) * TEXEL, shape.thickness() * TEXEL);
	}

	/** Other tomes lie on it; so can anything else, but it is only 3 px high. */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	/** Whether another tome lies on this one. */
	private boolean isCovered() {
		AABB box = this.getBoundingBox();
		return !this.level().getEntitiesOfClass(TomeEntity.class,
				new AABB(box.minX, box.maxY, box.minZ, box.maxX, box.maxY + 0.05D, box.maxZ), tome -> tome != this).isEmpty();
	}

	/** 0 closed, 1 lying open. */
	public float getOpenness(float partialTicks) {
		return Mth.lerp(partialTicks, this.oOpenness, this.openness);
	}

	/**
	 * How far page 0 or 1 is through its turn, 0 to 1, or -1 while it lies still: a page at rest is
	 * not drawn, it would only lie on the pages below it.
	 */
	public float getPageTurn(int page, float partialTicks) {
		float t = (this.tickCount + partialTicks - this.flipStart - page * SECOND_PAGE_LAG) / FLIP_TICKS;
		return t > 0.0F && t < 1.0F ? t : -1.0F;
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
			// one already open when it comes into view is simply open, not opening
			this.animationStarted = true;
			this.openness = this.oOpenness = this.isOpen() ? 1.0F : 0.0F;
		}
		this.oOpenness = this.openness;
		this.openness = Mth.clamp(this.openness + (this.isOpen() ? OPEN_SPEED : -OPEN_SPEED), 0.0F, 1.0F);
		if (this.openness < 1.0F || this.isUpright()) {
			return;
		}
		boolean turning = this.tickCount - this.flipStart < FLIP_TICKS + SECOND_PAGE_LAG;
		if (!turning && this.random.nextInt(FLIP_CHANCE) == 0) {
			this.flipStart = this.tickCount;
			this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BOOK_PAGE_TURN,
					SoundSource.BLOCKS, 0.4F, 0.9F + this.random.nextFloat() * 0.2F, false);
		}
		if (this.random.nextInt(6) == 0) {
			// an enchant glyph runs from (start + velocity) back to start, dropping 1.2 blocks on
			// the way: started 1.2 up with a velocity of -1.2 it leaves the pages, rises about half
			// a block and settles back into them
			double top = this.getY() + (TomeVariant.Shape.LID + this.getVariant().shape().pages) * TEXEL;
			this.level().addParticle(ParticleTypes.ENCHANT,
					this.getX(), top + 1.2D, this.getZ(),
					(this.random.nextFloat() - 0.5D) * 0.5D, -1.2D, (this.random.nextFloat() - 0.5D) * 0.5D);
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (held.getItem() instanceof TomeItem tome && !this.isOpen() && !this.isUpright()) {
			// laid on top, turned a little, as a pile of books lies
			float yaw = this.getYRot() + (this.random.nextFloat() - 0.5F) * 30.0F;
			return tome.lay(this.level(), this.position().add(0.0D, this.getBbHeight(), 0.0D), yaw, false, player, held);
		}
		if (this.isUpright() || (!this.isOpen() && this.isCovered())) {
			return InteractionResult.PASS;
		}
		if (!this.level().isClientSide) {
			this.setOpen(!this.isOpen());
			this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
					this.isOpen() ? SoundEvents.BOOK_PAGE_TURN : SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
			this.gameEvent(GameEvent.BLOCK_CHANGE, player);
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	@Override
	public boolean isPickable() {
		return true;
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
		this.playSound(SoundEvents.BOOK_PUT, 1.0F, 1.0F);
		this.gameEvent(GameEvent.ENTITY_DIE, source.getEntity());
		this.discard();
		return true;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(ModItems.TOMES.get(this.getVariant()).get());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.setVariant(TomeVariant.byId(compound.getString("Variant")));
		this.setOpen(compound.getBoolean("Open"));
		this.setUpright(compound.getBoolean("Upright"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("Variant", this.getVariant().id());
		compound.putBoolean("Open", this.isOpen());
		compound.putBoolean("Upright", this.isUpright());
	}
}
