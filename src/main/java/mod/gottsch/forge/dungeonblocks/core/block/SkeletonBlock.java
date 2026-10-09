/*
 * This file is part of Treasure2.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the Open Software Licence 3.0.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Open Software Licence 3.0 for more details.
 *
 * You should have received a copy of the Open Software Licence
 * along with Treasure2. If not, see <https://www.tldrlegal.com/license/open-software-licence-3-0>.
 */
package mod.gottsch.forge.dungeonblocks.core.block;

import mod.gottsch.neo.gottschcore.block.FacingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * @author Mark Gottschling on Feb 2, 2019
 *
 */
public class SkeletonBlock extends FacingBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/*
	 * An array of VoxelShape shapes for the bounding box
	 */
	private VoxelShape[] bounds = new VoxelShape[4];

	public static final EnumProperty<EnumPartType> PART = EnumProperty.<EnumPartType>create("part", EnumPartType.class);

	public SkeletonBlock(Block.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(WATERLOGGED, Boolean.valueOf(false))
				.setValue(PART, EnumPartType.BOTTOM));

		// the sprawl is 14 wide across the body and 16 along its length, so the footprint swaps
		// axes on the east/west facings rather than being the same box four times
		VoxelShape lengthwiseZ = Block.box(1, 0, 0, 15, 6, 16);
		VoxelShape lengthwiseX = Block.box(0, 0, 1, 16, 6, 15);
		setBounds(
				new VoxelShape[] {
						lengthwiseZ, 	// N
						lengthwiseX,  	// E
						lengthwiseZ,  	// S
						lengthwiseX	// W
				});
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED, PART);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch(state.getValue(FACING)) {
			default:
			case NORTH:
				return bounds[0];
			case EAST:
				return bounds[1];
			case SOUTH:
				return bounds[2];
			case WEST:
				return bounds[3];
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = this.defaultBlockState().setValue(FACING,
				context.getHorizontalDirection().getOpposite());
		blockState = blockState.setValue(WATERLOGGED, Waterlogging.placedInWater(context));
		return blockState;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos pos, BlockPos p_56930_) {
		Waterlogging.tickWater(state, levelAccessor, pos);
		return super.updateShape(state, direction, newState, levelAccessor, pos, p_56930_);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Waterlogging.fluid(blockState, super.getFluidState(blockState));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	/**
	 * called by ItemBlocks after a block is set in the world, to allow post-place logic
	 * ie. after the bottom/feet has been placed
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide) {
			BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());

			FluidState otherFluidState = level.getFluidState(blockPos);
			boolean isWaterAtOther = otherFluidState.getType() == Fluids.WATER;

			level.setBlock(blockPos, state.setValue(PART, EnumPartType.TOP).setValue(WATERLOGGED, isWaterAtOther), 3);
			level.blockUpdated(pos, Blocks.AIR);
			state.updateNeighbourShapes(level, pos, 3);
		}
	}

	/**
	 * Called before the Block is set to air in the world. Called regardless of if
	 * the player's tool can actually collect this block
	 */
	/**
	 * Destroying one half destroys the other, so the pair always breaks as a unit.
	 *
	 * <p>The {@code !newState.is(this)} guard is essential: the server calls {@code onRemove} for
	 * <em>every</em> state change at a position, not only removals, so without it any same-block
	 * property change ran the cascade. Waterlogging is exactly such a change - filling a skeleton
	 * with water destroyed both halves instead of flooding them.
	 */
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!newState.is(this)) {
			BlockPos blockPos = pos.relative(towardOtherHalf(state));
			if (level.getBlockState(blockPos).getBlock() == this) {
				Block.updateOrDestroy(state, Blocks.AIR.defaultBlockState(), level, blockPos, 3);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	/**
	 * In survival the surviving half is destroyed by {@link #onRemove} and the loot table decides
	 * what drops. Creative has to clear it explicitly with the no-drop flag, because that cascade
	 * runs {@code destroyBlock} with drops enabled and the BOTTOM's loot condition would otherwise
	 * be satisfied - handing the player a free skeleton for breaking one in creative.
	 */
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			BlockPos otherPos = pos.relative(towardOtherHalf(state));
			BlockState otherState = level.getBlockState(otherPos);
			if (otherState.is(this) && otherState.getValue(PART) != state.getValue(PART)) {
				level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	/**
	 * Direction from this half toward its partner. The BOTTOM is the placed half and FACING points
	 * back at the player, so its partner sits the other way.
	 */
	private static Direction towardOtherHalf(BlockState state) {
		Direction facing = state.getValue(FACING);
		return state.getValue(PART) == EnumPartType.BOTTOM ? facing.getOpposite() : facing;
	}

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
   }

	public VoxelShape[] getBounds() {
		return bounds;
	}
	public SkeletonBlock setBounds(VoxelShape[] bounds) {
		this.bounds = bounds;
		return this;
	}

	/**
	 * 
	 * @author Mark Gottschling on Feb 2, 2019
	 *
	 */
	public static enum EnumPartType implements StringRepresentable {
		TOP("top"), BOTTOM("bottom");

		private final String name;

		private EnumPartType(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

}