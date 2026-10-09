/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.dungeonblocks.core.event;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModWeatheringCopper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

/**
 * Waxing and scraping for the mod's copper, as vanilla copper has them. Vanilla's honeycomb and
 * axe only know vanilla's own blocks (HoneycombItem.WAXABLES, WeatheringCopper), so the mod's
 * copper families are handled here, from the maps in {@link ModWeatheringCopper}.
 */
@EventBusSubscriber(modid = DungeonBlocks.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class CopperInteractions {
    private CopperInteractions() {
    }

    /**
     * An axe scrapes one age off, or takes the wax off. NeoForge's AxeItem asks for these after
     * stripping, and itself plays the sound and particles and damages the axe for whichever applies.
     */
    @SubscribeEvent
    public static void onToolUse(BlockEvent.BlockToolModificationEvent event) {
        BlockState state = event.getState();
        if (event.getItemAbility() == ItemAbilities.AXE_SCRAPE) {
            ModWeatheringCopper.getPrevious(state).ifPresent(event::setFinalState);
        } else if (event.getItemAbility() == ItemAbilities.AXE_WAX_OFF) {
            ModWeatheringCopper.getUnwaxed(state).ifPresent(event::setFinalState);
        }
    }

    /** A honeycomb waxes the block, as vanilla's HoneycombItem.useOn does for vanilla copper. */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.HONEYCOMB)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Optional<BlockState> waxed = ModWeatheringCopper.getWaxed(level.getBlockState(pos));
        if (waxed.isEmpty()) {
            return;
        }
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }
        // the event is handled before the game's creative-mode count restore, so do it here
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.setBlock(pos, waxed.get(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxed.get()));
        level.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
        // waxing replaces the door's or trapdoor's own use (opening it), as it does for vanilla's
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
