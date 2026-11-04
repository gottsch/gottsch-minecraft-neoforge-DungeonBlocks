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

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.ItemInteractionResult;
import mod.gottsch.forge.dungeonblocks.core.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.function.ToIntFunction;

/**
 * A dark iron floor grate over a pit of soul fire. Lit, it glows through the bars and gives off a
 * thin haze of soul mist, with now and then a soul-fire flame and, rarely, a soul escaping.
 *
 * <p>It behaves like a campfire: placed lit, put out by a shovel (any tool that can dig as one;
 * Forge 1.20.1 has no douse action) or by water, relit by flint and
 * steel, a fire charge or a soul torch. A waterlogged vent stays out. Vanilla's own campfire
 * handling is not used - flint and steel and the shovel only treat {@code CampfireBlock}s that
 * way, and the #campfires tag would let bee hives read it as smoke - so {@link #use} does it.
 *
 * <p>The particles come from {@link #animateTick}, the client's random display tick, so a gallery
 * of vents costs nothing on the server. None rise while a solid block covers the grate.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class SoulVentBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** Vanilla soul fire's light. */
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIT) ? 10 : 0;

    public SoulVentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, true).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = Waterlogging.placedInWater(context);
        return this.defaultBlockState().setValue(WATERLOGGED, water).setValue(LIT, !water);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = stack;
        if (!player.getAbilities().mayBuild) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean lit = state.getValue(LIT);
        if (lit && held.canPerformAction(ItemAbilities.SHOVEL_DIG)) {
            if (!level.isClientSide) {
                douse(state, level, pos);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!lit && !state.getValue(WATERLOGGED)
                && (held.is(Items.FLINT_AND_STEEL) || held.is(Items.FIRE_CHARGE) || held.is(Items.SOUL_TORCH))) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
                level.playSound(null, pos, held.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE
                        : held.is(Items.SOUL_TORCH) ? SoundEvents.SOUL_ESCAPE.value() : SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                if (held.is(Items.FLINT_AND_STEEL)) {
                    held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                } else if (!player.getAbilities().instabuild) {
                    // the soul torch is spent lighting it, as a fire charge is
                    held.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static void douse(BlockState state, LevelAccessor level, BlockPos pos) {
        level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    /** Water flowing or poured in puts it out, as it does a campfire. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (!state.getValue(WATERLOGGED) && fluid.getType() == Fluids.WATER) {
            if (state.getValue(LIT)) {
                douse(state, level, pos);
                state = state.setValue(LIT, false);
            }
            level.setBlock(pos, state.setValue(WATERLOGGED, true), Block.UPDATE_ALL);
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
            return true;
        }
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) || level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN)) {
            return;
        }
        double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
        double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
        double y = pos.getY() + 0.95;
        if (random.nextInt(3) != 0) {
            level.addParticle(ModParticles.SOUL_MIST_PARTICLE.get(), x, y, z,
                    (random.nextDouble() - 0.5) * 0.01, 0.012 + random.nextDouble() * 0.012, (random.nextDouble() - 0.5) * 0.01);
        }
        if (random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.02, 0.0);
        }
        if (random.nextInt(120) == 0) {
            level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.03, 0.0);
            level.playLocalSound(pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 0.4F, 0.8F + random.nextFloat() * 0.4F, false);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        Waterlogging.tickWater(state, level, pos);
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }
}
