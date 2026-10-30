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
package mod.gottsch.forge.dungeonblocks.core.blockentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

/**
 * A sarcophagus half's block entity: the lid's animation state for SarcophagusRenderer, and what the
 * tomb holds. It does not tick.
 *
 * <p>The animation fields live on the client alone. The renderer compares the block's OPEN with
 * {@link #shownOpen} each frame; when they differ, a slide starts from wherever the lid is drawn
 * now. So nothing is synced beyond the ordinary block update that carries OPEN.
 *
 * <p>THE TOMB'S CONTENTS are server-side and saved: an optional loot table and an optional guardian
 * mob, put there by a structure or a command. A player-placed sarcophagus holds nothing, so one
 * cannot be farmed by breaking and re-placing it. The first opening rolls ONCE between them - loot,
 * the guardian or nothing, never both (see {@link #rollOutcome}) - and the tomb is empty for good.
 * The NBT, on either half (a tomb is one roll, and the head half's contents win):
 * <pre>
 *   LootTable       loot table id, e.g. "minecraft:chests/simple_dungeon" - rolled as chest loot
 *   LootTableSeed   optional, as on a vanilla chest
 *   Guardian        entity id, e.g. "minecraft:zombie"
 *   LootWeight      default 1   one roll picks loot, the guardian or nothing in proportion to
 *   GuardianWeight  default 1   these. Loot and the guardian only count when set, so with both
 *   EmptyWeight     default 0   set and no weights it is an even chance of either
 * </pre>
 * For example {@code {LootTable:"minecraft:chests/simple_dungeon", Guardian:"minecraft:skeleton",
 * LootWeight:3, GuardianWeight:2, EmptyWeight:5}} is 30% loot, 20% a skeleton, 50% nothing.
 */
public class SarcophagusBlockEntity extends BlockEntity {
    /** What the first opening of a sealed tomb releases. */
    public enum Outcome { LOOT, GUARDIAN, NOTHING }

    /** Client only: the OPEN value the lid is animating toward; null until first drawn. */
    public Boolean shownOpen;
    /** Client only: where the current slide started, 0 (closed) to 1 (open). */
    public float slideFrom;
    /** Client only: game time (with partial tick) the current slide started. */
    public double moveStart = Double.NEGATIVE_INFINITY;
    /** Client only: where the lid was drawn last frame, 0 (closed) to 1 (open). */
    public float slide;

    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    @Nullable
    private ResourceLocation guardian;
    private int lootWeight = 1;
    private int guardianWeight = 1;
    private int emptyWeight = 0;

    public SarcophagusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SARCOPHAGUS.get(), pos, state);
    }

    /** Whether the tomb still holds anything for its first opening to release. */
    public boolean isSealed() {
        return lootTable != null || guardian != null;
    }

    /**
     * The one roll a sealed tomb gets. Loot and the guardian take part only when set; nothing takes
     * part with EmptyWeight. Weights below zero count as zero.
     */
    public Outcome rollOutcome(RandomSource random) {
        int loot = lootTable != null ? Math.max(0, lootWeight) : 0;
        int guard = guardian != null ? Math.max(0, guardianWeight) : 0;
        int total = loot + guard + Math.max(0, emptyWeight);
        if (total <= 0) {
            return Outcome.NOTHING;
        }
        int roll = random.nextInt(total);
        return roll < loot ? Outcome.LOOT : roll < loot + guard ? Outcome.GUARDIAN : Outcome.NOTHING;
    }

    @Nullable
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public long getLootTableSeed() {
        return lootTableSeed;
    }

    @Nullable
    public ResourceLocation getGuardian() {
        return guardian;
    }

    /** Spent: whatever the roll gave, the tomb holds nothing now, and the change is saved. */
    public void empty() {
        if (isSealed()) {
            lootTable = null;
            lootTableSeed = 0L;
            guardian = null;
            lootWeight = 1;
            guardianWeight = 1;
            emptyWeight = 0;
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lootTable = tag.contains("LootTable", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("LootTable")) : null;
        lootTableSeed = tag.getLong("LootTableSeed");
        guardian = tag.contains("Guardian", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("Guardian")) : null;
        lootWeight = tag.contains("LootWeight", Tag.TAG_ANY_NUMERIC) ? tag.getInt("LootWeight") : 1;
        guardianWeight = tag.contains("GuardianWeight", Tag.TAG_ANY_NUMERIC) ? tag.getInt("GuardianWeight") : 1;
        emptyWeight = tag.contains("EmptyWeight", Tag.TAG_ANY_NUMERIC) ? tag.getInt("EmptyWeight") : 0;
    }

    /** Only what is set is written, so an empty tomb's data stays empty. */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (lootTable != null) {
            tag.putString("LootTable", lootTable.toString());
            if (lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", lootTableSeed);
            }
        }
        if (guardian != null) {
            tag.putString("Guardian", guardian.toString());
        }
        if (isSealed()) {
            tag.putInt("LootWeight", lootWeight);
            tag.putInt("GuardianWeight", guardianWeight);
            tag.putInt("EmptyWeight", emptyWeight);
        }
    }

    /** The lid slides 10px aside, over the block's edge, so draw beyond the block's own bounds. */
    // read by the renderer: NeoForge 21.1 moved the render bounds to BlockEntityRenderer
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }
}
