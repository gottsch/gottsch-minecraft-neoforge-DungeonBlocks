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
package mod.gottsch.forge.dungeonblocks.core.blockentity;

import mod.gottsch.forge.dungeonblocks.core.block.AbstractBannerBlock;
import mod.gottsch.forge.dungeonblocks.core.block.DungeonBannerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Renderer anchor for a {@link DungeonBannerBlock}. <b>Deliberately empty.</b>
 *
 * <p>It holds no fields, never ticks, saves nothing and syncs nothing. A block with a waving cloth
 * cannot be baked into the chunk mesh, and a {@code BlockEntityRenderer} is the only way to draw
 * outside it — so this exists purely to give one something to attach to. The wave itself is a
 * closed-form function of game time and block position, recomputed every frame on the client, which
 * is why there is no state to keep.
 *
 * <p>That makes the cost of a banner-heavy build the cost of a BlockEntity that is never ticked:
 * a map entry and a render call, nothing more.
 *
 * @author Mark Gottschling on Sep 12, 2026
 */
public class DungeonBannerBlockEntity extends BlockEntity {

	public DungeonBannerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.DUNGEON_BANNER.get(), pos, state);
	}

	/**
	 * How far the cloth actually reaches. A tall banner's upper half draws a full block below itself,
	 * so the default one-block render box would cull the whole banner the moment that block left the
	 * frustum - it would blink out while you were still looking straight at its bottom half. A
	 * pennant draws only inside its own block and gets the default.
	 */
	// read by the renderer: NeoForge 21.1 moved the render bounds to BlockEntityRenderer
	public AABB getRenderBoundingBox() {
		AbstractBannerBlock banner = AbstractBannerBlock.of(this.getBlockState());
		int below = banner == null ? 1 : banner.blocksBelow();
		return AABB.encapsulatingFullBlocks(this.worldPosition.below(below), this.worldPosition);
	}
}
