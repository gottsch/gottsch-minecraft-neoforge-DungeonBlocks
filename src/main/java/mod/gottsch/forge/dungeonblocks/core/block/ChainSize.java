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
package mod.gottsch.forge.dungeonblocks.core.block;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * The sizes of chain hung between {@link ChainAnchorBlock}s. Each chain on an anchor records
 * its size, which picks the link drawn along it, the spacing of the links, and the item it gives
 * back when cut. A chain saved before sizes existed has none recorded and is {@link #GIANT}.
 *
 * <p>Saved by ordinal, with a missing size read as 0: keep {@link #GIANT} first and add new sizes
 * at the end.
 *
 * <p>The pitch is a link's inside length, so each link's end bar sits in the next one's eye.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public enum ChainSize {
    /** 16px long, 10 wide, a 3px bar. */
    GIANT("giant_chain_link", 10.0 / 16.0, () -> ModItems.GIANT_CHAIN.get()),
    /** 10px long, 7 wide, a 2px bar. */
    MEDIUM("medium_chain_link", 6.0 / 16.0, () -> ModItems.MEDIUM_CHAIN.get());

    private final ResourceLocation linkModel;
    private final double pitch;
    private final Supplier<Item> item;

    ChainSize(String linkModel, double pitch, Supplier<Item> item) {
        this.linkModel = ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/" + linkModel);
        this.pitch = pitch;
        this.item = item;
    }

    /** The link model, registered as an additional model and drawn along the chain. */
    public ResourceLocation getLinkModel() {
        return linkModel;
    }

    /** Link centre to link centre, in blocks. */
    public double getPitch() {
        return pitch;
    }

    /** The item that hangs a chain of this size, and that a cut one gives back. */
    public Item getItem() {
        return item.get();
    }

    /** The size saved as {@code id}; anything unknown is {@link #GIANT}. */
    public static ChainSize byId(int id) {
        ChainSize[] sizes = values();
        return id >= 0 && id < sizes.length ? sizes[id] : GIANT;
    }
}
