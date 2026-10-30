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
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * A floor that gives way. Step on it - player or mob - and it shudders, trickles dust into whatever
 * is beneath, and a second later crumbles away to nothing. The tremor passes to every crumbling
 * floor block touching it, a few ticks per block, so a whole patch drops out in a wave spreading
 * from where it was trodden on, and whoever is on it goes down with it. A redstone signal sets it
 * off the same way, for a trap sprung from elsewhere.
 *
 * <p>It is its stone with a few faint hairline cracks (tools/gen_crumbling_floor_textures.py), there
 * to be spotted by a player who looks; once it starts to shake the cracks open right up. Broken by
 * hand it drops itself like any block; crumbling, it drops nothing.
 *
 * <p>Nothing ticks until it is set off: the tremor and the fall are scheduled ticks.
 */
public class CrumblingFloorBlock extends Block {
    /** Set off: shaking, and about to go. */
    public static final BooleanProperty SHAKING = BooleanProperty.create("shaking");
    /** Ticks from the first tremor to the fall: a moment to see it coming, not to walk off it. */
    public static final int CRUMBLE_DELAY = 20;
    /** Ticks for the tremor to pass to a neighbour, so the collapse spreads as a wave. */
    public static final int SPREAD_DELAY = 3;

    public CrumblingFloorBlock(Properties properties) {
        super(properties);
        // SHAKING set explicitly: stateDefinition.any() would leave the boolean true
        registerDefaultState(stateDefinition.any().setValue(SHAKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAKING);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity && !state.getValue(SHAKING)) {
            setOff(level, pos, state);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (!level.isClientSide && !state.getValue(SHAKING) && level.hasNeighborSignal(pos)) {
            setOff(level, pos, state);
        }
        super.neighborChanged(state, level, pos, block, fromPos, moving);
    }

    /**
     * A tick on a still block is the tremor arriving from a neighbour; on a shaking one it is the
     * fall. The fall is destroyBlock without drops, so it breaks with its stone's sound and
     * particles, and whatever stood on it drops through.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(SHAKING)) {
            setOff(level, pos, state);
        } else {
            level.destroyBlock(pos, false);
        }
    }

    /** Starts this block shaking toward its fall, and passes the tremor on to any crumbling floor touching it. */
    private static void setOff(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(SHAKING, true), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, state.getBlock(), CRUMBLE_DELAY);
        level.playSound(null, pos, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 0.7F,
                0.5F + level.random.nextFloat() * 0.2F);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            BlockState neighbour = level.getBlockState(next);
            if (neighbour.getBlock() instanceof CrumblingFloorBlock && !neighbour.getValue(SHAKING)) {
                level.scheduleTick(next, neighbour.getBlock(), SPREAD_DELAY);
            }
        }
    }

    /** While it shakes: dust sifting down out of its underside, and grit skittering on top. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(SHAKING)) {
            return;
        }
        BlockParticleOption dust = new BlockParticleOption(ParticleTypes.FALLING_DUST, state);
        for (int i = 0; i < 3; i++) {
            level.addParticle(dust, pos.getX() + random.nextDouble(), pos.getY() - 0.05, pos.getZ() + random.nextDouble(),
                    0.0, 0.0, 0.0);
        }
        BlockParticleOption grit = new BlockParticleOption(ParticleTypes.BLOCK, state);
        level.addParticle(grit, pos.getX() + random.nextDouble(), pos.getY() + 1.02, pos.getZ() + random.nextDouble(),
                0.0, 0.05, 0.0);
    }
}
