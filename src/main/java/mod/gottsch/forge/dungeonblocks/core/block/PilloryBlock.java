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

import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A pillory: two dark oak posts holding a board with a neck hole and two wrist holes. Placed,
 * broken and opened as the iron maiden is - two halves, LOWER where clicked, and a click lifts the
 * top of the board (OPEN) or drops it shut. FACING is the way the board's front, the side a
 * prisoner's head comes through, looks; it is placed facing the player.
 *
 * <p>The occupied pillory is this block with a skeleton in its models (blockbench/pillory_*.bbmodel,
 * group skeleton): only datagen tells the two apart.
 */
public class PilloryBlock extends IronMaidenBlock {
	public static final MapCodec<PilloryBlock> CODEC = simpleCodec(PilloryBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    // authored facing north: the frame and the prisoner behind it lie at z 5-15, the front at z 5
    private static final VoxelShape NORTH = Block.box(0, 0, 5, 16, 16, 15);
    private static final VoxelShape SOUTH = Block.box(0, 0, 1, 16, 16, 11);
    private static final VoxelShape EAST = Block.box(1, 0, 0, 11, 16, 16);
    private static final VoxelShape WEST = Block.box(5, 0, 0, 15, 16, 16);

    public PilloryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return facing == Direction.SOUTH ? SOUTH : facing == Direction.EAST ? EAST : facing == Direction.WEST ? WEST : NORTH;
    }

    /** A wooden board, dropped or lifted. */
    @Override
    protected void playToggleSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, open ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE,
                SoundSource.BLOCKS, 1.0F, 0.8F);
    }
}
