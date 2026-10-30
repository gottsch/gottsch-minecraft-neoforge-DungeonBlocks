/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class WeatheringCopperDoorBlock extends DoorBlock implements ModWeatheringCopper {
   private final WeatherState weatherState;

   public WeatheringCopperDoorBlock(BlockSetType blockSetType, WeatherState weatherState, Properties properties) {
      super(blockSetType, properties);
      this.weatherState = weatherState;
   }

   @Override
   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
         this.changeOverTime(state, level, pos, randomSource);
      }
   }

   @Override
   public boolean isRandomlyTicking(BlockState state) {
      return ModWeatheringCopper.getNext(state.getBlock()).isPresent();
   }

   @Override
   public WeatherState getAge() {
      return this.weatherState;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      state = (BlockState)state.cycle(OPEN);
      level.setBlock(pos, state, 10);
      this.playSound(player, level, pos, (Boolean)state.getValue(OPEN));
      level.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   public static boolean isWoodenDoor(BlockState state) {
      return false;
   }

   private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean p_251628_) {
      level.playSound(entity, pos, p_251628_ ? BlockSetType.IRON.doorOpen() : BlockSetType.IRON.doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
   }

   /** The other half follows when this one becomes a different copper door: see {@link CopperDoorHalves}. */
   @Override
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      BlockState follow = CopperDoorHalves.follow(state, direction, neighbor);
      return follow != null ? follow : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
   }
}
