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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.ToIntFunction;

/**
 * A pillar candle, knee-high at its largest, in three sizes. A player's candle is placed SMALL, and
 * each further Large Candle used on it makes it the next size up, as vanilla candles add a candle
 * to a group; breaking it gives back one for each. The default, what a structure gets when it names
 * no SIZE, is LARGE.
 *
 * <p>The models are blockbench/large_candle.bbmodel, a group per size; the sizes' dimensions here
 * must match LARGE_CANDLE_SIZES in tools/gen_bbmodels.py.
 *
 * @author Mark Gottschling on 10/22/2026
 */
public class LargeCandleBlock extends CandlePropBlock {
    public enum Size implements StringRepresentable {
        SMALL(4, 6, 1, 9), MEDIUM(4, 10, 2, 11), LARGE(6, 13, 2, 13);

        /** How many Large Candle items this size takes, and so gives back. */
        public final int candles;
        private final int light;
        private final VoxelShape shape;
        private final List<Vec3> flame;

        Size(int width, int height, int wick, int light) {
            this.candles = ordinal() + 1;
            this.light = light;
            double x0 = 8 - width / 2.0;
            this.shape = Block.box(x0, 0, x0, x0 + width, height, x0 + width);
            // the flame, as vanilla's, a pixel above the wick's tip
            this.flame = List.of(new Vec3(0.5, (height + wick + 1) / 16.0, 0.5));
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static final EnumProperty<Size> SIZE = EnumProperty.create("size", Size.class);
    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            state -> state.getValue(LIT) ? state.getValue(SIZE).light : 0;

    public static final MapCodec<LargeCandleBlock> CODEC = simpleCodec(LargeCandleBlock::new);

    public LargeCandleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SIZE, Size.LARGE).setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends AbstractCandleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE, LIT, WATERLOGGED);
    }

    /** Another Large Candle, used on this one, makes it bigger rather than going beside it. */
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(asItem()) && state.getValue(SIZE) != Size.LARGE
                || super.canBeReplaced(state, context);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) {
            return existing.setValue(SIZE, Size.values()[existing.getValue(SIZE).ordinal() + 1]);
        }
        return placed(defaultBlockState().setValue(SIZE, Size.SMALL), context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SIZE).shape;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return state.getValue(SIZE).flame;
    }
}
