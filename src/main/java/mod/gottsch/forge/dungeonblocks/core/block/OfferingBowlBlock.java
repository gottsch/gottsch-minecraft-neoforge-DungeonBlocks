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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.Direction;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.ToIntFunction;

/**
 * An offering bowl on a short foot, for an altar top or the floor before one: old copper as shipped. {@link #CONTENTS}
 * is what it holds: nothing, blood, or soul fire burning on a bed of soul soil.
 *
 * <p>An empty hand changes the contents to the next, as a catacomb niche's remains are rearranged;
 * holding anything passes, so building against a bowl still places the block. The default is
 * EMPTY, which is what a structure gets when it names no contents. Soul fire gives light 10 and
 * cannot burn under water: a waterlogged bowl skips it, and water poured into a burning bowl puts
 * the fire out. It is decoration and burns nothing.
 *
 * <p>The model is blockbench/offering_bowl.bbmodel, a template per contents, textured per material
 * in datagen.
 *
 * @author Mark Gottschling on 10/24/2026
 */
public class OfferingBowlBlock extends Block implements SimpleWaterloggedBlock {
    public enum Contents implements StringRepresentable {
        EMPTY, BLOOD, SOUL_FIRE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }

        /** The next contents, skipping fire where it could not burn. */
        Contents next(boolean underWater) {
            Contents next = values()[(ordinal() + 1) % values().length];
            return underWater && next == SOUL_FIRE ? next.next(true) : next;
        }
    }

    public static final EnumProperty<Contents> CONTENTS = EnumProperty.create("contents", Contents.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            state -> state.getValue(CONTENTS) == Contents.SOUL_FIRE ? 10 : 0;

    // the model's foot and bowl
    private static final VoxelShape SHAPE = Shapes.or(Block.box(6, 0, 6, 10, 2, 10), Block.box(4, 2, 4, 12, 6, 12));

    public OfferingBowlBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CONTENTS, Contents.EMPTY).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENTS, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, Waterlogging.placedInWater(context));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            Contents next = state.getValue(CONTENTS).next(state.getValue(WATERLOGGED));
            level.setBlock(pos, state.setValue(CONTENTS, next), Block.UPDATE_ALL);
            level.playSound(null, pos, next == Contents.SOUL_FIRE ? SoundEvents.FLINTANDSTEEL_USE
                    : next == Contents.BLOOD ? SoundEvents.BUCKET_EMPTY : SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS, 0.6F, 0.8F + level.random.nextFloat() * 0.4F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Water poured in puts a soul fire out. */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (!SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluid)) {
            return false;
        }
        if (state.getValue(CONTENTS) == Contents.SOUL_FIRE) {
            level.setBlock(pos, level.getBlockState(pos).setValue(CONTENTS, Contents.EMPTY), Block.UPDATE_ALL);
        }
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                  BlockPos pos, BlockPos neighborPos) {
        Waterlogging.tickWater(state, level, pos);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterlogging.fluid(state, super.getFluidState(state));
    }

    /** Now and then a soul flame and a crackle, as a soul campfire gives off. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(CONTENTS) != Contents.SOUL_FIRE) {
            return;
        }
        if (random.nextInt(8) == 0) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.4 + random.nextDouble() * 0.2,
                    pos.getY() + 0.6, pos.getZ() + 0.4 + random.nextDouble() * 0.2, 0, 0.02, 0);
        }
        if (random.nextInt(24) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS, 0.4F, 0.8F + random.nextFloat() * 0.4F, false);
        }
    }
}
