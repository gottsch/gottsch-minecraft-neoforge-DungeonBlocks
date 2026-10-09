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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * What every hanging banner shares, whatever its height.
 *
 * <p>Two subclasses: {@link DungeonBannerBlock} is the two-block banner, {@link PennantBlock} the
 * one-block one. The split is at the block level rather than a flag on one class because the
 * difference is structural, not cosmetic — a pennant has no {@code HALF} property at all, so it has
 * no partner half to place, break, keep in step or condition a loot table on. Folding that into one
 * class would mean a null-partner branch in every one of those methods.
 *
 * <p>Everything else really is common: the facing, the animation flag and the toggle that flips it,
 * the wall-hugging hitbox, walk-through collision, and the fact that a waving cloth cannot be baked
 * into the chunk mesh. Both share one BlockEntity type and one renderer as well; only the model
 * layer differs.
 *
 * @author Mark Gottschling on Sep 13, 2026
 */
public abstract class AbstractBannerBlock extends Block implements EntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	/**
	 * Whether this banner's cloth moves. Per-banner, toggled with an empty hand. The client config
	 * can veto it globally but cannot force a still banner to move — see {@code DungeonBannerRenderer}.
	 */
	public static final BooleanProperty ANIMATED = BooleanProperty.create("animated");

	/**
	 * A thin slab covering the cloth and rod, hugging the wall the banner hangs on. FACING is the
	 * direction the banner looks, so the supporting wall is always at {@code FACING.getOpposite()},
	 * and each shape sits against that side of the block. The rod is only marginally wider than the
	 * cloth, and a hitbox that changed with the geometry would just make a banner awkward to aim at.
	 */
	protected static final Map<Direction, VoxelShape> SHAPES = Map.of(
			Direction.NORTH, Block.box(2.0D, 0.0D, 12.0D, 14.0D, 16.0D, 16.0D),
			Direction.SOUTH, Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 4.0D),
			Direction.WEST, Block.box(12.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D),
			Direction.EAST, Block.box(0.0D, 0.0D, 2.0D, 4.0D, 16.0D, 14.0D));

	protected AbstractBannerBlock(Properties properties) {
		super(properties);
	}

	/**
	 * The cloth waves, so it cannot be baked into the chunk mesh — {@code DungeonBannerRenderer}
	 * draws all of it. Same call the swinging chain makes, for the same reason.
	 */
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	/** Cloth: you walk through it, exactly as you walk through a vanilla banner. */
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	/** True when the block behind this position can be hung from. */
	protected static boolean hasWall(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isSolid();
	}

	/**
	 * Empty hand toggles this banner's motion. There is nothing else to do to a banner, so a plain
	 * right-click is unambiguous, and holding anything passes so the click places the held block
	 * instead of silently freezing the banner.
	 */
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.getMainHandItem().isEmpty()) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide) {
			boolean animated = !state.getValue(ANIMATED);
			setAnimated(level, pos, state, animated);
			level.playSound(null, pos, SoundEvents.WOOL_HIT, SoundSource.BLOCKS,
					0.6F, animated ? 1.2F : 0.8F);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	/**
	 * Writes the flag back. A one-block banner is just this position; the two-block one overrides to
	 * carry it to its other half as well.
	 */
	protected void setAnimated(Level level, BlockPos pos, BlockState state, boolean animated) {
		level.setBlock(pos, state.setValue(ANIMATED, animated), Block.UPDATE_ALL);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	/**
	 * How far below this block the renderer draws. The BlockEntity reads it for its render bounding
	 * box: get it wrong and the banner is culled the moment this block leaves the frustum, while you
	 * are still looking straight at the part hanging below it.
	 */
	public abstract int blocksBelow();

	/** Both shapes are drawn by one renderer; this is what tells it which model to bake. */
	public boolean isPennant() {
		return blocksBelow() == 0;
	}

	/**
	 * Convenience for the renderer and the BlockEntity, which are handed a plain BlockState.
	 * Returns null when the block is not a banner at all.
	 */
	public static AbstractBannerBlock of(BlockState state) {
		return state.getBlock() instanceof AbstractBannerBlock banner ? banner : null;
	}

	/** Present so subclasses do not have to import it; both create the same stateless entity. */
	protected static DungeonBannerBlockEntity newEntity(BlockPos pos, BlockState state) {
		return new DungeonBannerBlockEntity(pos, state);
	}
}
