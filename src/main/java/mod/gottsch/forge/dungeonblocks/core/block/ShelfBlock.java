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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * A stone wall shelf: a board of large brick on a stepped corbel, for tomes in a library or bones in
 * an ossuary. FACING is the way it looks out from its wall; clicking a side of an existing shelf
 * carries on that shelf's run instead of turning to the clicked face.
 *
 * <p>LEFT is which half of the large brick pair the board shows, taken from the position along the
 * wall, so a run reads as long bricks breaking joint every two blocks. The model is
 * blockbench/shelf.bbmodel, a template per CONTENTS that datagen fills with each brick.
 *
 * <p>CONTENTS starts empty, so a tome can lie on it, and holding a bone moves on to the next. An empty
 * hand does nothing, so a shelf in a library never fills with bones by accident.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class ShelfBlock extends WaterloggedNonCubeFacingBlock {
    /** What lies on the shelf, each a group in the Blockbench project. */
    public enum Contents implements StringRepresentable {
        EMPTY, BONES, SKULLS, SKULL_AND_BONES;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        Contents next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public static final EnumProperty<Contents> CONTENTS = EnumProperty.create("contents", Contents.class);
    public static final BooleanProperty LEFT = BooleanProperty.create("left");

    /** The board and the corbel under it, for each way the shelf can face. */
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            SHAPES.put(facing, Shapes.or(part(facing, 0, 4, 6, 16, 8, 16), part(facing, 5, 2, 10, 11, 4, 16),
                    part(facing, 5, 0, 13, 11, 2, 16)));
        }
    }

    /** A box as authored facing north, wall to the south, turned to {@code facing}. */
    private static VoxelShape part(Direction facing, double x0, double y0, double z0, double x1, double y1, double z1) {
        return switch (facing) {
            case SOUTH -> Block.box(16 - x1, y0, 16 - z1, 16 - x0, y1, 16 - z0);
            case WEST -> Block.box(z0, y0, 16 - x1, z1, y1, 16 - x0);
            case EAST -> Block.box(16 - z1, y0, x0, 16 - z0, y1, x1);
            default -> Block.box(x0, y0, z0, x1, y1, z1);
        };
    }

    public ShelfBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(CONTENTS, Contents.EMPTY).setValue(LEFT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONTENTS, LEFT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockState against = context.getLevel().getBlockState(context.getClickedPos().relative(face.getOpposite()));
        BlockState state = super.getStateForPlacement(context);
        Direction facing = against.getBlock() instanceof ShelfBlock ? against.getValue(FACING)
                : face.getAxis().isHorizontal() ? face : state.getValue(FACING);
        return state.setValue(FACING, facing).setValue(LEFT, isLeft(context.getClickedPos(), facing));
    }

    /** Every second block along the wall, the same on every course. */
    private static boolean isLeft(BlockPos pos, Direction facing) {
        return Math.floorMod(facing.getAxis() == Direction.Axis.Z ? pos.getX() : pos.getZ(), 2) == 1;
    }

    /** A bone in the hand rearranges what lies on the shelf. The bone is only a tool: it is not used up. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.BONE) || !player.getAbilities().mayBuild) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(CONTENTS, state.getValue(CONTENTS).next()), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.BONE_BLOCK_HIT, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // FacingBlock's FACING also has up and down: never placed, but their states exist and are cached
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPES.get(Direction.NORTH));
    }

    /** A mirrored run reads the other way along the wall, so its halves swap. */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        BlockState mirrored = super.mirror(state, mirror);
        return mirror == Mirror.NONE ? mirrored : mirrored.setValue(LEFT, !state.getValue(LEFT));
    }
}
