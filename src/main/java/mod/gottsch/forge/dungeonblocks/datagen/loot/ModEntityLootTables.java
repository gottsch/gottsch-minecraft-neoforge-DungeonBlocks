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
package mod.gottsch.forge.dungeonblocks.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import mod.gottsch.forge.dungeonblocks.core.entity.ModEntityTypes;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

/**
 * Default loot for the decorative prop entities.
 *
 * <p>Deliberately <b>empty</b> except for the cinerary urn: a plain decorative pot shattering should leave nothing behind, and
 * the cosmetic {@code PotShardEntity} debris already sells the break visually. These tables exist
 * so the drop path is wired and datapack-overridable, not to hand out items.
 *
 * <p>Real loot comes from individual pots carrying a {@code LootTable} NBT override (see
 * {@code PotEntity#setLootTable}), which lets a structure point one particular pot at a proper
 * dungeon table without touching these defaults.
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class ModEntityLootTables implements LootTableSubProvider {

	public ModEntityLootTables(HolderLookup.Provider registries) {
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
		noDrops(consumer, ModEntityTypes.POT.get());
		noDrops(consumer, ModEntityTypes.SQUAT_CLAY_POT.get());
		noDrops(consumer, ModEntityTypes.THIN_CLAY_POT.get());
		noDrops(consumer, ModEntityTypes.STONE_POT.get());
		noDrops(consumer, ModEntityTypes.SQUAT_STONE_POT.get());
		noDrops(consumer, ModEntityTypes.THIN_STONE_POT.get());
		noDrops(consumer, ModEntityTypes.RED_POT.get());
		noDrops(consumer, ModEntityTypes.SQUAT_RED_POT.get());
		noDrops(consumer, ModEntityTypes.THIN_RED_POT.get());
		noDrops(consumer, ModEntityTypes.BLUE_POT.get());
		noDrops(consumer, ModEntityTypes.SQUAT_BLUE_POT.get());
		noDrops(consumer, ModEntityTypes.THIN_BLUE_POT.get());
		cineraryUrn(consumer);
	}

	/**
	 * The one prop with loot of its own: what was buried with the dead. Always some bone meal for the
	 * ashes, and usually a little gold. A structure can still override it per urn, as for any pot.
	 */
	private void cineraryUrn(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
		consumer.accept(ModEntityTypes.CINERARY_URN.get().getDefaultLootTable(), LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(Items.BONE_MEAL)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))))
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.when(LootItemRandomChanceCondition.randomChance(0.6F))
						.add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(8)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
						.add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))));
	}

	/** A pool-less table: valid, resolvable, and yields nothing. */
	private void noDrops(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, EntityType<?> type) {
		consumer.accept(type.getDefaultLootTable(), LootTable.lootTable());
	}
}
