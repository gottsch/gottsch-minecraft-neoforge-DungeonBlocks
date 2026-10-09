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
package mod.gottsch.forge.dungeonblocks.core.item;

import mod.gottsch.forge.dungeonblocks.core.entity.ModEntityTypes;
import mod.gottsch.forge.dungeonblocks.core.entity.ScrollEntity;
import mod.gottsch.forge.dungeonblocks.core.entity.ScrollVariant;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Lays a {@link ScrollEntity} down rolled up, as the tome item lays a tome: on top of a block
 * exactly where the click landed, against a side into the space in front. It lies across the
 * player's view, so it unrolls toward them.
 */
public class ScrollItem extends Item {
	private final ScrollVariant variant;

	public ScrollItem(ScrollVariant variant, Properties properties) {
		super(properties);
		this.variant = variant;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Direction face = context.getClickedFace();
		Vec3 at = face == Direction.UP ? context.getClickLocation()
				: Vec3.atBottomCenterOf(context.getClickedPos().relative(face));
		Player player = context.getPlayer();
		return this.lay(context.getLevel(), at, player != null ? player.getYRot() : 0.0F, player, context.getItemInHand());
	}

	/** Lays a scroll of this design at {@code at}, turned to {@code yaw}, if there is room for it. */
	public InteractionResult lay(Level level, Vec3 at, float yaw, @Nullable Player player, ItemStack stack) {
		if (!level.isClientSide) {
			ScrollEntity scroll = ModEntityTypes.SCROLL.get().create(level);
			if (scroll == null) {
				return InteractionResult.PASS;
			}
			scroll.setVariant(this.variant);
			scroll.setPos(at);
			scroll.setYRot(yaw);
			if (!level.noCollision(scroll.getBoundingBox().deflate(1.0E-4D))) {
				return InteractionResult.FAIL;
			}
			level.addFreshEntity(scroll);
			level.playSound(null, at.x, at.y, at.z, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.3F);
			level.gameEvent(player, GameEvent.ENTITY_PLACE, at);
		}
		if (player == null || !player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
