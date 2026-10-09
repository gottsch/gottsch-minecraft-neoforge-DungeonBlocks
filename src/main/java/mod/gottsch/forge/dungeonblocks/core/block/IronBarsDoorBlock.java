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

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A cell door of iron bars that a player opens by hand, and redstone still opens as normal.
 *
 * <p>It keeps {@link BlockSetType#IRON}, whose {@code canOpenByHand} is false, and opens by hand
 * through {@link #use} instead - the same arrangement as {@link WaxedCopperDoorBlock}. A block set
 * type that allowed hand-opening would also make vanilla treat it as a wooden door: villagers
 * would path through it and zombies could break it down, which is exactly wrong for a cell.
 */
public class IronBarsDoorBlock extends DoorBlock {

   public IronBarsDoorBlock(Properties properties) {
      super(BlockSetType.IRON, properties);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      state = state.cycle(OPEN);
      level.setBlock(pos, state, 10);
      level.playSound(player, pos, state.getValue(OPEN) ? BlockSetType.IRON.doorOpen() : BlockSetType.IRON.doorClose(),
            SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
      level.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
      return InteractionResult.sidedSuccess(level.isClientSide);
   }
}
