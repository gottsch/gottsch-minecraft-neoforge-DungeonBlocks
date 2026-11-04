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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A false wall of femurs, to hide a secret room behind. It is Jumbled Femurs with a few hairline
 * cracks for a player who looks. Punched, used with an empty hand or given a redstone signal, it
 * shudders, and a second later collapses; the tremor passes to every crumbling bone wall touching it,
 * so a whole false wall goes in a wave from where it was touched. Walking into it does nothing, so a
 * mob cannot open the room by accident.
 *
 * <p>Its lowest blocks leave a bone pile where they stood, if there is a floor there, so the doorway
 * reads as a collapse. Otherwise crumbling drops nothing. Sneaking leaves it still, so it can be
 * mined back like any block.
 *
 * <p>The textures are tools/gen_crumbling_bone_wall_textures.py's. Nothing ticks until it is set off,
 * as on {@link CrumblingFloorBlock}.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class CrumblingBoneWallBlock extends WallPatternBlock {
    /** Set off: shaking, and about to go. */
    public static final BooleanProperty SHAKING = CrumblingFloorBlock.SHAKING;

    public CrumblingBoneWallBlock(Properties properties) {
        super(properties);
        // defaultBlockState(), for the pattern the parent has set; any() would leave SHAKING true
        registerDefaultState(defaultBlockState().setValue(SHAKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAKING);
    }

    /** A punch. The client's own creative break never comes here, so creative can still clear one. */
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && !state.getValue(SHAKING) && !player.isShiftKeyDown()) {
            setOff(level, pos, state);
        }
        super.attack(state, level, pos, player);
    }

    /** An empty hand only: holding anything passes, so building against it still places the block. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || player.isShiftKeyDown() || state.getValue(SHAKING)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            setOff(level, pos, state);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (!level.isClientSide && !state.getValue(SHAKING) && level.hasNeighborSignal(pos)) {
            setOff(level, pos, state);
        }
        super.neighborChanged(state, level, pos, block, fromPos, moving);
    }

    /** A tick on a still block is the tremor arriving from a neighbour; on a shaking one it is the fall. */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(SHAKING)) {
            setOff(level, pos, state);
            return;
        }
        // read first: the block below may be more false wall, still to fall
        boolean lowest = !(level.getBlockState(pos.below()).getBlock() instanceof CrumblingBoneWallBlock);
        level.destroyBlock(pos, false);
        BlockState pile = ModBlocks.BONE_PILE.get().defaultBlockState()
                .setValue(BonePileBlock.BONES, 1 + (int) Math.floorMod(Mth.getSeed(pos), 3L));
        if (lowest && pile.canSurvive(level, pos) && level.getFluidState(pos).is(Fluids.EMPTY)) {
            level.setBlock(pos, pile, Block.UPDATE_ALL);
        }
    }

    /** Starts this block shaking toward its fall, and passes the tremor on to any touching it. */
    private static void setOff(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(SHAKING, true), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, state.getBlock(), CrumblingFloorBlock.CRUMBLE_DELAY);
        level.playSound(null, pos, SoundEvents.BONE_BLOCK_BREAK, SoundSource.BLOCKS, 0.8F,
                0.5F + level.random.nextFloat() * 0.3F);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            BlockState neighbour = level.getBlockState(next);
            if (neighbour.getBlock() instanceof CrumblingBoneWallBlock && !neighbour.getValue(SHAKING)) {
                level.scheduleTick(next, neighbour.getBlock(), CrumblingFloorBlock.SPREAD_DELAY);
            }
        }
    }

    /** While it shakes: dust sifting from its underside, and grit skittering off its faces. */
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
        Direction face = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockParticleOption grit = new BlockParticleOption(ParticleTypes.BLOCK, state);
        level.addParticle(grit, pos.getX() + 0.5 + face.getStepX() * 0.55 + (random.nextDouble() - 0.5) * Math.abs(face.getStepZ()),
                pos.getY() + random.nextDouble(),
                pos.getZ() + 0.5 + face.getStepZ() * 0.55 + (random.nextDouble() - 0.5) * Math.abs(face.getStepX()),
                face.getStepX() * 0.05, 0.05, face.getStepZ() * 0.05);
    }
}
