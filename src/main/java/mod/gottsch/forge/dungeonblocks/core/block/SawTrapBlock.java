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

import mod.gottsch.forge.dungeonblocks.core.blockentity.SawTrapBlockEntity;
import mod.gottsch.forge.dungeonblocks.core.damage.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A floor block with a circular saw hidden inside it. The only sign of it is a slot across the top.
 * A player steps on it, there is a click, and {@link #DELAY} ticks later the blade rises out of the
 * slot, spinning, and cuts whatever living thing is still standing there. It stays up, cutting every
 * {@link #CUT_INTERVAL} ticks, until the block's top is clear and it has no redstone signal, then
 * sinks back and re-arms.
 *
 * <p>Only players spring it by stepping on it, so a dungeon's own mobs don't give it away or wear
 * themselves down on it. Anything else springs it through redstone: a signal turning on fires it
 * the same way (click, delay, blade), and holds the blade up while it lasts. So a base can wire it
 * to a pressure plate, tripwire or sensor, with a lever to make it safe for its owner.
 *
 * <p>All of the trap's state is in the blockstate. RAISED is the blade up or down; POWERED is the
 * last redstone signal seen; AXIS is the way the slot runs, taken from the placer's facing. The blade is never baked into the chunk mesh:
 * {@code SawTrapRenderer} draws it from the block entity whenever it is out of the block. Down, it
 * would be hidden inside a solid cube anyway. The block itself is a plain cube, with the slot as a
 * cutout overlay on its top face. Textures and the blade model: tools/gen_saw_trap.py.
 *
 * @author Mark Gottschling on 11/05/2026
 */
public class SawTrapBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty RAISED = BooleanProperty.create("raised");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    /** Ticks from the click to the blade coming up: long enough to jump clear, if you are quick. */
    public static final int DELAY = 10;
    /** Ticks between cuts while the blade is up, and the least time it stays up. */
    public static final int CUT_INTERVAL = 10;
    /** Damage per cut: two hearts. */
    public static final float DAMAGE = 4.0F;
    /** How high above the block the blade reaches, in blocks: the space it cuts. */
    public static final double REACH = 9.0 / 16.0;

    public SawTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Z).setValue(RAISED, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, RAISED, POWERED);
    }

    /** The slot runs the way the placer is looking. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.setValue(AXIS,
                    state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
            default -> state;
        };
    }

    /** Springs when a player steps on it. */
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player && !entity.isSpectator()) {
            spring(level, pos, state, entity);
        }
        super.stepOn(level, pos, state, entity);
    }

    /** Springs when a redstone signal turns on; the signal also holds the blade up (see {@link #tick}). */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (level.isClientSide) {
            return;
        }
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            return;
        }
        state = state.setValue(POWERED, powered);
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        if (powered) {
            spring(level, pos, state, null);
        }
    }

    /** Clicks and sets the blade coming, unless it is already up or on its way. */
    private void spring(Level level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        if (state.getValue(RAISED) || level.getBlockTicks().hasScheduledTick(pos, this)) {
            return;
        }
        level.scheduleTick(pos, this, DELAY);
        level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
        level.gameEvent(cause, GameEvent.BLOCK_ACTIVATE, pos);
    }

    /**
     * Down: the blade comes up and cuts. Up: it cuts again, and stays up while anything is still on
     * the block or the redstone signal is on; it sinks once neither is.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(RAISED)) {
            level.setBlock(pos, state.setValue(RAISED, true), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.4F);
            cut(level, pos);
            level.scheduleTick(pos, this, CUT_INTERVAL);
            level.gameEvent(null, GameEvent.BLOCK_ACTIVATE, pos);
        } else if (cut(level, pos) | state.getValue(POWERED)) {
            level.scheduleTick(pos, this, CUT_INTERVAL);
        } else {
            level.setBlock(pos, state.setValue(RAISED, false), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.4F, 1.2F);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
        }
    }

    /** Hurts everything living in the blade's reach above the block; whether anything was there. */
    private boolean cut(ServerLevel level, BlockPos pos) {
        AABB reach = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1 + REACH, pos.getZ() + 1);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, reach,
                entity -> entity.isAlive() && !entity.isSpectator());
        if (victims.isEmpty()) {
            return false;
        }
        for (LivingEntity victim : victims) {
            victim.hurt(ModDamageTypes.saw(level), DAMAGE);
        }
        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F,
                0.7F + level.random.nextFloat() * 0.2F);
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SawTrapBlockEntity(pos, state);
    }
}
