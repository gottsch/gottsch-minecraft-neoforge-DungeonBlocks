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

import net.minecraft.world.ItemInteractionResult;
import mod.gottsch.forge.dungeonblocks.core.blockentity.SwingingChainBlockEntity;
import mod.gottsch.forge.dungeonblocks.core.state.properties.ChainFixture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.Nullable;

/**
 * A decorative chain segment that sways — gently on its own, and properly when something walks
 * through it. Stack them to make a longer chain, exactly like vanilla chains; each segment is a real
 * block, so each one can be placed and broken on its own.
 *
 * <p><b>Only the top segment carries state.</b> {@link #TOP} is true for a segment with no chain
 * above it, and {@link #newBlockEntity} returns a BlockEntity <em>only</em> for those — so a chain of
 * any length costs exactly one {@link SwingingChainBlockEntity}, which holds the swing and renders
 * the whole run beneath it. The property is maintained in {@link #updateShape}, and
 * {@link #onPlace} clears the BlockEntity when a segment stops being the top (vanilla never removes
 * a BlockEntity across a same-block state change, and {@code onPlace} runs before BlockEntity
 * creation, so dropping it there sticks).
 *
 * <p><b>Nothing ticks.</b> Impulses arrive through {@link #entityInside}, which fires only when an
 * entity is actually within a segment — no per-tick scanning, and it identifies which segment was
 * struck, so a hit lower down the chain swings it harder.
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
public class SwingingChainBlock extends Block implements EntityBlock {

	/** True when nothing above this block is another chain — i.e. this segment owns the chain. */
	public static final BooleanProperty TOP = BooleanProperty.create("top");

	/**
	 * What hangs off this segment. Only ever set on the bottom segment (see
	 * {@link #canHoldFixture}). The fixture is drawn in <em>this</em> block's space, replacing its
	 * chain link — so the lantern you see is the block you can click and the block that emits the
	 * light.
	 */
	public static final EnumProperty<ChainFixture> FIXTURE = EnumProperty.create("fixture", ChainFixture.class);

	/** Only meaningful for a {@link ChainFixture#isLightable} fixture; ignored otherwise. */
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	/** Safety bound on how far the run-length walk and the renderer will follow a chain. */
	public static final int MAX_RUN = 24;

	// matches vanilla chain's 3px-wide core, so the highlight box sits on the links
	private static final VoxelShape SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);

	public SwingingChainBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(TOP, true)
				.setValue(FIXTURE, ChainFixture.NONE)
				.setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TOP, FIXTURE, LIT);
	}

	/** Block light for a chain, which comes entirely from whatever fixture is attached. */
	public static int lightEmission(BlockState state) {
		return state.getValue(FIXTURE).lightLevel(state.getValue(LIT));
	}

	/**
	 * The chain is drawn entirely by {@code SwingingChainRenderer} — it swings, so it can't be baked
	 * into the chunk mesh. Same approach vanilla takes for the end gateway.
	 */
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	/** Walk-through: you're meant to be able to push through a chain, which is what makes it swing. */
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return true;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(TOP,
				!context.getLevel().getBlockState(context.getClickedPos().above()).is(this));
	}

	/**
	 * A chain has to hang from something: another chain, or a solid face above it. Breaking a link
	 * mid-run therefore drops everything below it rather than leaving chain floating in mid-air.
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos above = pos.above();
		BlockState supporting = level.getBlockState(above);
		return supporting.is(this)
				|| supporting.is(Blocks.CHAIN)
				|| supporting.isFaceSturdy(level, above, Direction.DOWN);
	}

	/** Keeps {@link #TOP} honest as chains are stacked onto or broken off the top of this one. */
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP) {
			state = state.setValue(TOP, !neighborState.is(this));
		}
		if (!state.canSurvive(level, pos)) {
			// scheduled rather than returned as air so the chain and its fixture actually drop.
			// Each break cascades to the link below on the next tick, so a long chain visibly
			// unzips downward — the same idiom vanilla scaffolding uses.
			level.scheduleTick(pos, this, 1);
		}
		return state;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!state.canSurvive(level, pos)) {
			level.destroyBlock(pos, true);
		}
	}

	/**
	 * Drops the BlockEntity from a segment that just stopped being the top. Vanilla's own removal
	 * only fires when the block type changes, so a top -> not-top flip would otherwise strand it.
	 * This runs before vanilla's BlockEntity creation step, and {@link #newBlockEntity} declines to
	 * make one for a non-top state, so the removal isn't immediately undone.
	 */
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!state.getValue(TOP) && level.getBlockEntity(pos) != null) {
			level.removeBlockEntity(pos);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		// one BlockEntity per chain, not per segment
		return state.getValue(TOP) ? new SwingingChainBlockEntity(pos, state) : null;
	}

	/**
	 * Fires when something is inside this segment. Vanilla calls this for every block overlapping an
	 * entity's bounding box regardless of collision shape, which is why the chain can be
	 * non-colliding and still notice you.
	 */
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide || !(entity instanceof LivingEntity living) || !living.isAlive()) {
			return;
		}
		BlockPos topPos = findTop(level, pos);
		if (!(level.getBlockEntity(topPos) instanceof SwingingChainBlockEntity chain)) {
			return;
		}
		// how far down the run the hit landed: lower means more leverage on the pivot
		int depth = topPos.getY() - pos.getY();
		chain.nudge(living, depth);
	}

	/**
	 * Attach, light, extinguish or detach a fixture. The cascade mirrors
	 * {@code DungeonLanternBlock#use} so a hanging dungeon lantern behaves the same as a placed one:
	 * torch or flint and steel lights it, an empty hand extinguishes it, and an empty hand on an
	 * unlit (or non-lightable) fixture takes it back off.
	 */
	@Override
	protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ChainFixture fixture = state.getValue(FIXTURE);

		if (fixture.isLightable() && !state.getValue(LIT)
				&& (held.is(Items.FLINT_AND_STEEL) || held.is(Blocks.TORCH.asItem()))) {
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
				if (held.is(Items.FLINT_AND_STEEL)) {
					held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		if (!fixture.isPresent()) {
			ChainFixture attaching = fixtureFor(held);
			if (!attaching.isPresent() || !canHoldFixture(level, pos)) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(FIXTURE, attaching).setValue(LIT, false), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.7F, 1.1F);
				if (!player.getAbilities().instabuild) {
					held.shrink(1);
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	/** An empty main hand puts a lit fixture out, or else takes the fixture off. */
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		ChainFixture fixture = state.getValue(FIXTURE);
		if (!player.getMainHandItem().isEmpty() || !fixture.isPresent()) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide) {
			if (fixture.isLightable() && state.getValue(LIT)) {
				level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.5F);
			} else {
				detachFixture(state, level, pos);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	/** Pops the fixture back into the world and clears it from the state. */
	private static void detachFixture(BlockState state, Level level, BlockPos pos) {
		popResource(level, pos, dropFor(state.getValue(FIXTURE)));
		level.setBlock(pos, state.setValue(FIXTURE, ChainFixture.NONE).setValue(LIT, false), Block.UPDATE_ALL);
		level.playSound(null, pos, SoundEvents.CHAIN_HIT, SoundSource.BLOCKS, 0.7F, 0.9F);
	}

	/**
	 * Fixtures belong at the end of a chain, and the fixture is drawn in this block's own space
	 * (replacing its chain link), so all that's required is that nothing hangs below.
	 */
	private boolean canHoldFixture(Level level, BlockPos pos) {
		return !level.getBlockState(pos.below()).is(this);
	}

	private static ChainFixture fixtureFor(ItemStack stack) {
		if (stack.is(Items.LANTERN)) {
			return ChainFixture.LANTERN;
		}
		if (stack.is(Items.SOUL_LANTERN)) {
			return ChainFixture.SOUL_LANTERN;
		}
		if (stack.is(ModBlocks.DUNGEON_LANTERN.get().asItem())) {
			return ChainFixture.DUNGEON_LANTERN;
		}
		if (stack.is(ModBlocks.MANACLES.get().asItem())) {
			return ChainFixture.MANACLES;
		}
		if (stack.is(ModBlocks.MEAT_HOOK.get().asItem())) {
			return ChainFixture.MEAT_HOOK;
		}
		if (stack.is(ModBlocks.CENSER.get().asItem())) {
			return ChainFixture.CENSER;
		}
		return ChainFixture.NONE;
	}

	private static ItemStack dropFor(ChainFixture fixture) {
		return switch (fixture) {
			case LANTERN -> new ItemStack(Items.LANTERN);
			case SOUL_LANTERN -> new ItemStack(Items.SOUL_LANTERN);
			case DUNGEON_LANTERN -> new ItemStack(ModBlocks.DUNGEON_LANTERN.get());
			case MANACLES -> new ItemStack(ModBlocks.MANACLES.get());
			case MEAT_HOOK -> new ItemStack(ModBlocks.MEAT_HOOK.get());
			case CENSER -> new ItemStack(ModBlocks.CENSER.get());
			case NONE -> ItemStack.EMPTY;
		};
	}

	/**
	 * Extending the chain below a fixture would leave the fixture stranded mid-run, where it isn't
	 * drawn — so pop it off instead of letting it silently vanish.
	 */
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide && state.getValue(FIXTURE).isPresent()
				&& fromPos.equals(pos.below()) && level.getBlockState(pos.below()).is(this)) {
			detachFixture(state, level, pos);
		}
	}

	/** Breaking the segment drops its fixture too; the loot table only covers the chain itself. */
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!level.isClientSide && !newState.is(this) && state.getValue(FIXTURE).isPresent()) {
			popResource(level, pos, dropFor(state.getValue(FIXTURE)));
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	/**
	 * A lit censer smokes. The smoke rises from where the censer is drawn - swung aside, the smoke
	 * goes with it - which {@link SwingingChainBlockEntity#bottomPoint} works out from the same swing
	 * the renderer draws.
	 */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(FIXTURE) != ChainFixture.CENSER || !state.getValue(LIT)) {
			return;
		}
		if (!(level.getBlockEntity(findTop(level, pos)) instanceof SwingingChainBlockEntity chain)) {
			return;
		}
		Vec3 at = chain.bottomPoint(level.getGameTime(), CenserBlock.smokeOrigin(random));
		level.addParticle(ParticleTypes.SMOKE, at.x, at.y, at.z, 0.0D, 0.015D, 0.0D);
		if (random.nextInt(4) == 0) {
			level.addParticle(ParticleTypes.SMOKE, at.x, at.y + 0.1D, at.z, 0.0D, 0.02D, 0.0D);
		}
	}

	/** The bottom segment of the chain owned by {@code topPos} — the one that can carry a fixture. */
	public static BlockState bottomSegment(BlockGetter level, BlockPos topPos) {
		return level.getBlockState(topPos.below(runLength(level, topPos) - 1));
	}

	/** Walks up to the segment that owns this chain (the one holding the BlockEntity). */
	public static BlockPos findTop(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof SwingingChainBlock) || state.getValue(TOP)) {
			return pos;
		}
		BlockPos.MutableBlockPos cursor = pos.mutable();
		for (int i = 0; i < MAX_RUN; i++) {
			cursor.move(Direction.UP);
			BlockState above = level.getBlockState(cursor);
			if (!(above.getBlock() instanceof SwingingChainBlock)) {
				return cursor.move(Direction.DOWN).immutable();
			}
			if (above.getValue(TOP)) {
				return cursor.immutable();
			}
		}
		return cursor.immutable();
	}

	/** How many contiguous chain segments hang from {@code topPos}, including it. */
	public static int runLength(BlockGetter level, BlockPos topPos) {
		int length = 1;
		BlockPos.MutableBlockPos cursor = topPos.mutable();
		while (length < MAX_RUN) {
			cursor.move(Direction.DOWN);
			if (!(level.getBlockState(cursor).getBlock() instanceof SwingingChainBlock)) {
				break;
			}
			length++;
		}
		return length;
	}
}
