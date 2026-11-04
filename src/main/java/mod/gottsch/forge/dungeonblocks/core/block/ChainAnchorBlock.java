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
import mod.gottsch.forge.dungeonblocks.core.blockentity.ChainAnchorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.EnumMap;
import java.util.Map;

/**
 * A block of dark iron plate that a giant or medium chain is fixed to. The chain runs from the centre of one
 * anchor to the centre of another, so it can leave by any face at any angle and the plate hides
 * where it starts. Connect two with a {@link mod.gottsch.forge.dungeonblocks.core.item.ChainItem};
 * shears cut every chain on an anchor, and breaking either end takes its chains down. Each chain
 * gives back the one chain item it took. Giant and medium chains ({@link ChainSize}) share the
 * anchor, and the same rules.
 *
 * <p><b>FACING and MIRRORED are not seen.</b> The plate looks the same every way round. They are
 * the frame the block entity's chain offsets are stored in, and exist so that a structure, which
 * turns block states but not block entity data, turns the chains with them. A placed anchor is
 * always NORTH, unmirrored, so its offsets are plain world offsets.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class ChainAnchorBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");

    /** The longest chain, centre to centre. A design choice: nothing breaks past it. */
    public static final double MAX_LENGTH = 32.0;

    public ChainAnchorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(MIRRORED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MIRRORED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChainAnchorBlockEntity(pos, state);
    }

    /**
     * Turns an offset stored in the anchor's frame into a world offset: mirrored first (z negated),
     * then turned from north to FACING.
     */
    public static BlockPos toWorld(BlockState state, BlockPos local) {
        BlockPos offset = state.getValue(MIRRORED) ? new BlockPos(local.getX(), local.getY(), -local.getZ()) : local;
        return offset.rotate(rotation(state.getValue(FACING)));
    }

    /** The inverse of {@link #toWorld}: a world offset into the anchor's frame. */
    public static BlockPos toLocal(BlockState state, BlockPos world) {
        BlockPos offset = world.rotate(inverse(rotation(state.getValue(FACING))));
        return state.getValue(MIRRORED) ? new BlockPos(offset.getX(), offset.getY(), -offset.getZ()) : offset;
    }

    private static Rotation inverse(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            default -> rotation;
        };
    }

    /** The turn that takes north to the facing. */
    private static Rotation rotation(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /**
     * A mirror turns the frame the other way and flips it. Mirror m, turn R, flip F: m R F equals
     * R⁻¹ m F, and for front-back m is a half turn after a left-right flip - so left-right takes
     * the facing to its east-west mirror, front-back to its north-south one, and both toggle the flip.
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        Direction facing = state.getValue(FACING);
        Direction turned = mirror == Mirror.LEFT_RIGHT ? Mirror.FRONT_BACK.mirror(facing) : Mirror.LEFT_RIGHT.mirror(facing);
        return state.setValue(FACING, turned).setValue(MIRRORED, !state.getValue(MIRRORED));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = stack;
        if (!held.is(Items.SHEARS) || !player.getAbilities().mayBuild
                || !(level.getBlockEntity(pos) instanceof ChainAnchorBlockEntity anchor) || !anchor.hasChains()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            cutChains(level, pos, anchor);
            level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.gameEvent(player, GameEvent.SHEAR, pos);
            held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Takes down every chain on this anchor, telling the anchor at each far end, and drops one chain
     * item of the chain's size for each. A far end that is not loaded is left as it is, and drops its half of the chain
     * the next time it loads ({@link #tick}).
     */
    private static void cutChains(Level level, BlockPos pos, ChainAnchorBlockEntity anchor) {
        Map<ChainSize, Integer> chains = new EnumMap<>(ChainSize.class);
        BlockPos target = anchor.getTarget();
        if (target != null) {
            if (level.isLoaded(target) && level.getBlockEntity(target) instanceof ChainAnchorBlockEntity end) {
                end.removeSource(pos);
            }
            chains.merge(anchor.getTargetSize(), 1, Integer::sum);
            anchor.clearTarget();
        }
        anchor.getSources().forEach((source, size) -> {
            if (level.isLoaded(source) && level.getBlockEntity(source) instanceof ChainAnchorBlockEntity start
                    && pos.equals(start.getTarget())) {
                start.clearTarget();
            }
            anchor.removeSource(source);
            chains.merge(size, 1, Integer::sum);
        });
        chains.forEach((size, count) -> drop(level, pos, new ItemStack(size.getItem(), count)));
    }

    private static void drop(Level level, BlockPos pos, ItemStack stack) {
        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    /**
     * Scheduled by the block entity's load: drops what no longer holds. A chain out to a block that is
     * no longer an anchor is cut, and its chain item dropped here; one that the far anchor has
     * forgotten is mended, so a structure need only record a chain at its start. A chain in from an
     * anchor that no longer points here is forgotten - its chain item went with the other end.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof ChainAnchorBlockEntity anchor)) {
            return;
        }
        BlockPos target = anchor.getTarget();
        if (target != null && level.isLoaded(target)) {
            if (level.getBlockEntity(target) instanceof ChainAnchorBlockEntity end) {
                end.addSource(pos, anchor.getTargetSize());
            } else {
                drop(level, pos, new ItemStack(anchor.getTargetSize().getItem()));
                anchor.clearTarget();
            }
        }
        for (BlockPos source : anchor.getSources().keySet()) {
            if (level.isLoaded(source) && !(level.getBlockEntity(source) instanceof ChainAnchorBlockEntity start
                    && pos.equals(start.getTarget()))) {
                anchor.removeSource(source);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof ChainAnchorBlockEntity anchor) {
                cutChains(level, pos, anchor);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}
