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

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A cauldron brimming with a coloured brew at a rolling boil: the surface bubbles (an animated
 * texture), bubbles in the brew's colour rise and burst over the rim, and steam drifts off the top.
 * A prop, not a working cauldron - it takes no water, brews nothing and is never emptied.
 *
 * <p>The colour is the {@link #COLOR} blockstate, so a structure can name it directly. The model
 * is vanilla's full cauldron with a greyscale brew tinted by colour (ClientSetup), so every colour
 * shares one model. Right-click with a matching dye to change it.
 */
public class BubblingCauldronBlock extends Block {
    /**
     * The brew's colour. Green by default: a structure that names no colour, or misspells the
     * property, gets the classic witch's brew.
     */
    public static final EnumProperty<BrewColor> COLOR = EnumProperty.create("color", BrewColor.class);

    /** Vanilla's cauldron shape: the hollow bowl on four feet (AbstractCauldronBlock). */
    private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(),
            Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D),
                    box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE),
            BooleanOp.ONLY_FIRST);
    /** The brew's surface, as the full cauldron model draws it (template_cauldron_full). */
    private static final double SURFACE_Y = 15.0D / 16.0D;

    public BubblingCauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(COLOR, BrewColor.GREEN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    /** A dye of one of the brew colours turns the brew that colour. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = stack;
        BrewColor color = BrewColor.fromDye(held.getItem());
        if (color == null || !player.getAbilities().mayBuild) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (color == state.getValue(COLOR)) {
            return ItemInteractionResult.CONSUME;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(COLOR, color), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + SURFACE_Y;
        double z = pos.getZ() + 0.5D;

        // steam, off the whole surface: a slow white rise, most ticks
        if (random.nextInt(3) != 0) {
            level.addParticle(ParticleTypes.CLOUD, x + offset(random), y + 0.05D, z + offset(random),
                    0.0D, 0.02D + random.nextDouble() * 0.02D, 0.0D);
        }

        // bubbles in the brew's colour, rising off the surface, as a potion's swirls do. Since 1.20.5
        // ENTITY_EFFECT carries its colour in the particle option, not in the speed arguments.
        if (random.nextInt(2) == 0) {
            BrewColor color = state.getValue(COLOR);
            level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT,
                            (float) color.red(), (float) color.green(), (float) color.blue()),
                    x + offset(random), y, z + offset(random), 0.0D, 0.0D, 0.0D);
        }

        // a burst at the surface, and now and then the sound of one
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.BUBBLE_POP, x + offset(random), y, z + offset(random), 0.0D, 0.01D, 0.0D);
        }
        if (random.nextInt(12) == 0) {
            level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS,
                    0.3F + random.nextFloat() * 0.2F, 0.7F + random.nextFloat() * 0.3F, false);
        }
    }

    /** Somewhere across the brew's surface, inside the rim (2..14 px). */
    private static double offset(RandomSource random) {
        return (random.nextDouble() - 0.5D) * 0.7D;
    }

    public enum BrewColor implements StringRepresentable {
        GREEN("green", 0x5DBB2F, Items.GREEN_DYE),
        PURPLE("purple", 0x8A3FCF, Items.PURPLE_DYE),
        RED("red", 0xB8262C, Items.RED_DYE),
        BLUE("blue", 0x2F68D6, Items.BLUE_DYE),
        YELLOW("yellow", 0xE3C22E, Items.YELLOW_DYE),
        BLACK("black", 0x2B2633, Items.BLACK_DYE);

        private final String name;
        private final int color;
        private final Item dye;

        BrewColor(String name, int color, Item dye) {
            this.name = name;
            this.color = color;
            this.dye = dye;
        }

        /** The brew colour a dye makes, or null if it is not one of them. */
        public static BrewColor fromDye(Item item) {
            for (BrewColor color : values()) {
                if (color.dye == item) {
                    return color;
                }
            }
            return null;
        }

        /** The tint for the brew's surface, as 0xRRGGBB. */
        public int getColor() {
            return color;
        }

        public double red() {
            return ((color >> 16) & 0xFF) / 255.0D;
        }

        public double green() {
            return ((color >> 8) & 0xFF) / 255.0D;
        }

        public double blue() {
            return (color & 0xFF) / 255.0D;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
