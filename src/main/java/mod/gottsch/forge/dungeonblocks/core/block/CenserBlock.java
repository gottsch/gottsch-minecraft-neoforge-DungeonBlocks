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

import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.joml.Vector3f;

import java.util.function.ToIntFunction;

/**
 * A censer hung as a block: an iron bowl of incense that smoulders once lit. Lit and put out as
 * the chandelier is - a torch or flint and steel lights it, an empty hand puts it out, water puts
 * it out and it cannot be lit waterlogged. A lit one glows faintly and smokes from under its lid.
 */
public class CenserBlock extends HangingFixtureBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    /** Smouldering incense: a glow, not a lamp. The same on a swinging chain (ChainFixture). */
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIT) ? 7 : 0;
    /**
     * Height of the ember band under the lid (blockbench/chain_fixture_censer.bbmodel), 0-1: where
     * the smoke comes out round the rim, here and on a swinging chain.
     */
    public static final float SMOKE_Y = 6.0F / 16.0F;
    /** Just outside the lid's edge (5..11 px): smoke born any nearer the middle is inside the model. */
    private static final float RIM = 3.25F / 16.0F;
    /** Just above the knob, the censer's top (9..10 px). */
    private static final float TOP_Y = 10.25F / 16.0F;

    public CenserBlock(Properties properties, VoxelShape shape) {
        super(properties, shape);
        // LIT set explicitly: stateDefinition.any() would leave the added boolean true
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // empty hand only: useWithoutItem also runs for an item that useItemOn passed on
        if (!player.getAbilities().mayBuild || !state.getValue(LIT) || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.5F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean igniter = held.is(Items.FLINT_AND_STEEL) || held.is(Blocks.TORCH.asItem());
        if (!player.getAbilities().mayBuild || state.getValue(LIT) || state.getValue(WATERLOGGED) || !igniter) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (held.is(Items.FLINT_AND_STEEL)) {
                held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Water floods in: the incense goes out. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (!state.getValue(WATERLOGGED) && fluid.getType() == Fluids.WATER) {
            level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(LIT, false), Block.UPDATE_ALL);
            if (state.getValue(LIT)) {
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.5F);
            }
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
            return true;
        }
        return false;
    }

    /**
     * Where a wisp of smoke starts, in the censer's own block space (0-1): mostly from under the
     * lid's rim on one side or another, sometimes from the top by the knob. Here and on a swinging
     * chain, which moves the point with the swing.
     */
    public static Vector3f smokeOrigin(RandomSource random) {
        if (random.nextInt(3) == 0) {
            return new Vector3f(0.5F + (random.nextFloat() - 0.5F) * 0.1F, TOP_Y, 0.5F + (random.nextFloat() - 0.5F) * 0.1F);
        }
        float along = (random.nextFloat() - 0.5F) * 0.3F;
        float out = random.nextBoolean() ? RIM : -RIM;
        return random.nextBoolean()
                ? new Vector3f(0.5F + out, SMOKE_Y, 0.5F + along)
                : new Vector3f(0.5F + along, SMOKE_Y, 0.5F + out);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        Vector3f at = smokeOrigin(random);
        double x = pos.getX() + at.x();
        double y = pos.getY() + at.y();
        double z = pos.getZ() + at.z();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.015, 0.0);
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.1, z, 0.0, 0.02, 0.0);
        }
    }
}
