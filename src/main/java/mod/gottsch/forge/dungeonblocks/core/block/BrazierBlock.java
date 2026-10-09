package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class BrazierBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    /**
     * What is burning in the bowl, and therefore how much light it gives off. One property rather
     * than a lit/soul pair so that every look the brazier can have is exactly one value - dungeon
     * generators author these as literal blockstate strings and cannot reason about combinations.
     *
     * <p>The default is {@link BrazierFire#NONE} on purpose: a datapack that misspells the property
     * name has it silently ignored, and failing to a dark brazier is obviously wrong in a way that
     * failing to a lit one is not.
     */
    public static final EnumProperty<BrazierFire> FIRE = EnumProperty.create("fire", BrazierFire.class);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.getValue(FIRE).getLightLevel();

    protected static final VoxelShape AABB = Block.box(2D, 4D, 2D, 14D, 15D, 14D);

    public BrazierBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FIRE, BrazierFire.NONE)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((builder));
        builder.add(FIRE).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FIRE, BrazierFire.NONE)
                .setValue(WATERLOGGED, Waterlogging.placedInWater(context));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // empty hand only: useWithoutItem also runs for an item that useItemOn passed on
        if (player.getAbilities().mayBuild && player.getMainHandItem().isEmpty() && state.getValue(FIRE) != BrazierFire.NONE) {
            extinguish(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().mayBuild &&
                (stack.is(Blocks.TORCH.asItem()) ||
                        stack.is(Blocks.SOUL_TORCH.asItem()) ||
                        stack.is(Items.FLINT_AND_STEEL))
                && canBeLit(state)
                && !state.getValue(WATERLOGGED)
        ) {
            // the igniter decides the fire: only a soul torch burns soul fire.
            boolean soul = stack.is(Blocks.SOUL_TORCH.asItem());
            setFire(level, state, pos, soul ? BrazierFire.SOUL : BrazierFire.LIT);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (stack.is(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BrazierFire fire = state.getValue(FIRE);
        if (fire == BrazierFire.NONE) {
            return;
        }

        if (fire == BrazierFire.EMBERS) {
            // coals only: a slow curl of smoke and the occasional pop, no flame particles and no
            // crackle loud enough to read as a full fire.
            if (random.nextInt(20) == 0) {
                level.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.5D + random.nextDouble() / 4.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.5D + random.nextDouble() / 4.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
            }
            return;
        }

        if (random.nextInt(10) == 0) {
            level.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            level.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.5D + random.nextDouble() / 4.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.5D + random.nextDouble() / 4.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
        }

        if (random.nextInt(5) == 0) {
            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                if (fire == BrazierFire.SOUL) {
                    // soul flames rise in place instead of arcing outward like lava spits
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) / 3.0D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) / 3.0D, 0.0D, 5.0E-5D, 0.0D);
                } else {
                    level.addParticle(ParticleTypes.LAVA, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)(random.nextFloat() / 2.0F), 5.0E-5D, (double)(random.nextFloat() / 2.0F));
                }
            }
        }
    }

    public VoxelShape getShape(BlockState p_153474_, BlockGetter p_153475_, BlockPos p_153476_, CollisionContext p_153477_) {
        return AABB;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            boolean wasBurning = state.getValue(FIRE) != BrazierFire.NONE;
            // one setBlock carries the waterlogging and the drowning of the fire together
            level.setBlock(pos, state.setValue(WATERLOGGED, Boolean.valueOf(true))
                    .setValue(FIRE, BrazierFire.NONE), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            if (wasBurning) {
                level.playSound((Player)null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, pos);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Waterlogging.tickWater(state, level, pos);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return Waterlogging.fluid(blockState, super.getFluidState(blockState));
    }

    /**
     * True only for an open flame. Embers are hot but are deliberately not "lit" - they are the
     * state a flame can still be struck from.
     */
    public static boolean isLit(BlockState state) {
        if (!state.hasProperty(FIRE)) {
            return false;
        }
        BrazierFire fire = state.getValue(FIRE);
        return fire == BrazierFire.LIT || fire == BrazierFire.SOUL;
    }

    protected boolean canBeLit(BlockState state) {
        return state.hasProperty(FIRE) && !isLit(state);
    }

    public static void extinguish(@Nullable Player player, BlockState state, LevelAccessor level, BlockPos pos) {
        setFire(level, state, pos, BrazierFire.NONE);
        level.playSound((Player)null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void setFire(LevelAccessor level, BlockState state, BlockPos pos, BrazierFire fire) {
        level.setBlock(pos, state.setValue(FIRE, fire), 11);
    }

    /**
     * The values of {@link #FIRE}, each carrying the light level it emits.
     *
     * <p>{@link #EMBERS} at 3 is not an arbitrary midpoint. Hostile mobs need block light 0 to
     * spawn and block light falls off 1 per block, so a light-L brazier clears an L-block radius;
     * at 10 or 15 a single brazier sterilises a whole ordinary dungeon room. 3 is the highest level
     * that still reads as hot while leaving most of a room's floor spawnable.
     */
    public enum BrazierFire implements StringRepresentable {
        NONE("none", 0),
        EMBERS("embers", 3),
        SOUL("soul", 10),
        LIT("lit", 15);

        private final String name;
        private final int lightLevel;

        private BrazierFire(String name, int lightLevel) {
            this.name = name;
            this.lightLevel = lightLevel;
        }

        public int getLightLevel() {
            return this.lightLevel;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return getName();
        }
    }
}
