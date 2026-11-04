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
import mod.gottsch.forge.dungeonblocks.core.block.BrazierBlock.BrazierFire;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A fire basket (a cresset): an open dark iron basket of burning logs, hung on four rods from an
 * eye, under a vanilla chain or from a ceiling as a lantern hangs. The fire shows through the bars.
 *
 * <p>It burns as the brazier does, on the brazier's own {@link BrazierBlock#FIRE} property, so a
 * dungeon generator writes the same states for both: {@code none}, {@code embers}, {@code lit} and
 * {@code soul}. A torch or flint and steel lights it, a soul torch lights soul fire, an empty hand
 * puts it out, and water drowns it. Embers are only placed, never struck by hand.
 *
 * <p>Being hung, it does what a floor brazier cannot: now and then a spark drops out of its bottom.
 * On a swinging chain it is a fixture ({@code ChainFixture.FIRE_BASKET} and
 * {@code SOUL_FIRE_BASKET}), drawn from the same models (blockbench/fire_basket.bbmodel).
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class FireBasketBlock extends HangingFixtureBlock {
    public static final EnumProperty<BrazierFire> FIRE = BrazierBlock.FIRE;
    /** The brazier's levels: 15 lit, 10 soul, 3 embers. */
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(FIRE).getLightLevel();
    /** The basket, and the rods and eye above it. */
    public static final VoxelShape SHAPE = Shapes.or(Block.box(3, 1, 3, 13, 10.5, 13), Block.box(6.5, 10.5, 6.5, 9.5, 16, 9.5));

    /** The top of the logs, where the flames start; and the base's underside, where sparks fall from. */
    private static final float FUEL_Y = 7.0F / 16.0F;
    private static final float BASE_Y = 0.9F / 16.0F;

    public FireBasketBlock(Properties properties) {
        super(properties, SHAPE);
        registerDefaultState(defaultBlockState().setValue(FIRE, BrazierFire.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FIRE);
    }

    /** A torch or flint and steel lights it, a soul torch with soul fire; embers can still be struck to a flame. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        BrazierFire fire = state.getValue(FIRE);
        boolean soul = held.is(Blocks.SOUL_TORCH.asItem());
        boolean igniter = soul || held.is(Items.FLINT_AND_STEEL) || held.is(Blocks.TORCH.asItem());
        boolean burning = fire == BrazierFire.LIT || fire == BrazierFire.SOUL;
        if (!player.getAbilities().mayBuild || burning || state.getValue(WATERLOGGED) || !igniter) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(FIRE, soul ? BrazierFire.SOUL : BrazierFire.LIT), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (held.is(Items.FLINT_AND_STEEL)) {
                held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** An empty main hand puts it out. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild || !player.getMainHandItem().isEmpty() || state.getValue(FIRE) == BrazierFire.NONE) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(FIRE, BrazierFire.NONE), Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.2F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Water floods in: the fire goes out. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (!state.getValue(WATERLOGGED) && fluid.getType() == Fluids.WATER) {
            level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(FIRE, BrazierFire.NONE), Block.UPDATE_ALL);
            if (state.getValue(FIRE) != BrazierFire.NONE) {
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.2F);
            }
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
            return true;
        }
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        particles(state.getValue(FIRE), level, random, at -> new Vec3(pos.getX() + at.x, pos.getY() + at.y, pos.getZ() + at.z));
    }

    /**
     * One tick's particles for a basket burning {@code fire}. {@code place} takes a point in the
     * basket's block space (0-1) to the world: straight across for the block, along the swing on a
     * swinging chain. Embers smoke now and then; a fire crackles, smokes, throws flames up off the
     * logs and drops the odd spark out of the bottom.
     */
    public static void particles(BrazierFire fire, Level level, RandomSource random, Function<Vector3f, Vec3> place) {
        if (fire == BrazierFire.NONE) {
            return;
        }
        if (fire == BrazierFire.EMBERS) {
            if (random.nextInt(12) == 0) {
                Vec3 at = place.apply(overFuel(random, 0.12F));
                level.addParticle(ParticleTypes.SMOKE, at.x, at.y, at.z, 0.0D, 0.01D, 0.0D);
            }
            return;
        }
        boolean soul = fire == BrazierFire.SOUL;
        if (random.nextInt(10) == 0) {
            Vec3 at = place.apply(new Vector3f(0.5F, 0.5F, 0.5F));
            level.playLocalSound(at.x, at.y, at.z, soul ? SoundEvents.SOUL_ESCAPE.value() : SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }
        if (random.nextInt(3) == 0) {
            Vec3 at = place.apply(overFuel(random, 0.15F).add(0, 0.35F, 0));
            level.addParticle(ParticleTypes.SMOKE, at.x, at.y, at.z, 0.0D, 0.02D, 0.0D);
        }
        if (random.nextInt(4) == 0) {
            Vec3 at = place.apply(overFuel(random, 0.15F));
            level.addParticle(soul ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, at.x, at.y, at.z, 0.0D, 0.03D, 0.0D);
        }
        // a spark through the bottom bars, falling: dripping lava (an orange speck that falls and
        // fades) for a fire, a soul fire flame drifting down for soul fire
        if (random.nextInt(soul ? 12 : 8) == 0) {
            Vector3f under = new Vector3f(0.5F + (random.nextFloat() - 0.5F) * 0.3F, BASE_Y,
                    0.5F + (random.nextFloat() - 0.5F) * 0.3F);
            Vec3 at = place.apply(under);
            if (soul) {
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, at.x, at.y, at.z, 0.0D, -0.04D, 0.0D);
            } else {
                level.addParticle(ParticleTypes.FALLING_LAVA, at.x, at.y, at.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /** A point on top of the logs, up to {@code spread} either way from the middle. */
    private static Vector3f overFuel(RandomSource random, float spread) {
        return new Vector3f(0.5F + (random.nextFloat() - 0.5F) * 2 * spread, FUEL_Y,
                0.5F + (random.nextFloat() - 0.5F) * 2 * spread);
    }
}
