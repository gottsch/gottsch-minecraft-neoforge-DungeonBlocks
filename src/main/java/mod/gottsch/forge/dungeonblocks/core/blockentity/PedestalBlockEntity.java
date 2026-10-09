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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * What stands on a pedestal: one stack, shown on its top by PedestalRenderer. It does not tick.
 *
 * <p>A structure can stock one without knowing the item, as it stocks a chest: {@code LootTable}
 * (and an optional {@code LootTableSeed}) is rolled once, as chest loot, the first time the
 * pedestal is loaded into a world, and the first stack it gives stands on the pedestal - so a
 * pedestal's table should yield one. {@code Item} sets the stack outright instead.
 *
 * <p>{@code Key}, an item id, makes it a puzzle piece: its comparator output is 15 only while that
 * item stands on it, rather than while anything does. Put a relic there to open a door.
 */
public class PedestalBlockEntity extends BlockEntity implements Clearable {
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    @Nullable
    private ResourceLocation key;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.PEDESTAL.get(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    /** Puts a stack on the pedestal, or with EMPTY takes it off; clients and comparators hear of it. */
    public void setItem(ItemStack stack) {
        item = stack;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    /** 15 while an item stands here - or, with a Key set, only while the key does. */
    public int signal() {
        if (item.isEmpty()) {
            return 0;
        }
        return key == null || key.equals(BuiltInRegistries.ITEM.getKey(item.getItem())) ? 15 : 0;
    }

    public boolean hasLoot() {
        return lootTable != null;
    }

    /** Rolls the loot table and stands its first stack on the pedestal. Server side, once. */
    public void unpackLoot(ServerLevel level) {
        if (lootTable == null) {
            return;
        }
        ResourceLocation id = lootTable;
        lootTable = null;
        if (!item.isEmpty() || BuiltInLootTables.EMPTY.location().equals(id)) {
            setChanged();
            return;
        }
        LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);
        List<ItemStack> loot = lootTableSeed == 0L ? table.getRandomItems(params) : table.getRandomItems(params, lootTableSeed);
        setItem(loot.stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // rolled a tick later, by the block: rolling loot mid-load is too early to change the world
        if (level != null && !level.isClientSide && lootTable != null) {
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public void clearContent() {
        item = ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        item = tag.contains("Item", Tag.TAG_COMPOUND) ? ItemStack.parseOptional(registries, tag.getCompound("Item")) : ItemStack.EMPTY;
        lootTable = tag.contains("LootTable", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("LootTable")) : null;
        lootTableSeed = tag.getLong("LootTableSeed");
        key = tag.contains("Key", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("Key")) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!item.isEmpty()) {
            tag.put("Item", item.saveOptional(registries));
        }
        if (lootTable != null) {
            tag.putString("LootTable", lootTable.toString());
            if (lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", lootTableSeed);
            }
        }
        if (key != null) {
            tag.putString("Key", key.toString());
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** The client needs only the item, and must be told when it is gone. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Item", item.saveOptional(registries));
        return tag;
    }

    /** The item floats above the pedestal's top. */
    // read by the renderer: NeoForge 21.1 moved the render bounds to BlockEntityRenderer
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, 0.75, 0);
    }
}
