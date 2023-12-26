/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.dungeonblocks.core.block;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.ToIntFunction;

/**
 * @author Mark Gottschling on Jan 18, 2020
 *
 */
public class SconceBlock extends AbstractSconceBlock {
	public static final int MIN_CANDLES = 0;
	public static final int MAX_CANDLES = 3;
	public static final int LIGHT_PER_CANDLE = 4;
	public static final IntegerProperty CANDLES = IntegerProperty.create("candles", MIN_CANDLES, MAX_CANDLES);

	public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> {
		return state.getValue(LIT) ? LIGHT_PER_CANDLE * state.getValue(CANDLES) : 0;
	};

	private static final VoxelShape NORTH_FACING_SHAPE = Block
			.box(2.0D, 2.0D, 9.0D, 14.0D, 15.0D, 16);
	private static final VoxelShape EAST_FACING_SHAPE = Block
			.box(0.0001D, 2.0D, 2.0D, 6.0D, 15.0D, 14.0D);
	private static final VoxelShape SOUTH_FACING_SHAPE = Block
			.box(2.0D, 2.0D, 0.0001D, 14.0D, 15.0D, 6.0D);
	private static final VoxelShape WEST_FACING_SHAPE = Block
			.box(9.0D, 2.0D, 2.0D, 16, 15.0D, 14.0D);

	/**
	 * 
	 * @param properties
	 */
	public SconceBlock(Properties properties) {

		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
						.setValue(LIT, Boolean.valueOf(false))
				.setValue(CANDLES, Integer.valueOf(0)));

	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CANDLES);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (player.getAbilities().mayBuild &&
				(player.getItemInHand(hand).is(Items.CANDLE) ||
						player.getItemInHand(hand).is(ItemTags.CANDLES))) {
			DungeonBlocks.LOGGER.info("placing candle, lit state -> {}", state.getValue(LIT));
			state = state.cycle(CANDLES);
			level.setBlock(pos, state, 11);
			level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
				p.broadcastBreakEvent(hand);
			});
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.use(state, level, pos, player, hand, hitResult);
	}

	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState blockState, FluidState fluidState) {
		if (!blockState.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			BlockState blockstate = blockState.setValue(WATERLOGGED, Boolean.valueOf(true));
			if (blockState.getValue(LIT)) {
				extinguish((Player)null, blockstate, level, pos);
			} else {
				level.setBlock(pos, blockstate, 3);
			}

			level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
			return true;
		} else {
			return false;
		}
	}

	// THIS DOESN'T WORK
	public BlockState getStateForPlacement(BlockPlaceContext context) {

		BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
			FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
			boolean flag = fluidstate.getType() == Fluids.WATER;
			return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag));
	}

	/**
	 * Called periodically clientside on blocks near the player to show effects
	 * (like furnace fire particles). Note that this method is unrelated to
	 * {randomTick} and {needsRandomTick}, and will always be
	 * called regardless of whether the block can receive random update ticks
	 */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		
		if (!state.getValue(LIT) || state.getValue(WATERLOGGED)) {
			return;
		}
		// 35% probability of animating
		float f = rand.nextFloat();
		if (f > 0.35F) {
			return;
		}
		if (f < 0.17F) {
			level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}
		Direction direction = state.getValue(FACING);
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.7D;
		double d2 = (double) pos.getZ() + 0.5D;
		double d3 = 0.37D; // y offset
		double d4 = 0.22D; // horizontal offset middle
		double d5 = (double) pos.getX() + 0.25D;
		double d6 = (double) pos.getZ() + 0.25D;
		double d7 = (double) pos.getX() + 0.75D;
		double d8 = (double) pos.getZ() + 0.75D;
		double d9 = 0.24D; // horizontal offset for side torches

		int candles = state.getValue(CANDLES);
		if (direction.getAxis().isHorizontal()) {
				Direction directionFacing = direction.getOpposite();
			if (candles == 1 || candles == 3) {
				// middle
				level.addParticle(ParticleTypes.SMOKE,
						d0 + d4 * (double) directionFacing.getStepX(), d1 + d3,
						d2 + d4 * (double) directionFacing.getStepZ(), 0.0D, 0.0D,
						0.0D);
				level.addParticle(ParticleTypes.SMALL_FLAME,
						d0 + d4 * (double) directionFacing.getStepX(), d1 + d3,
						d2 + d4 * (double) directionFacing.getStepZ(), 0.0D, 0.0D,
						0.0D);
			}

			if (candles == 2 || candles == 3) {
				// right
				if (directionFacing.getStepX() != 0) {
					level.addParticle(ParticleTypes.SMOKE,
							d0 + d9 * (double) directionFacing.getStepX(),
							d1 + d3,
							d6 + d9 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
					level.addParticle(ParticleTypes.SMALL_FLAME,
							d0 + d9 * (double) directionFacing.getStepX(),
							d1 + d3,
							d6 + d9 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
				} else {
					level.addParticle(ParticleTypes.SMOKE,
							d5 + d9 * (double) directionFacing.getStepX(),
							d1 + d3,
							d2 + d9 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
					level.addParticle(ParticleTypes.SMALL_FLAME,
							d5 + d9 * (double) directionFacing.getStepX(),
							d1 + d3,
							d2 + d9 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
				}

				// left
				if (directionFacing.getStepX() != 0) {
					level.addParticle(ParticleTypes.SMOKE,
							d0 + d4 * (double) directionFacing.getStepX(),
							d1 + d3,
							d8 + d4 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
					level.addParticle(ParticleTypes.SMALL_FLAME,
							d0 + d4 * (double) directionFacing.getStepX(),
							d1 + d3,
							d8 + d4 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
				} else {
					level.addParticle(ParticleTypes.SMOKE,
							d7 + d4 * (double) directionFacing.getStepX(),
							d1 + d3,
							d2 + d4 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
					level.addParticle(ParticleTypes.SMALL_FLAME,
							d7 + d4 * (double) directionFacing.getStepX(),
							d1 + d3,
							d2 + d4 * (double) directionFacing.getStepZ(), 0.0D,
							0.0D, 0.0D);
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);

		switch (direction) {
		case NORTH :
		default :
			return NORTH_FACING_SHAPE;
		case EAST :
			return EAST_FACING_SHAPE;
		case SOUTH :
			return SOUTH_FACING_SHAPE;
		case WEST :
			return WEST_FACING_SHAPE;
		}
	}
}
