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

import mod.gottsch.forge.dungeonblocks.core.blockentity.DungeonBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A two-block wall banner with a single baked-in design, tapering to a point.
 *
 * <p><b>Not a vanilla banner.</b> None of vanilla's pattern machinery is here - no layers, no dyes,
 * no {@code BannerPattern} tags, no loom interaction. The cloth carries exactly one design, which
 * lives in the texture, so the whole thing costs two blocks, one stateless BlockEntity and one
 * renderer. That is the entire point: a dungeon dressing prop, not a heraldry system.
 *
 * <p><b>Only the upper half carries the BlockEntity</b>, and therefore all of the geometry: the rod
 * sits at the top of the upper block and the cloth is drawn hanging 30px down through both. That is
 * the same split {@link SwingingChainBlock} uses - one renderer draws the whole run - and it means
 * a banner costs one BlockEntity, not two. {@code DungeonBannerBlockEntity#getRenderBoundingBox}
 * widens the render box to match, or the lower half would vanish the moment the upper block left
 * the frustum.
 *
 * <p><b>Only the upper half needs a wall.</b> A banner hangs from its rod, so the lower half asks
 * nothing of the block behind it and a banner can hang over a doorway or into a stairwell. Breaking
 * either half, or the one wall block the upper half hangs on, takes the whole banner down - and
 * exactly one item drops, because the loot table is conditioned on the upper half and the surviving
 * half is destroyed through {@link #updateShape}, which drops.
 *
 * <p><b>Nothing ticks and nothing is stored.</b> The cloth's wave is a closed-form function of game
 * time and block position, evaluated client-side in {@code DungeonBannerRenderer}, the same trick
 * the swinging chain uses.
 *
 * @author Mark Gottschling on Sep 12, 2026
 */
public class DungeonBannerBlock extends AbstractBannerBlock {

	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;


	public DungeonBannerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(HALF, DoubleBlockHalf.UPPER)
				.setValue(ANIMATED, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF, ANIMATED);
	}






	/**
	 * The upper half hangs on a wall; the lower half hangs on the upper half. So a banner needs a
	 * wall only at the top, which is what lets one hang over a doorway, and knocking that single wall
	 * block out still brings the whole banner down.
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return hasWall(state, level, pos);
		}
		BlockState above = level.getBlockState(pos.above());
		return above.is(this) && above.getValue(HALF) == DoubleBlockHalf.UPPER;
	}

	/**
	 * The clicked block becomes the <em>upper</em> half and the banner hangs down from it, so the rod
	 * ends up where the player aimed. That needs the space below free as well as a wall behind, and
	 * returning null when either is missing is what makes the item a no-op instead of placing a
	 * broken half.
	 *
	 * <p>Walking the looking directions rather than trusting {@code getClickedFace} is what lets a
	 * banner still be placed when the click lands on a floor or ceiling next to a usable wall -
	 * vanilla's wall banner does the same.
	 */
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelReader level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (pos.getY() <= level.getMinBuildHeight()
				|| !level.getBlockState(pos.below()).canBeReplaced(context)) {
			return null;
		}

		BlockState state = this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER);
		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis().isHorizontal()) {
				state = state.setValue(FACING, direction.getOpposite());
				if (hasWall(state, level, pos)) {
					return state;
				}
			}
		}
		return null;
	}


	/**
	 * Carries the flag to the other half too. Only the upper half's state is ever read for
	 * rendering, but leaving the lower half stale would make the two disagree in F3 and in any
	 * structure or datapack that reads them.
	 */
	@Override
	protected void setAnimated(Level level, BlockPos pos, BlockState state, boolean animated) {
		super.setAnimated(level, pos, state, animated);
		BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		BlockState otherState = level.getBlockState(otherPos);
		if (otherState.is(this) && otherState.getValue(HALF) != state.getValue(HALF)) {
			level.setBlock(otherPos, otherState.setValue(ANIMATED, animated), Block.UPDATE_ALL);
		}
	}

	/**
	 * Fills in the lower half once the placed upper half is in the world. ANIMATED rides along with
	 * the copied state, so both halves agree from the moment the banner exists.
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
			ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		level.setBlock(pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER), Block.UPDATE_ALL);
	}

	/**
	 * Takes the banner down as a unit. Returning AIR from here rather than clearing the partner by
	 * hand is what keeps the drop right: the engine turns a returned AIR into {@code destroyBlock}
	 * <em>with</em> drops, so breaking the lower half still yields one banner, off the upper half's
	 * loot condition.
	 */
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		boolean towardPartner = direction == (half == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP);

		if (towardPartner) {
			if (!neighborState.is(this) || neighborState.getValue(HALF) == half) {
				return Blocks.AIR.defaultBlockState();
			}
		} else if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	/**
	 * In survival the surviving half is destroyed by {@link #updateShape} and the loot table decides
	 * what drops. Creative has to clear it explicitly with the no-drop flag, because that cascade
	 * drops by design and the upper half's loot condition would otherwise be satisfied - handing the
	 * player a free banner for breaking one in creative. Same fix, and the same reason, as
	 * {@code SkeletonBlock}.
	 */
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
			BlockState otherState = level.getBlockState(otherPos);
			if (otherState.is(this) && otherState.getValue(HALF) != state.getValue(HALF)) {
				level.setBlock(otherPos, Blocks.AIR.defaultBlockState(),
						Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
				level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}



	/**
	 * Upper half only - it owns the rod and draws the entire banner, both halves' worth. The lower
	 * half is geometry-free, so a BlockEntity there would be pure overhead.
	 */
	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? newEntity(pos, state) : null;
	}

	/** The cloth hangs a full block below the half that owns the BlockEntity. */
	@Override
	public int blocksBelow() {
		return 1;
	}
}
