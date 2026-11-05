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
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A cell door of iron bars that a player opens by hand, and redstone still opens as normal.
 *
 * <p>It keeps {@link BlockSetType#IRON}, whose {@code canOpenByHand} is false, and opens by hand
 * through {@link #use} instead - the same arrangement as {@link WaxedCopperDoorBlock}. A block set
 * type that allowed hand-opening would also make vanilla treat it as a wooden door: villagers
 * would path through it and zombies could break it down, which is exactly wrong for a cell.
 *
 * <p>Shut, it stands in the middle of its block, on the plane iron bars stand on, so a cell front of
 * bars and a door reads as one line (3.1.0; a vanilla door, and this one until then, hangs at the
 * block's edge, 7px out of line with the bars beside it). Open, it swings back against the edge as a
 * vanilla door does. The models are {@code template_edged_door_*}: only the shut ones moved.
 *
 * <p>Iron bars, panes, walls and fences join a neighbour only through a sturdy face, and a centred
 * panel touches its two in-line faces with a 3px strip, so shut it reports a support shape whose
 * in-line faces are full: the bars either side reach it, as they did the old edge-hung door on its
 * hinge side. Open, the support shape is vanilla's, as before.
 */
public class IronBarsDoorBlock extends DoorBlock {
   // shut, centred on the bars' plane: 3px thick, as the panel is, across the block
   private static final VoxelShape SHUT_X = Block.box(6.5D, 0.0D, 0.0D, 9.5D, 16.0D, 16.0D);
   private static final VoxelShape SHUT_Z = Block.box(0.0D, 0.0D, 6.5D, 16.0D, 16.0D, 9.5D);
   // shut support: the panel plus a 1px skin on each in-line face, so those two faces are sturdy
   private static final VoxelShape SUPPORT_X = Shapes.or(SHUT_X,
         Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D), Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
   private static final VoxelShape SUPPORT_Z = Shapes.or(SHUT_Z,
         Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D), Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));


   public IronBarsDoorBlock(Properties properties) {
      super(BlockSetType.IRON, properties);
   }

   /** Shut: centred, across the facing axis. Open: vanilla's, against the hinge-side edge. */
   @Override
   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (state.getValue(OPEN)) {
         return super.getShape(state, level, pos, context);
      }
      return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHUT_X : SHUT_Z;
   }

   /** Shut: in-line faces full, so bars beside the door connect to it. Never collided with. */
   @Override
   protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
      if (state.getValue(OPEN)) {
         return super.getBlockSupportShape(state, level, pos);
      }
      return state.getValue(FACING).getAxis() == Direction.Axis.X ? SUPPORT_X : SUPPORT_Z;
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
