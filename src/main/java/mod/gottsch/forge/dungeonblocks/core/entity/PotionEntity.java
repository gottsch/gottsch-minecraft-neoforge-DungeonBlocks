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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * A breakable glass potion prop. Physically it is a {@link PotEntity} in every respect — gravity,
 * tipping over, shattering on a hard enough hit or fall — and differs only in what breaking it
 * leaves behind: instead of rolling a loot table it releases a lingering {@link AreaEffectCloud},
 * the way a vanilla lingering potion does when it lands.
 *
 * <p>The effects are a <b>per-instance</b> property, exactly like {@code PotEntity}'s
 * {@code LootTable} override. They are a vanilla {@code potion_contents} component, the same form a
 * 1.21 potion item carries ({@code potion} for a registry id, {@code custom_effects} for a
 * hand-authored list), stored under {@code Effects.potion_contents}, so {@code /data} and existing
 * tooling work on them unchanged.
 *
 * <p><b>A potion with nothing set releases nothing</b> — it just shatters. The appearance and the
 * effect are deliberately unrelated: a red potion is not implicitly healing, and any potion model
 * can be given any effect by whatever places it.
 *
 * @author Mark Gottschling on Aug 10, 2026
 */
public class PotionEntity extends PotEntity {

	// vanilla's own lingering-potion numbers (ThrownPotion#makeAreaOfEffectCloud), so a shattered
	// prop behaves like a lingering potion a player threw at the same spot.
	private static final float CLOUD_RADIUS = 3.0F;
	private static final float CLOUD_RADIUS_ON_USE = -0.5F;
	private static final int CLOUD_WAIT_TIME = 10;

	/**
	 * The tag holding this potion's {@code potion_contents}, or null for "no
	 * effect". Kept as the raw tag rather than a resolved effect list so an unknown or datapack-added
	 * potion id round-trips through save/load untouched instead of being silently dropped.
	 */
	private CompoundTag effectsTag;

	public PotionEntity(EntityType<? extends PotEntity> type, Level level, PotMaterial material) {
		super(type, level, material);
	}

	/**
	 * Points this individual potion at a set of effects. Call before it is added to the world
	 * (structure placement, worldgen).
	 *
	 * @param effectsTag a tag carrying a {@code potion_contents} component, or null to
	 *                   make this potion inert
	 */
	public void setEffectsTag(CompoundTag effectsTag) {
		this.effectsTag = effectsTag == null ? null : effectsTag.copy();
	}

	/** The effects this potion would release, empty if it is inert. */
	public List<MobEffectInstance> getEffects() {
		List<MobEffectInstance> effects = new ArrayList<>();
		getContents().getAllEffects().forEach(effects::add);
		return effects;
	}

	/** The potion contents, decoded from {@link #effectsTag}; empty if inert or unreadable. */
	public PotionContents getContents() {
		if (this.effectsTag == null || !this.effectsTag.contains("potion_contents")) {
			return PotionContents.EMPTY;
		}
		return PotionContents.CODEC
				.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.effectsTag.get("potion_contents"))
				.resultOrPartial(error -> DungeonBlocks.LOGGER.warn("potion at {} has unreadable potion_contents: {}", this.blockPosition(), error))
				.orElse(PotionContents.EMPTY);
	}

	/**
	 * Releases a lingering cloud instead of loot. The shard burst, shatter sound and dust still play
	 * — those come from {@link PotEntity}'s break path and are what sells the break either way, so an
	 * inert potion still looks like it broke properly rather than quietly vanishing.
	 */
	@Override
	protected void releasePayload(DamageSource damageSource) {
		PotionContents contents = this.getContents();
		if (!contents.hasEffects()) {
			return;
		}

		AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
		cloud.setRadius(CLOUD_RADIUS);
		cloud.setRadiusOnUse(CLOUD_RADIUS_ON_USE);
		cloud.setWaitTime(CLOUD_WAIT_TIME);
		// the cloud's reapplication delay stays at its default 20 ticks — the value a vanilla lingering
		// potion uses.
		// shrink to nothing exactly as the cloud times out, so it never pops out of existence at
		// full size. Must come after setRadius — it is computed from the radius set above.
		cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());

		// the cloud takes its colour from what it actually carries rather than from the potion's own
		// texture: the model and the effect are unrelated here, and a mismatch would misinform the player.
		cloud.setPotionContents(contents);

		this.level().addFreshEntity(cloud);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.effectsTag = compound.contains("Effects", CompoundTag.TAG_COMPOUND)
				? compound.getCompound("Effects").copy()
				: null;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (this.effectsTag != null) {
			compound.put("Effects", this.effectsTag.copy());
		}
	}
}
