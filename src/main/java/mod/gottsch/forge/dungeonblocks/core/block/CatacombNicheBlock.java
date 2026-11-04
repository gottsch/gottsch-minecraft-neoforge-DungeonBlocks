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
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * A catacomb niche: a block of wall with a burial recess cut into one face, a skull and bones left
 * in it. Build it into a wall of its stone. FACING is the way the opening looks; it is placed
 * facing the player. The model is blockbench/catacomb_niche.bbmodel, a template per REMAINS that
 * datagen fills with each stone.
 *
 * <p>REMAINS is what lies in the recess. A placed niche takes one from its position - a wall of them
 * varies with no effort, and client and server agree on it without a roll - and an empty hand
 * moves on to the next. The default, what a structure gets when it names none, is the skull and
 * bones every niche had before there was a choice.
 *
 * <p>The shape is the solid block less the recess, so the wall still culls and collides as a wall
 * everywhere but the opening. Light is taken from that shape too ({@link #useShapeForLightOcclusion}):
 * a block that occludes as a full cube lets no light into its own cell, and the recess - drawn with
 * that cell's light - would render black.
 */
public class CatacombNicheBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<CatacombNicheBlock> CODEC = simpleCodec(CatacombNicheBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    /** What lies in the recess, each a group in the Blockbench project. */
    public enum Remains implements StringRepresentable {
        EMPTY(1), SKULL(2), BONES(2), SKULL_AND_BONES(3), SKULLS(2), SKULL_HEAP(2), SOUL_LAMP(0);

        /** How often a placed niche takes this, out of the sum over all of them. */
        private final int weight;

        Remains(int weight) {
            this.weight = weight;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        Remains next() {
            // the lamp is last and out of the round: it is lit with a soul torch, not rearranged to
            return values()[(ordinal() + 1) % SOUL_LAMP.ordinal()];
        }

        /** One by weight, from a position's hash: fixed per place, and different next door. */
        static Remains at(BlockPos pos) {
            int total = 0;
            for (Remains r : values()) {
                total += r.weight;
            }
            int pick = (int) Math.floorMod(Mth.getSeed(pos), (long) total);
            for (Remains r : values()) {
                pick -= r.weight;
                if (pick < 0) {
                    return r;
                }
            }
            return SKULL_AND_BONES;
        }
    }

    public static final EnumProperty<Remains> REMAINS = EnumProperty.create("remains", Remains.class);

    /** Monsters spawn only at block light 0, so this keeps them off just the two blocks nearest the lamp. */
    private static final int LAMP_LIGHT = 3;

    /** The block less its recess, x 2-14, y 2-14 and 12 deep, for each way the opening can face. */
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, cut(Block.box(2, 2, 0, 14, 14, 12)));
        SHAPES.put(Direction.SOUTH, cut(Block.box(2, 2, 4, 14, 14, 16)));
        SHAPES.put(Direction.WEST, cut(Block.box(0, 2, 2, 12, 14, 14)));
        SHAPES.put(Direction.EAST, cut(Block.box(4, 2, 2, 16, 14, 14)));
    }

    private static VoxelShape cut(VoxelShape recess) {
        return Shapes.join(Shapes.block(), recess, BooleanOp.ONLY_FIRST);
    }

    public CatacombNicheBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(REMAINS, Remains.SKULL_AND_BONES));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, REMAINS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(REMAINS, Remains.at(context.getClickedPos()));
    }

    public static int lightOf(BlockState state) {
        return state.getValue(REMAINS) == Remains.SOUL_LAMP ? LAMP_LIGHT : 0;
    }

    /**
     * An empty hand rearranges what lies in the niche, to the next of REMAINS, or takes the torch back
     * out of a lit lamp. A soul torch lights the lamp in place of the remains. Holding anything else
     * passes, so building against a niche still places the block.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild || !held.is(Items.SOUL_TORCH) || state.getValue(REMAINS) == Remains.SOUL_LAMP) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(REMAINS, Remains.SOUL_LAMP), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** An empty main hand: rearrange the remains, or take the torch back out of a lit lamp. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        boolean lit = state.getValue(REMAINS) == Remains.SOUL_LAMP;
        if (!player.getMainHandItem().isEmpty() || !player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (lit) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(REMAINS, Remains.EMPTY), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SOUL_TORCH));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(REMAINS, state.getValue(REMAINS).next()), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.BONE_BLOCK_HIT, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** A broken lamp gives its torch back along with the niche. */
    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropXp) {
        super.spawnAfterBreak(state, level, pos, tool, dropXp);
        if (state.getValue(REMAINS) == Remains.SOUL_LAMP) {
            popResource(level, pos, new ItemStack(Items.SOUL_TORCH));
        }
    }

    /** The odd soul flame rising off the lamp, from the middle of the recess. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(REMAINS) != Remains.SOUL_LAMP || random.nextInt(4) != 0) {
            return;
        }
        Direction facing = state.getValue(FACING);
        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5 + facing.getStepX() * 0.125, pos.getY() + 0.6,
                pos.getZ() + 0.5 + facing.getStepZ() * 0.125, 0.0, 0.005, 0.0);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
