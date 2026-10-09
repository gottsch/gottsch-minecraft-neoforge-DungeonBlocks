/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.datagen.loot;

import mod.gottsch.forge.dungeonblocks.core.block.TallPropBlock;
import mod.gottsch.forge.dungeonblocks.core.block.DungeonBannerBlock;
import mod.gottsch.forge.dungeonblocks.core.block.GibbetBlock;
import mod.gottsch.forge.dungeonblocks.core.block.TortureRackBlock;
import mod.gottsch.forge.dungeonblocks.core.block.IronMaidenBlock;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import mod.gottsch.forge.dungeonblocks.core.block.RubbleScatterBlock;
import mod.gottsch.forge.dungeonblocks.core.block.SkeletonBlock;
import mod.gottsch.forge.dungeonblocks.core.block.SlabTableBlock;
import mod.gottsch.forge.dungeonblocks.core.block.TapestryBlock;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Stream;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        // every registered block-item drops itself. ModBlocks.MAP already excludes
        // blocks that are handled specially (mold, lichen, skeleton).
        ModBlocks.MAP.keySet().forEach(block -> {
            Block b = block.get();
            if (b instanceof SlabTableBlock) {
                // two blocks, one item: only the HEAD half carries the drop, exactly as vanilla beds
                // do. Breaking either half destroys the other, and the destroyed FOOT fails this
                // condition, so the pair yields exactly one table whichever end is broken.
                add(b, createSinglePropConditionTable(b, SlabTableBlock.PART, BedPart.HEAD));
            } else if (b instanceof DungeonBannerBlock) {
                // two blocks, one item. The upper half is the one that carries the drop; breaking the
                // lower half destroys the upper through updateShape, which drops - so either half
                // yields exactly one banner, and neither yields two.
                add(b, createSinglePropConditionTable(b, DungeonBannerBlock.HALF, DoubleBlockHalf.UPPER));
            } else if (b instanceof IronMaidenBlock) {
                // two halves, one item, the same shape as a door: the lower half carries the drop
                add(b, createSinglePropConditionTable(b, IronMaidenBlock.HALF, DoubleBlockHalf.LOWER));
            } else if (b instanceof TallPropBlock) {
                // two halves, one item, as the iron maiden: the lower half carries the drop
                add(b, createSinglePropConditionTable(b, TallPropBlock.HALF, DoubleBlockHalf.LOWER));
            } else if (b instanceof TortureRackBlock) {
                // three parts, one item, as the gibbet: the foot carries the drop
                add(b, createSinglePropConditionTable(b, TortureRackBlock.PART, TortureRackBlock.Part.FOOT));
            } else if (b instanceof GibbetBlock) {
                // three parts, one item: the bottom part carries the drop, and breaking any part
                // destroys the rest, so a gibbet yields exactly one whichever part is broken
                add(b, createSinglePropConditionTable(b, GibbetBlock.PART, GibbetBlock.Part.BOTTOM));
            } else if (b instanceof DoorBlock) {
                // vanilla door table: only the LOWER half drops. Breaking either half destroys the
                // other through updateShape, which drops too - the same mechanism as the banner
                // above - so the blanket dropSelf below gave two doors for every one broken.
                add(b, createDoorTable(b));
            } else if (b instanceof TapestryBlock) {
                // twelve parts, one item: the bottom-left part carries the drop, and losing any part
                // takes down the rest, so a tapestry yields exactly one whichever part is broken
                add(b, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(b).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(TapestryBlock.COLUMN, 0).hasProperty(TapestryBlock.ROW, 0))))
                        .when(ExplosionCondition.survivesExplosion())));
            } else if (b instanceof RubbleScatterBlock) {
                // one for each stage laid, as vanilla's pink petals give back one per petal
                add(b, createStagedDrops(b, RubbleScatterBlock.CHIPS));
            } else if (b instanceof SlabBlock) {
                // vanilla slab table: one item, or two when broken as a double slab. The blanket
                // dropSelf below would give one either way, losing an item on every double slab.
                add(b, createSlabItemTable(b));
            } else {
                dropSelf(b);
            }
        });

        // The skeleton is not in ModBlocks.MAP (its BlockItem is registered by hand as
        // ModItems.SKELETON), so the sweep above never reached it and it had no table at all -
        // placing one was a one-way trip. Same two-blocks-one-item shape as the slab table.
        Block skeleton = ModBlocks.SKELETON.get();
        add(skeleton, createSinglePropConditionTable(skeleton, SkeletonBlock.PART,
                SkeletonBlock.EnumPartType.BOTTOM));
    }

    /**
     * The block itself, as many as the stage `stages` names - vanilla's createPetalsDrops, which is
     * written against pink petals' own property, for any stage property counted from one.
     */
    private LootTable.Builder createStagedDrops(Block block, IntegerProperty stages) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(applyExplosionDecay(block, LootItem.lootTableItem(block).apply(stages.getPossibleValues(),
                        n -> SetItemCountFunction.setCount(ConstantValue.exactly(n))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(stages, n)))))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // must match exactly the set of blocks handled in generate()
        return Stream.concat(
                ModBlocks.MAP.keySet().stream().map(h -> (Block) h.get()),
                Stream.of((Block) ModBlocks.SKELETON.get()))::iterator;
    }
}