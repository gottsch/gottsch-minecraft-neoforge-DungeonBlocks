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

import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import com.mojang.serialization.MapCodec;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.blockentity.SarcophagusBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A two-block stone sarcophagus with a carved effigy on its lid. Right-click and the lid grinds
 * aside to show the tomb; right-click again and it slides back.
 *
 * <p>A sarcophagus a structure or command has sealed holds something for its first opening: loot,
 * which spills out over the lid, or a guardian mob that rises from it - one or the other, or
 * nothing, on a single weighted roll. The contents and weights are block data on
 * {@link SarcophagusBlockEntity}, which documents them. One placed by a player holds nothing.
 *
 * <p>The two-block placement and breaking are the slab table's, reused as they are: FOOT where the
 * player clicked, HEAD one block further in the direction they faced, and whichever half breaks
 * takes the other with it.
 *
 * <p>THE LID is drawn the way a chest's is: never baked into the block, always by
 * {@code SarcophagusRenderer}. The block's model is the body alone. A click only flips OPEN; each
 * client sees the change and animates the lid to its new position, from wherever it is, so a
 * click mid-slide reverses it smoothly. Nothing ticks, on either side.
 *
 * <p>Two earlier designs were rejected in game. Stepping through five baked lid frames on
 * scheduled ticks read as choppy. Baking the lid in at rest and handing it to the renderer only
 * while it moved flickered at both hand-offs: the renderer starts and stops drawing on the very
 * frame the state changes, but the chunk re-mesh that adds or removes the baked lid lands a frame
 * or more later.
 */
public class SarcophagusBlock extends SlabTableBlock implements EntityBlock {
	public static final MapCodec<SarcophagusBlock> CODEC = simpleCodec(SarcophagusBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    /** How long the lid takes to open or close, in ticks. */
    public static final int MOVE_TICKS = 12;

    /** How the lid moves when the tomb opens; the renderer reads it. */
    public enum LidMotion {
        /** Grinds aside along the model's x, as a stone slab would. */
        SLIDE,
        /** Swings up on a hinge along one side, as a coffin lid does. */
        HINGE
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);
    /** The top of the sarcophagus, where the lid sits and a guardian stands. */
    private static final double TOP = 14.0 / 16.0;

    public SarcophagusBlock(Properties properties) {
        super(properties);
        // Explicitly closed. The slab table's constructor builds the default from
        // stateDefinition.any(), which takes every property's FIRST value - and a boolean's first
        // value is true, so left to that every sarcophagus would be placed open.
        registerDefaultState(defaultBlockState().setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public LidMotion lidMotion() {
        return LidMotion.SLIDE;
    }

    /** The top of the closed tomb, in blocks: where the lid rests and a guardian stands. */
    protected double lidTop() {
        return TOP;
    }

    protected void playLidSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.8F, 0.5F);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SarcophagusBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean open = !state.getValue(OPEN);
            level.setBlock(pos, state.setValue(OPEN, open), 2);
            BlockPos otherPos = pos.relative(towardOtherHalf(state));
            BlockState other = level.getBlockState(otherPos);
            if (other.is(this)) {
                level.setBlock(otherPos, other.setValue(OPEN, open), 2);
            }
            playLidSound(level, pos, open);
            if (open && level instanceof ServerLevel serverLevel) {
                disturb(serverLevel, pos, state, player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * The first opening of a sealed tomb: one roll between its loot, its guardian and nothing, and
     * then it is empty for good. Either half may hold the contents - a structure could put them on
     * either - but a tomb is one roll, so the head's win and both halves are emptied.
     */
    private void disturb(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockPos otherPos = pos.relative(towardOtherHalf(state));
        boolean clickedHead = state.getValue(PART) == BedPart.HEAD;
        BlockPos headPos = clickedHead ? pos : otherPos;
        BlockPos footPos = clickedHead ? otherPos : pos;
        SarcophagusBlockEntity head = level.getBlockEntity(headPos) instanceof SarcophagusBlockEntity h ? h : null;
        SarcophagusBlockEntity foot = level.getBlockEntity(footPos) instanceof SarcophagusBlockEntity f ? f : null;
        SarcophagusBlockEntity tomb = head != null && head.isSealed() ? head
                : foot != null && foot.isSealed() ? foot : null;
        if (tomb == null) {
            return;
        }
        // the middle of the lid, over the seam between the halves
        Vec3 top = new Vec3((headPos.getX() + footPos.getX()) / 2.0 + 0.5, pos.getY() + lidTop(),
                (headPos.getZ() + footPos.getZ()) / 2.0 + 0.5);
        switch (tomb.rollOutcome(level.random)) {
            case LOOT -> spillLoot(level, tomb, top, player);
            case GUARDIAN -> raiseGuardian(level, tomb, top, headPos, footPos, player);
            default -> { }
        }
        if (head != null) {
            head.empty();
        }
        if (foot != null) {
            foot.empty();
        }
    }

    /** The loot spills out over the lid, rolled as chest loot for the player who opened the tomb. */
    private static void spillLoot(ServerLevel level, SarcophagusBlockEntity tomb, Vec3 top, Player player) {
        ResourceLocation id = tomb.getLootTable();
        if (id == null || BuiltInLootTables.EMPTY.location().equals(id)) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, ResourceKey.create(Registries.LOOT_TABLE, id));
        }
        LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, top)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);
        List<ItemStack> loot = tomb.getLootTableSeed() == 0L
                ? table.getRandomItems(params)
                : table.getRandomItems(params, tomb.getLootTableSeed());
        loot.forEach(stack -> Containers.dropItemStack(level, top.x, top.y, top.z, stack));
    }

    /**
     * The guardian rises onto the open tomb - or, with no headroom there, steps out beside it - and
     * goes for the player who disturbed it. It is finalized through NeoForge's spawn event, as a
     * spawner's mob is, so a skeleton gets its bow and difficulty mods see it.
     */
    private static void raiseGuardian(ServerLevel level, SarcophagusBlockEntity tomb, Vec3 top,
                                      BlockPos headPos, BlockPos footPos, Player player) {
        ResourceLocation id = tomb.getGuardian();
        Optional<EntityType<?>> type = id == null ? Optional.empty() : EntityType.byString(id.toString());
        if (type.isEmpty()) {
            DungeonBlocks.LOGGER.warn("sarcophagus at {} has an unknown guardian '{}'", headPos, id);
            return;
        }
        Entity guardian = type.get().create(level);
        if (guardian == null) {
            return;
        }
        Vec3 at = standingRoom(level, guardian, top, headPos, footPos);
        if (at == null) {
            return;
        }
        // facing the player who opened it
        float yaw = (float) (Mth.atan2(player.getZ() - at.z, player.getX() - at.x) * Mth.RAD_TO_DEG) - 90.0F;
        guardian.moveTo(at.x, at.y, at.z, yaw, 0.0F);
        if (guardian instanceof Mob mob) {
            mob.yHeadRot = yaw;
            mob.yBodyRot = yaw;
            EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()),
                    MobSpawnType.TRIGGERED, null);
            if (!player.isCreative() && !player.isSpectator()) {
                mob.setTarget(player);
            }
        }
        level.addFreshEntityWithPassengers(guardian);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 0.6, at.z, 24, 0.3, 0.5, 0.3, 0.02);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 1.0F, 0.6F);
    }

    /** On the tomb if the guardian fits there, else the first free spot beside it; null if none. */
    @Nullable
    private static Vec3 standingRoom(ServerLevel level, Entity guardian, Vec3 top, BlockPos headPos, BlockPos footPos) {
        List<Vec3> spots = new ArrayList<>();
        spots.add(top);
        for (BlockPos half : List.of(headPos, footPos)) {
            for (Direction side : Direction.Plane.HORIZONTAL) {
                BlockPos beside = half.relative(side);
                if (!beside.equals(headPos) && !beside.equals(footPos)) {
                    spots.add(Vec3.atBottomCenterOf(beside));
                }
            }
        }
        for (Vec3 spot : spots) {
            guardian.moveTo(spot.x, spot.y, spot.z);
            if (level.noCollision(guardian)) {
                return spot;
            }
        }
        return null;
    }
}
