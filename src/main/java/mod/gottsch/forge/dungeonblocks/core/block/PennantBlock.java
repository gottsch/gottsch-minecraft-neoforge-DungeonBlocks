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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * A one-block wall banner — a pennant.
 *
 * <p>The same cloth as {@link DungeonBannerBlock}, cut short: 14px of drop under the rod instead of
 * 30, so the whole thing lives inside its own block. That makes it the simple case of the pair —
 * no {@code HALF}, no partner to place or destroy, no loot condition, and a render box that is just
 * this block. Everything it has in common with the tall banner is in {@link AbstractBannerBlock}.
 *
 * <p>The crest is not the tall banner's crest scaled down. Each faction's primary emblem is laid
 * out again for the shorter cloth and the secondary motif is dropped outright — see the
 * {@code compact} path in {@code tools/gen_banner_textures.py}, which is also where the shorter
 * texture comes from.
 *
 * @author Mark Gottschling on Sep 13, 2026
 */
public class PennantBlock extends AbstractBannerBlock {

	public PennantBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(ANIMATED, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ANIMATED);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return hasWall(state, level, pos);
	}

	/**
	 * Walking the looking directions rather than trusting {@code getClickedFace} is what lets a
	 * pennant still be placed when the click lands on a floor or ceiling next to a usable wall —
	 * vanilla's wall banner does the same. Unlike the tall banner there is no space below to check,
	 * so a pennant goes anywhere a wall does.
	 */
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis().isHorizontal()) {
				state = state.setValue(FACING, direction.getOpposite());
				if (hasWall(state, context.getLevel(), context.getClickedPos())) {
					return state;
				}
			}
		}
		return null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return newEntity(pos, state);
	}

	/** Nothing hangs below a pennant; the cloth stops at the bottom of its own block. */
	@Override
	public int blocksBelow() {
		return 0;
	}
}
