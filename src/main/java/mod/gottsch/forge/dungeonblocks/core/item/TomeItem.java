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
import mod.gottsch.forge.dungeonblocks.core.entity.TomeEntity;
import mod.gottsch.forge.dungeonblocks.core.entity.TomeVariant;
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
 * Lays a {@link TomeEntity} down where it is used, its foot toward the player. On top of a block it
 * lies exactly where the click landed, so tomes can go anywhere along a shelf or table; against a
 * side it drops into the space in front. Sneaking, it stands upright there, spine to the player.
 */
public class TomeItem extends Item {
	private final TomeVariant variant;

	public TomeItem(TomeVariant variant, Properties properties) {
		super(properties);
		this.variant = variant;
	}

	/** Laid down while sneaking, on the top of a block, it stands upright. */
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Direction face = context.getClickedFace();
		Vec3 at = face == Direction.UP ? context.getClickLocation()
				: Vec3.atBottomCenterOf(context.getClickedPos().relative(face));
		Player player = context.getPlayer();
		return this.lay(context.getLevel(), at, player != null ? player.getYRot() : 0.0F,
				face == Direction.UP && player != null && player.isShiftKeyDown(), player, context.getItemInHand());
	}

	/**
	 * Lays a tome of this cover at {@code at}, turned to {@code yaw}, if there is room for it: on a
	 * block, or on another tome (TomeEntity#interact).
	 */
	public InteractionResult lay(Level level, Vec3 at, float yaw, boolean upright, @Nullable Player player, ItemStack stack) {
		if (!level.isClientSide) {
			TomeEntity tome = ModEntityTypes.TOME.get().create(level);
			if (tome == null) {
				return InteractionResult.PASS;
			}
			tome.setVariant(this.variant);
			tome.setUpright(upright);
			tome.setPos(at);
			tome.setYRot(yaw);
			if (!level.noCollision(tome.getBoundingBox().deflate(1.0E-4D))) {
				return InteractionResult.FAIL;
			}
			level.addFreshEntity(tome);
			level.playSound(null, at.x, at.y, at.z, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(player, GameEvent.ENTITY_PLACE, at);
		}
		if (player == null || !player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
