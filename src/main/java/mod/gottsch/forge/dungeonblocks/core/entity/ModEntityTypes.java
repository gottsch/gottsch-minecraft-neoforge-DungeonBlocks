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

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @author Mark Gottschling on Jul 25, 2026
 */
public class ModEntityTypes {

	// every pot shares PotEntity — only geometry, texture, hitbox and material differ, so they are
	// distinct EntityTypes (separate renderer + default loot table) over one behaviour class.
	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> POT = Registration.ENTITY_TYPES.register("pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.TERRACOTTA), MobCategory.MISC)
					.sized(0.5F, 0.6F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> SQUAT_CLAY_POT = Registration.ENTITY_TYPES.register("squat_clay_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.TERRACOTTA), MobCategory.MISC)
					// 10px wide x 10px tall modeled -> 0.625 blocks square
					.sized(0.625F, 0.625F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("squat_clay_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> THIN_CLAY_POT = Registration.ENTITY_TYPES.register("thin_clay_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.TERRACOTTA), MobCategory.MISC)
					// 6px wide x 12px tall modeled
					.sized(0.375F, 0.75F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("thin_clay_pot"));

	// the stone set: same three shapes and hitboxes as the terracotta pots above, differing only in
	// texture (supplied by ClientSetup) and in the material the shatter debris takes its colour from.
	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> STONE_POT = Registration.ENTITY_TYPES.register("stone_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.STONE), MobCategory.MISC)
					.sized(0.5F, 0.6F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("stone_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> SQUAT_STONE_POT = Registration.ENTITY_TYPES.register("squat_stone_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.STONE), MobCategory.MISC)
					.sized(0.625F, 0.625F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("squat_stone_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> THIN_STONE_POT = Registration.ENTITY_TYPES.register("thin_stone_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.STONE), MobCategory.MISC)
					.sized(0.375F, 0.75F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("thin_stone_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> RED_POT = Registration.ENTITY_TYPES.register("red_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.RED), MobCategory.MISC)
					.sized(0.5F, 0.6F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("red_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> SQUAT_RED_POT = Registration.ENTITY_TYPES.register("squat_red_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.RED), MobCategory.MISC)
					.sized(0.625F, 0.625F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("squat_red_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> THIN_RED_POT = Registration.ENTITY_TYPES.register("thin_red_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.RED), MobCategory.MISC)
					.sized(0.375F, 0.75F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("thin_red_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BLUE_POT = Registration.ENTITY_TYPES.register("blue_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.BLUE), MobCategory.MISC)
					.sized(0.5F, 0.6F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("blue_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> SQUAT_BLUE_POT = Registration.ENTITY_TYPES.register("squat_blue_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.BLUE), MobCategory.MISC)
					.sized(0.625F, 0.625F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("squat_blue_pot"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> THIN_BLUE_POT = Registration.ENTITY_TYPES.register("thin_blue_pot",
			() -> EntityType.Builder.of(potOf(PotMaterial.BLUE), MobCategory.MISC)
					.sized(0.375F, 0.75F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("thin_blue_pot"));

	// A potion prop: PotEntity physics, but it releases a lingering effect cloud instead of loot.
	// Hitbox is half the tall pot's, matching the half render scale — and like the pots it covers the
	// bottle's body only, not the neck and cork above it.
	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BIG_RED_POTION = Registration.ENTITY_TYPES.register("big_red_potion",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.25F, 0.3125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("big_red_potion"));

	// hitbox covers the flask's 7x9x6 body only, halved to match the half render scale
	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> RED_FLASK = Registration.ENTITY_TYPES.register("red_flask",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.21875F, 0.28125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("red_flask"));

	// colour is purely cosmetic (the liquid tint) — every colour shares PotMaterial.GLASS, since a
	// shattered potion of any colour throws the same glass shards.
	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BIG_YELLOW_POTION = Registration.ENTITY_TYPES.register("big_yellow_potion",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.25F, 0.3125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("big_yellow_potion"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> YELLOW_FLASK = Registration.ENTITY_TYPES.register("yellow_flask",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.21875F, 0.28125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("yellow_flask"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BIG_BLUE_POTION = Registration.ENTITY_TYPES.register("big_blue_potion",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.25F, 0.3125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("big_blue_potion"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BLUE_FLASK = Registration.ENTITY_TYPES.register("blue_flask",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.21875F, 0.28125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("blue_flask"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> BIG_GREEN_POTION = Registration.ENTITY_TYPES.register("big_green_potion",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.25F, 0.3125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("big_green_potion"));

	public static final DeferredHolder<EntityType<?>, EntityType<PotEntity>> GREEN_FLASK = Registration.ENTITY_TYPES.register("green_flask",
			() -> EntityType.Builder.of(potionOf(PotMaterial.GLASS), MobCategory.MISC)
					.sized(0.21875F, 0.28125F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("green_flask"));

	/**
	 * Factory that stamps a material onto every pot of a type. Going through the {@code EntityType}
	 * factory rather than a lookup table means a pot is built with its material however it comes into
	 * existence — placed from an item, loaded from disk, or spawned by a command.
	 */
	private static EntityType.EntityFactory<PotEntity> potOf(PotMaterial material) {
		return (type, level) -> new PotEntity(type, level, material);
	}

	/** As {@link #potOf}, for the {@link PotionEntity} subclass. */
	private static EntityType.EntityFactory<PotEntity> potionOf(PotMaterial material) {
		return (type, level) -> new PotionEntity(type, level, material);
	}

	public static final DeferredHolder<EntityType<?>, EntityType<PotShardEntity>> POT_SHARD = Registration.ENTITY_TYPES.register("pot_shard",
			() -> EntityType.Builder.<PotShardEntity>of(PotShardEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(6)
					.updateInterval(1)
					.build("pot_shard"));

	// one type for every cover: the variant is synced data, and only the texture differs
	public static final DeferredHolder<EntityType<?>, EntityType<TomeEntity>> TOME = Registration.ENTITY_TYPES.register("tome",
			() -> EntityType.Builder.<TomeEntity>of(TomeEntity::new, MobCategory.MISC)
					// a standard tome lying closed; TomeEntity#getDimensions sizes each shape and stance
					.sized(0.4F, 0.1875F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("tome"));

	// one type for every design, as the tome: the variant is synced data
	public static final DeferredHolder<EntityType<?>, EntityType<ScrollEntity>> SCROLL = Registration.ENTITY_TYPES.register("scroll",
			() -> EntityType.Builder.<ScrollEntity>of(ScrollEntity::new, MobCategory.MISC)
					// the rolled scroll: 7 px long, 1.5 px thick
					.sized(0.45F, 0.1F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.build("scroll"));

	public static void register(IEventBus bus) {
		Registration.registerEntityTypes(bus);
	}
}
