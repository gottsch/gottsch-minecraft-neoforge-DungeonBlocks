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

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * A gibbet: a full-width iron cage three blocks tall with a full-size skeleton hanging in it
 * (blockbench/gibbet_*.bbmodel). Every block of it is a full 16x16 cage section, so the skeleton
 * has room for other poses later. A prop; FACING is the way the skeleton faces, toward the player.
 *
 * <p>PART is BOTTOM, MIDDLE or TOP. Clicked onto the underside of a block - a ceiling, or a
 * chain - it hangs: the top part goes where it was placed and the other two below it, so its
 * chain meets whatever it hangs from. Clicked onto any other face it stands, the bottom part
 * where it was placed and the other two above. Either way all three spaces must be free; losing
 * any part destroys the rest; only the bottom part drops the item.
 */
public class GibbetBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<GibbetBlock> CODEC = simpleCodec(GibbetBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public enum Part implements StringRepresentable {
        BOTTOM, MIDDLE, TOP;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        Part above() {
            return values()[ordinal() + 1];
        }

        Part below() {
            return values()[ordinal() - 1];
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public GibbetBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, Part.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        // hung from the underside of whatever was clicked, it grows down; stood, it grows up
        boolean hanging = context.getClickedFace() == Direction.DOWN;
        Direction grows = hanging ? Direction.DOWN : Direction.UP;
        for (int i = 1; i <= 2; i++) {
            BlockPos p = pos.relative(grows, i);
            if (level.isOutsideBuildHeight(p) || !level.getBlockState(p).canBeReplaced(context)) {
                return null;
            }
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(PART, hanging ? Part.TOP : Part.BOTTOM);
    }

    /** Fills in the other two parts, below a hung gibbet's top or above a stood one's bottom. */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (state.getValue(PART) == Part.TOP) {
            level.setBlock(pos.below(), state.setValue(PART, Part.MIDDLE), 3);
            level.setBlock(pos.below(2), state.setValue(PART, Part.BOTTOM), 3);
        } else {
            level.setBlock(pos.above(), state.setValue(PART, Part.MIDDLE), 3);
            level.setBlock(pos.above(2), state.setValue(PART, Part.TOP), 3);
        }
    }

    /** Each part needs its neighbours in the stack; losing one destroys the rest, as a unit. */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        Part part = state.getValue(PART);
        if (direction == Direction.UP && part != Part.TOP) {
            return isPart(neighbour, part.above()) ? state : Blocks.AIR.defaultBlockState();
        }
        if (direction == Direction.DOWN && part != Part.BOTTOM) {
            return isPart(neighbour, part.below()) ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    private boolean isPart(BlockState state, Part part) {
        return state.is(this) && state.getValue(PART) == part;
    }

    /**
     * In creative, the cascade from breaking an upper part would reach the bottom part with drops
     * on and hand the player a free gibbet: clear the bottom with the no-drop flag first.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        Part part = state.getValue(PART);
        if (!level.isClientSide && player.isCreative() && part != Part.BOTTOM) {
            BlockPos bottom = pos.below(part.ordinal());
            BlockState bottomState = level.getBlockState(bottom);
            if (isPart(bottomState, Part.BOTTOM)) {
                level.setBlock(bottom, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, bottom, Block.getId(bottomState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
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
