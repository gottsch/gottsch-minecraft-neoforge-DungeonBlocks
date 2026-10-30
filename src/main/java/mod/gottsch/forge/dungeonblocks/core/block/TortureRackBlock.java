/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * A rack: a dark oak frame three blocks long with a roller across each end. Three blocks, because
 * a skeleton at full size - skull, ribs, legs, and its arms stretched over its head - needs them;
 * squeezed into two it was squashed past reading. Placed as a bed is: FOOT where clicked, MIDDLE
 * and HEAD ahead along FACING, which is the way the player looked. Losing any part destroys the
 * rest, and the FOOT carries the drop.
 *
 * <p>A click turns the crank on the head roller a notch, and TENSION (0-2, on every part) steps
 * round: the crank turns, the ropes shorten and an occupant's arms and legs are drawn toward the
 * rollers. From the last notch the crank is let go, back to slack.
 *
 * <p>The occupied rack is this block with a skeleton in its models (blockbench/torture_rack_*.bbmodel,
 * groups skeleton, arms and legs): only datagen tells the two apart.
 */
public class TortureRackBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<TortureRackBlock> CODEC = simpleCodec(TortureRackBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public enum Part implements StringRepresentable {
        FOOT, MIDDLE, HEAD;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final IntegerProperty TENSION = IntegerProperty.create("tension", 0, 2);

    // the frame to the rails and the rollers' tops, the rack running along FACING
    private static final VoxelShape SHAPE_Z = Block.box(1, 0, 0, 15, 13, 16);
    private static final VoxelShape SHAPE_X = Block.box(0, 0, 1, 16, 13, 15);

    public TortureRackBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.FOOT).setValue(TENSION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, TENSION);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? SHAPE_Z : SHAPE_X;
    }

    /** Null, so no placement at all, unless both blocks ahead are free. */
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        Level level = context.getLevel();
        for (int i = 1; i <= 2; i++) {
            BlockPos p = context.getClickedPos().relative(facing, i);
            if (!level.getBlockState(p).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(p)) {
                return null;
            }
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        level.setBlock(pos.relative(facing), state.setValue(PART, Part.MIDDLE), 3);
        level.setBlock(pos.relative(facing, 2), state.setValue(PART, Part.HEAD), 3);
    }

    /** Each part needs its neighbours along the rack; losing one destroys the rest, as a unit. */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        Direction facing = state.getValue(FACING);
        Part part = state.getValue(PART);
        if (direction == facing && part != Part.HEAD) {
            return isPart(neighbour, Part.values()[part.ordinal() + 1], facing) ? state : Blocks.AIR.defaultBlockState();
        }
        if (direction == facing.getOpposite() && part != Part.FOOT) {
            return isPart(neighbour, Part.values()[part.ordinal() - 1], facing) ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    private boolean isPart(BlockState state, Part part, Direction facing) {
        return state.is(this) && state.getValue(PART) == part && state.getValue(FACING) == facing;
    }

    /** The FOOT's position, from any part. */
    private static BlockPos foot(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING).getOpposite(), state.getValue(PART).ordinal());
    }

    /**
     * In creative, the cascade from breaking another part would reach the FOOT with drops on and
     * hand the player a free rack: clear the FOOT with the no-drop flag first.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(PART) != Part.FOOT) {
            BlockPos foot = foot(pos, state);
            BlockState footState = level.getBlockState(foot);
            if (isPart(footState, Part.FOOT, state.getValue(FACING))) {
                level.setBlock(foot, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, foot, Block.getId(footState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            int tension = (state.getValue(TENSION) + 1) % 3;
            BlockPos foot = foot(pos, state);
            for (int i = 0; i < 3; i++) {
                BlockPos p = foot.relative(state.getValue(FACING), i);
                BlockState s = level.getBlockState(p);
                if (s.is(this)) {
                    level.setBlock(p, s.setValue(TENSION, tension), 10);
                }
            }
            // a ratchet's click and creak as it takes up; a thump as it runs back to slack
            if (tension == 0) {
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 0.6F);
            } else {
                level.playSound(null, pos, SoundEvents.CROSSBOW_LOADING_MIDDLE.value(), SoundSource.BLOCKS, 1.0F, 0.5F + 0.1F * tension);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
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
