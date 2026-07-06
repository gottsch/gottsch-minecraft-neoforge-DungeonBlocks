/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;

/**
 *
 * @author Mark Gottschling on 10/14/2025
 *
 */
public class WeatheringCopperValveWheelBlock extends ValveWheelBlock implements ModWeatheringCopper {
   private final WeatherState weatherState;

   public WeatheringCopperValveWheelBlock(WeatherState weatherState, Properties properties) {
      super(properties);
      this.weatherState = weatherState;
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      this.changeOverTime(state, level, pos, randomSource);
   }

   public boolean isRandomlyTicking(BlockState state) {
      return ModWeatheringCopper.getNext(state.getBlock()).isPresent();
   }

   public WeatherState getAge() {
      return this.weatherState;
   }
}
