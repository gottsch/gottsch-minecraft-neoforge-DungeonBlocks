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
import mod.gottsch.forge.dungeonblocks.core.block.ChainSize;
import mod.gottsch.forge.dungeonblocks.core.block.ChainAnchorBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.ChainRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The chains fixed to a chain anchor: at most one running out to another anchor, and any
 * number arriving from others. Both ends know of a chain, so either can take it down when broken,
 * and either can draw it while the other is out of the loaded world. It does not tick, and has no
 * block entity renderer: {@link ChainRenderer} draws every chain in one pass.
 *
 * <p>Each chain records its {@link ChainSize}, at both ends, so either end can draw it and give
 * back the right item.
 *
 * <p><b>Offsets, not positions, in the anchor's own frame.</b> A structure rotates and mirrors block
 * states but copies block entity data as it is, so a stored world offset would point the wrong way
 * in a turned room. Each offset is stored in the frame of the anchor's FACING and MIRRORED
 * properties, which the structure does turn, and turned to world space on reading
 * ({@link ChainAnchorBlock#toWorld}).
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class ChainAnchorBlockEntity extends BlockEntity {
    /** The chain running out of this anchor, as an offset in the anchor's frame; null for none. */
    @Nullable
    private BlockPos out;
    private ChainSize outSize = ChainSize.GIANT;
    /** The anchors whose chains end here, as offsets in this anchor's frame, with each chain's size. */
    private final Map<BlockPos, ChainSize> in = new LinkedHashMap<>();

    public ChainAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CHAIN_ANCHOR.get(), pos, state);
    }

    /** Where this anchor's own chain ends, in the world; null for none. */
    @Nullable
    public BlockPos getTarget() {
        return out == null ? null : worldPosition.offset(ChainAnchorBlock.toWorld(getBlockState(), out));
    }

    /** The size of this anchor's own chain; meaningless when it has none. */
    public ChainSize getTargetSize() {
        return outSize;
    }

    /** The anchors whose chains end here, in the world, with each chain's size. A copy. */
    public Map<BlockPos, ChainSize> getSources() {
        Map<BlockPos, ChainSize> sources = new LinkedHashMap<>();
        in.forEach((offset, size) -> sources.put(worldPosition.offset(ChainAnchorBlock.toWorld(getBlockState(), offset)), size));
        return sources;
    }

    public boolean hasChains() {
        return out != null || !in.isEmpty();
    }

    public void setTarget(@Nullable BlockPos target, ChainSize size) {
        out = target == null ? null : ChainAnchorBlock.toLocal(getBlockState(), target.subtract(worldPosition));
        outSize = size;
        changed();
    }

    public void clearTarget() {
        setTarget(null, ChainSize.GIANT);
    }

    public void addSource(BlockPos source, ChainSize size) {
        BlockPos offset = ChainAnchorBlock.toLocal(getBlockState(), source.subtract(worldPosition));
        if (in.put(offset, size) != size) {
            changed();
        }
    }

    public void removeSource(BlockPos source) {
        if (in.remove(ChainAnchorBlock.toLocal(getBlockState(), source.subtract(worldPosition))) != null) {
            changed();
        }
    }

    private void changed() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) {
            return;
        }
        if (level.isClientSide) {
            ChainRenderer.add(worldPosition);
        } else if (hasChains()) {
            // checked a tick later, by the block: the other end of a chain may have gone while this
            // one was unloaded, or a structure may have placed only half of one
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && level.isClientSide) {
            ChainRenderer.remove(worldPosition);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null && level.isClientSide) {
            ChainRenderer.remove(worldPosition);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        out = tag.contains("Out", Tag.TAG_LONG) ? BlockPos.of(tag.getLong("Out")) : null;
        // no size saved: a chain from before medium chains, so giant
        outSize = ChainSize.byId(tag.getByte("OutSize"));
        in.clear();
        ListTag offsets = tag.getList("In", Tag.TAG_LONG);
        byte[] sizes = tag.getByteArray("InSizes");
        for (int i = 0; i < offsets.size(); i++) {
            in.put(BlockPos.of(((LongTag) offsets.get(i)).getAsLong()), ChainSize.byId(i < sizes.length ? sizes[i] : 0));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeChains(tag);
    }

    private void writeChains(CompoundTag tag) {
        if (out != null) {
            tag.putLong("Out", out.asLong());
            tag.putByte("OutSize", (byte) outSize.ordinal());
        }
        if (!in.isEmpty()) {
            ListTag list = new ListTag();
            byte[] sizes = new byte[in.size()];
            int i = 0;
            for (Map.Entry<BlockPos, ChainSize> entry : in.entrySet()) {
                list.add(LongTag.valueOf(entry.getKey().asLong()));
                sizes[i++] = (byte) entry.getValue().ordinal();
            }
            tag.put("In", list);
            tag.putByteArray("InSizes", sizes);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * The chains, and an always-present In list. An anchor left with no chains would otherwise send
     * an empty tag, which ClientboundBlockEntityDataPacket turns into no tag at all: the client never
     * loads it, and keeps drawing the chain that was cut.
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        writeChains(tag);
        if (!tag.contains("In")) {
            tag.put("In", new ListTag());
        }
        return tag;
    }
}
