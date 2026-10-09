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

import mod.gottsch.forge.dungeonblocks.core.entity.PotEntity;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * Places a {@link PotEntity} upright on the clicked block face, mirroring
 * vanilla's boat/armor-stand placement items. The pot shape is decided by the
 * {@link EntityType} this item is constructed with, so every pot variant reuses
 * this class.
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotItem extends Item {

	private final Supplier<EntityType<PotEntity>> entityType;

	public PotItem(Supplier<EntityType<PotEntity>> entityType, Properties properties) {
		super(properties);
		this.entityType = entityType;
	}

	/** Which pot shape this item places; also how the inventory renderer picks its model. */
	public EntityType<PotEntity> getEntityType() {
		return this.entityType.get();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos placedOn = context.getClickedPos();
		Direction face = context.getClickedFace();
		BlockPos spawnPos = placedOn.relative(face);
		// clicked a wall (N/S/E/W) -> the pot lies on its side; top/bottom -> it stands upright
		boolean onSide = face.getAxis().isHorizontal();

		Player player = context.getPlayer();

		if (!level.isClientSide) {
			// built through the EntityType factory, not `new PotEntity(...)`, so the type's material
			// (see ModEntityTypes#potOf) is applied — a directly constructed pot would be terracotta
			// whatever its type says.
			PotEntity pot = this.entityType.get().create(level);
			if (pot == null) {
				return InteractionResult.PASS;
			}
			pot.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
			if (onSide) {
				// set before addFreshEntity so it's already in the spawn packet's synced data —
				// the pot appears on its side immediately instead of tipping over after spawning.
				pot.setTumbled(true);
				// orient off the wall rather than wherever the player happened to be facing
				pot.setYRot(face.toYRot());
			} else if (player != null) {
				pot.setYRot(player.getYRot());
			}
			level.addFreshEntity(pot);
		}

		if (player == null || !player.getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
