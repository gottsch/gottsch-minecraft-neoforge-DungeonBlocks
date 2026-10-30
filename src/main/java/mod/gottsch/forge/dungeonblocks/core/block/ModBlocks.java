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
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.util.ColorRGBA;
import net.neoforged.bus.api.IEventBus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import mod.gottsch.neo.gottschcore.block.FacingBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @author Mark Gottschling on Jan 12, 2020
 */
public class ModBlocks {
    public static final Map<DeferredHolder<Block, ? extends Block>, DeferredHolder<Item, ? extends Item>> MAP = Maps.newHashMap();

    /**
     * Every banner variant, in registration order. One BlockEntityType and one renderer cover the
     * whole list (see ModBlockEntityTypes and DungeonBannerRenderer), and the datagen providers
     * iterate it - so a new banner is one {@link #banner} call here plus its two PNGs, with nothing
     * else to touch.
     */
    public static final List<DeferredHolder<Block, Block>> BANNERS = new ArrayList<>();

    /**
     * The mod's own stone blocks that are neither decorative types ({@link #DECOR}) nor in a list
     * of their own (capstones, niches, crumbling floors, hidden doors): its square, large and mossy
     * bricks with their stairs and slabs, the greek blocks, the square-brick facades and the slab
     * tables. Datagen tags them for a pickaxe. Register one with {@link #stone}, and it is tagged.
     */
    public static final List<DeferredHolder<Block, ? extends Block>> STONE_BLOCKS = new ArrayList<>();

    private static <B extends Block> DeferredHolder<Block, B> stone(String id, Supplier<B> block) {
        DeferredHolder<Block, B> registered = Registration.BLOCKS.register(id, block);
        STONE_BLOCKS.add(registered);
        return registered;
    }

    // ------------------------------------------------------------------
    // Copper helpers. The copper families themselves are CopperFamily.register
    // calls below; these are the grate's own properties, which most of the
    // other copper shapes copy.
    // ------------------------------------------------------------------

    private static Properties copperGrateProperties() {
        return Properties.of().strength(3.0F, 6.0F).sound(SoundType.COPPER).mapColor(MapColor.WARPED_STEM).noOcclusion()
                .requiresCorrectToolForDrops().isValidSpawn((a, b, c, d) -> false).isRedstoneConductor((a, b, c) -> false)
                .isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false);
    }

    /** The grate's map colour at each later age (the unaffected grate's is in its properties). */
    private static MapColor copperGrateColor(WeatherState age) {
        return switch (age) {
            case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case WEATHERED -> MapColor.COLOR_ORANGE;
            case OXIDIZED -> MapColor.WARPED_NYLIUM;
            default -> MapColor.WARPED_STEM;
        };
    }

    // wall sconce
    public static final DeferredHolder<Block, Block> TORCH_SCONCE = Registration.BLOCKS.register("torch_sconce_block",
            () -> new TorchSconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)
                    .noCollission().lightLevel((light) -> {
                        return 14;
                    }).sound(SoundType.WOOD)));

    // purely decorative corner webbing - no vanilla cobweb slowdown/break behavior, see AngleCobwebBlock
    //
    // Properties are spelled out rather than copied from Blocks.COBWEB, which carries
    // requiresCorrectToolForDrops() and cannot have it unset - Properties has a setter for it but no
    // clearer. Inheriting it made these unusable: nothing is ever the "correct" tool, because these
    // blocks are in no mineable tag, and SwordItem.isCorrectToolForDrops hardcodes Blocks.COBWEB. So
    // hasCorrectToolForDrops was always false, which both blocked the drop entirely and put the
    // 4.0 hardness over the 100x no-correct-tool divisor: 1.0 / 4.0 / 100 = 20 seconds to break.
    //
    // Vanilla cobweb only feels quick because SwordItem.getDestroySpeed special-cases
    // Blocks.COBWEB to 15.0F, giving 15.0 / 4.0 / 30 = 8 ticks. A modded web cannot reach that
    // branch, so the same 8 ticks is reached from the block side instead: 0.4 hardness, no tool
    // requirement, and SWORD_EFFICIENT (1.5F for swords) = 1.5 / 0.4 / 30. Anything else takes
    // 12 ticks, which keeps a decorative block removable without a specific tool in hand.
    private static final Supplier<Properties> ANGLE_COBWEB_PROPS = () -> Properties.of()
            .mapColor(Blocks.COBWEB.defaultMapColor())
            .sound(Blocks.COBWEB.defaultBlockState().getSoundType())
            .noCollission()
            .strength(0.4F)
            .pushReaction(PushReaction.DESTROY);

    public static final DeferredHolder<Block, Block> ANGLE_COBWEB_1 = Registration.BLOCKS.register("angle_cobweb_1",
            () -> new AngleCobwebBlock(ANGLE_COBWEB_PROPS.get()));
    public static final DeferredHolder<Block, Block> ANGLE_COBWEB_2 = Registration.BLOCKS.register("angle_cobweb_2",
            () -> new AngleCobwebBlock(ANGLE_COBWEB_PROPS.get()));

    public static final DeferredHolder<Block, Block> CANDLE_SCONCE = Registration.BLOCKS.register("candle_sconce_block",
            () -> new SconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion().lightLevel(SconceBlock.LIGHT_EMISSION)));
    public static final DeferredHolder<Block, Block> DUNGEON_LANTERN = Registration.BLOCKS.register("dungeon_lantern", () -> new DungeonLanternBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(DungeonLanternBlock.LIGHT_EMISSION).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredHolder<Block, Block> BRAZIER = Registration.BLOCKS.register("brazier_block", () -> new BrazierBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().strength(3.5F).sound(SoundType.METAL).lightLevel(BrazierBlock.LIGHT_EMISSION).noOcclusion()));

    // ------------------------------------------------------------------
    // Slab tables. Bed-like two-block furniture, so they are registered explicitly rather than
    // through the ModMaterials.STONE loop - a table per material would add two blockstates and an
    // item for all ~30 stones. Adding one is a register(...) line here plus a slabTableBlock(...)
    // line in ModBlockStateProvider and a slabTableItem(...) line in ItemModelsProvider.
    // PushReaction.DESTROY keeps a piston from separating the halves.
    // ------------------------------------------------------------------
    private static DeferredHolder<Block, Block> slabTable(String id, Block propsFrom) {
        return stone(id, () -> new SlabTableBlock(
                Properties.ofFullCopy(propsFrom).noOcclusion().pushReaction(PushReaction.DESTROY)));
    }

    public static final DeferredHolder<Block, Block> STONE_SLAB_TABLE = slabTable("stone_slab_table", Blocks.STONE);
    public static final DeferredHolder<Block, Block> STONE_BRICKS_SLAB_TABLE = slabTable("stone_bricks_slab_table", Blocks.STONE_BRICKS);
    public static final DeferredHolder<Block, Block> MOSSY_STONE_BRICKS_SLAB_TABLE = slabTable("mossy_stone_bricks_slab_table", Blocks.MOSSY_STONE_BRICKS);
    public static final DeferredHolder<Block, Block> SMOOTH_STONE_SLAB_TABLE = slabTable("smooth_stone_slab_table", Blocks.SMOOTH_STONE);
    public static final DeferredHolder<Block, Block> SMOOTH_SANDSTONE_SLAB_TABLE = slabTable("smooth_sandstone_slab_table", Blocks.SMOOTH_SANDSTONE);

    // Dark iron grates in their rust stages. Separate blocks, not a weathering chain (see
    // AgedIronFamily): a builder places whichever stage they want. Same properties as the plain grate -
    // rust is only the texture (tools/gen_rusted_dark_iron_textures.py).
    public static final AgedIronFamily DARK_IRON_GRATES = AgedIronFamily.register("dark_iron_grate", AgedIronFamily.ALL_AGES,
            age -> age == AgedIronFamily.Age.PLAIN
                    ? Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()
                    : Properties.ofFullCopy(ModBlocks.DARK_IRON_GRATES.get(AgedIronFamily.Age.PLAIN).get()),
            HeavyGrateBlock::new);

    // Copper families: 4 weathering ages plus a waxed twin of each (CopperFamily). Properties are
    // always copied from an EXPLICIT source family named right here, so a waxed block can never
    // copy the wrong age. The grate is the root most of the others borrow from.
    public static final CopperFamily COPPER_GRATES = CopperFamily.register("copper_grate",
            age -> age == WeatherState.UNAFFECTED ? copperGrateProperties()
                    : ModBlocks.COPPER_GRATES.props(WeatherState.UNAFFECTED).mapColor(copperGrateColor(age)),
            WeatheringCopperGrateBlock::new,
            age -> ModBlocks.COPPER_GRATES.props(age), WaterloggedCubeBlock::new);
    public static final CopperFamily COPPER_HEAVY_GRATES = CopperFamily.register("copper_heavy_grate",
            COPPER_GRATES::props, WeatheringHeavyGrateBlock::new, COPPER_GRATES::props, HeavyGrateBlock::new);
    public static final CopperFamily COPPER_VALVE_WHEELS = CopperFamily.register("copper_valve_wheel",
            COPPER_GRATES::props, WeatheringCopperValveWheelBlock::new, COPPER_GRATES::props, ValveWheelBlock::new);
    public static final CopperFamily COPPER_TRAPDOORS = CopperFamily.register("copper_trapdoor",
            COPPER_GRATES::props, WeatheringCopperTrapDoorBlock::new,
            COPPER_GRATES::props, p -> new TrapDoorBlock(BlockSetType.DARK_OAK, p));

    // dark iron heavy trapdoors in their rust stages - see the dark iron grates above
    public static final AgedIronFamily DARK_IRON_HEAVY_TRAPDOORS = AgedIronFamily.register("dark_iron_heavy_trapdoor", AgedIronFamily.ALL_AGES,
            age -> age == AgedIronFamily.Age.PLAIN
                    ? Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()
                    : Properties.ofFullCopy(ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.get(AgedIronFamily.Age.PLAIN).get()),
            HeavyTrapDoorBlock::new);
    public static final CopperFamily COPPER_HEAVY_TRAPDOORS = CopperFamily.register("copper_heavy_trapdoor",
            COPPER_TRAPDOORS::props, WeatheringHeavyTrapDoorBlock::new, COPPER_TRAPDOORS::props, HeavyTrapDoorBlock::new);

    public static final DeferredHolder<Block, Block> SQUARE_STONE_BRICK = stone("square_stone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_STONE_BRICK = stone("mossy_square_stone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    // square_mud_brick is square_stone_brick's texture remapped onto the vanilla mud brick
    // palette, so its properties come from MUD_BRICKS rather than STONE_BRICKS.
    public static final DeferredHolder<Block, Block> SQUARE_MUD_BRICK = stone("square_mud_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });

    // ------------------------------------------------------------------
    // square brick stairs / facades.
    // Registered explicitly rather than as ModMaterials.STONE entries: a Material there produces
    // all eleven stone block-types, and only stairs, facade and quarter facade are wanted here.
    // Datagen lists these facades by name (they are not DecorType blocks), and stone(...) tags
    // them for a pickaxe.
    // ------------------------------------------------------------------
    public static final DeferredHolder<Block, StairBlock> SQUARE_STONE_BRICK_STAIRS = stone("square_stone_brick_stairs", () -> {
        return new StairBlock(SQUARE_STONE_BRICK.get().defaultBlockState(), Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, StairBlock> SQUARE_MUD_BRICK_STAIRS = stone("square_mud_brick_stairs", () -> {
        return new StairBlock(SQUARE_MUD_BRICK.get().defaultBlockState(), Properties.ofFullCopy(Blocks.MUD_BRICK_STAIRS));
    });
    // The mossy counterpart of SQUARE_STONE_BRICK_STAIRS; Dungeons2's weathering rules age square stone
    // brick stairs into it. Follows MOSSY_LARGE_BRICK_STAIRS -- mossy stone brick stairs' properties
    // over the mossy full block's state, and the mossy full block's texture.
    public static final DeferredHolder<Block, StairBlock> MOSSY_SQUARE_STONE_BRICK_STAIRS = stone("mossy_square_stone_brick_stairs", () -> {
        return new StairBlock(MOSSY_SQUARE_STONE_BRICK.get().defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });

    // Square stone brick slabs - the mod's first SlabBlock. A double slab drops two items, which
    // the blanket dropSelf in ModBlockLootTables would not do; see the SlabBlock branch there.
    public static final DeferredHolder<Block, SlabBlock> SQUARE_STONE_BRICK_SLAB = stone("square_stone_brick_slab", () -> {
        return new SlabBlock(Properties.ofFullCopy(Blocks.STONE_BRICK_SLAB));
    });
    public static final DeferredHolder<Block, SlabBlock> MOSSY_SQUARE_STONE_BRICK_SLAB = stone("mossy_square_stone_brick_slab", () -> {
        return new SlabBlock(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_SLAB));
    });

    public static final DeferredHolder<Block, Block> SQUARE_STONE_BRICK_FACADE_BLOCK = stone("square_stone_brick_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS), DecorType.FACADE);
    });
    public static final DeferredHolder<Block, Block> SQUARE_MUD_BRICK_FACADE_BLOCK = stone("square_mud_brick_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS), DecorType.FACADE);
    });
    public static final DeferredHolder<Block, Block> SQUARE_STONE_BRICK_QUARTER_FACADE_BLOCK = stone("square_stone_brick_quarter_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS), DecorType.QUARTER_FACADE);
    });
    public static final DeferredHolder<Block, Block> SQUARE_MUD_BRICK_QUARTER_FACADE_BLOCK = stone("square_mud_brick_quarter_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS), DecorType.QUARTER_FACADE);
    });
    public static final DeferredHolder<Block, Block> LEFT_LARGE_STONE_BRICK = stone("left_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_STONE_BRICK = stone("right_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_STONE_BRICK = stone("mossy_left_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_STONE_BRICK = stone("mossy_right_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });

    // the two halves of one large brick spanning two blocks, in the mud brick palette
    public static final DeferredHolder<Block, Block> LEFT_LARGE_MUD_BRICK = stone("left_large_mud_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_MUD_BRICK = stone("right_large_mud_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });

    // Mossy mud bricks take their properties from MUD_BRICKS - vanilla has no mossy mud brick to
    // copy - and their moss is the mod's usual overlay, unchanged from the stone and clay variants.
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_MUD_BRICK = stone("mossy_square_mud_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_MUD_BRICK = stone("mossy_left_large_mud_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_MUD_BRICK = stone("mossy_right_large_mud_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS));
    });

    // The square / large brick set in the deepslate brick palette - the same textures as the stone
    // set, per-shade remapped onto vanilla deepslate_bricks' seven shades (see
    // tools/gen_deepslate_brick_textures.py).
    public static final DeferredHolder<Block, Block> SQUARE_DEEPSLATE_BRICK = stone("square_deepslate_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    // As with the mud bricks above, vanilla has no mossy deepslate brick to copy, so the mossy
    // variants take their properties from the plain DEEPSLATE_BRICKS family.
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_DEEPSLATE_BRICK = stone("mossy_square_deepslate_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> SQUARE_DEEPSLATE_BRICK_STAIRS = stone("square_deepslate_brick_stairs", () -> {
        return new StairBlock(SQUARE_DEEPSLATE_BRICK.get().defaultBlockState(), Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_SQUARE_DEEPSLATE_BRICK_STAIRS = stone("mossy_square_deepslate_brick_stairs", () -> {
        return new StairBlock(MOSSY_SQUARE_DEEPSLATE_BRICK.get().defaultBlockState(), Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, SlabBlock> SQUARE_DEEPSLATE_BRICK_SLAB = stone("square_deepslate_brick_slab", () -> {
        return new SlabBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB));
    });
    public static final DeferredHolder<Block, SlabBlock> MOSSY_SQUARE_DEEPSLATE_BRICK_SLAB = stone("mossy_square_deepslate_brick_slab", () -> {
        return new SlabBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB));
    });
    // Unlike the stone and mud square bricks, which have plain facades only, these come in mossy
    // too - the mossy texture already exists for the full block, so the facade costs only the
    // registration and the DataGenMaps texture entry the facade generators look the material up by.
    public static final DeferredHolder<Block, Block> SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK = stone("square_deepslate_brick_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS), DecorType.FACADE);
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK = stone("mossy_square_deepslate_brick_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS), DecorType.FACADE);
    });
    public static final DeferredHolder<Block, Block> SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK = stone("square_deepslate_brick_quarter_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS), DecorType.QUARTER_FACADE);
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK = stone("mossy_square_deepslate_brick_quarter_facade_block", () -> {
        return new FacadeShapeBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS), DecorType.QUARTER_FACADE);
    });
    public static final DeferredHolder<Block, Block> LEFT_LARGE_DEEPSLATE_BRICK = stone("left_large_deepslate_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_DEEPSLATE_BRICK = stone("right_large_deepslate_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_DEEPSLATE_BRICK = stone("mossy_left_large_deepslate_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_DEEPSLATE_BRICK = stone("mossy_right_large_deepslate_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });

    // Chiseled Deepslate Bricks: vanilla's chiseled STONE BRICK carving retoned onto the deepslate
    // brick palette. Distinct from vanilla's own CHISELED_DEEPSLATE, which carries a different
    // carving and is a darker stone - this one is a member of the deepslate BRICK family and sits at
    // that family's brightness, so a course of it reads as part of a deepslate brick wall.
    // Plural, like Polished Andesite Bricks and vanilla's Chiseled Stone Bricks: the face shows
    // several bricks, unlike the square/large bricks which are one brick per block.
    public static final DeferredHolder<Block, Block> CHISELED_DEEPSLATE_BRICKS = stone("chiseled_deepslate_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_CHISELED_DEEPSLATE_BRICKS = stone("mossy_chiseled_deepslate_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });

    // Base blocks for the three mossy deepslate material families in ModMaterials.STONE. Vanilla
    // has no mossy deepslate to source them from, so the full block is registered here by hand
    // while the eleven decorative types come off the STONE loop.
    // Properties come from the plain block the moss grows on; only the texture differs.
    public static final DeferredHolder<Block, Block> MOSSY_DEEPSLATE_BRICKS = stone("mossy_deepslate_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_DEEPSLATE_TILES = Registration.BLOCKS.register("mossy_deepslate_tiles", () -> {
        return new Block(Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
    });
    public static final DeferredHolder<Block, Block> MOSSY_COBBLED_DEEPSLATE = Registration.BLOCKS.register("mossy_cobbled_deepslate", () -> {
        return new Block(Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE));
    });
    // Mossy tuff: a single full block only, not a ModMaterials.STONE family. Properties from plain
    // tuff; texture from tools/gen_mossy_textures.py.
    public static final DeferredHolder<Block, Block> MOSSY_TUFF = Registration.BLOCKS.register("mossy_tuff", () -> {
        return new Block(Properties.ofFullCopy(Blocks.TUFF));
    });
    // Stairs are not one of the eleven STONE block-types, so this is registered explicitly the way
    // MOSSY_POLISHED_ANDESITE_BRICK_STAIRS is - vanilla deepslate brick stairs' properties over the
    // mossy full block's state.
    public static final DeferredHolder<Block, StairBlock> MOSSY_DEEPSLATE_BRICK_STAIRS = stone("mossy_deepslate_brick_stairs", () -> {
        return new StairBlock(MOSSY_DEEPSLATE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS));
    });

    // Polished Andesite Bricks: the vanilla stone brick pattern re-toned onto polished andesite's
    // own palette, the way vanilla derives deepslate_bricks from polished_deepslate. Plural, because
    // the texture shows several bricks - unlike square/large bricks, which are one brick per block.
    // Not a ModMaterials.STONE entry: that would produce all eleven stone block-types at once.
    public static final DeferredHolder<Block, Block> POLISHED_ANDESITE_BRICKS = stone("polished_andesite_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.POLISHED_ANDESITE));
    });
    public static final DeferredHolder<Block, StairBlock> POLISHED_ANDESITE_BRICK_STAIRS = stone("polished_andesite_brick_stairs", () -> {
        return new StairBlock(POLISHED_ANDESITE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.POLISHED_ANDESITE_STAIRS));
    });
    // Vanilla has no mossy polished andesite to copy properties from, so - as with mossy_bricks/
    // mossy_large_bricks above - these take their properties from MOSSY_STONE_BRICKS instead.
    public static final DeferredHolder<Block, Block> MOSSY_POLISHED_ANDESITE_BRICKS = stone("mossy_polished_andesite_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_POLISHED_ANDESITE_BRICK_STAIRS = stone("mossy_polished_andesite_brick_stairs", () -> {
        return new StairBlock(MOSSY_POLISHED_ANDESITE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_BRICKS = stone("mossy_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_BRICK_STAIRS = stone("mossy_brick_stairs", () -> {
        return new StairBlock(Blocks.BRICKS.defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, Block> LARGE_BRICKS = stone("large_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LARGE_BRICKS = stone("mossy_large_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> LARGE_BRICK_STAIRS = stone("large_brick_stairs", () -> {
        return new StairBlock(LARGE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_LARGE_BRICK_STAIRS = stone("mossy_large_brick_stairs", () -> {
        return new StairBlock(MOSSY_LARGE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, Block> SQUARE_BRICK = stone("square_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_BRICK = stone("mossy_square_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> LEFT_LARGE_BRICK = stone("left_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_BRICK = stone("right_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_BRICK = stone("mossy_left_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_BRICK = stone("mossy_right_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });

    public static final DeferredHolder<Block, Block> COBBLESTONE_BRICK = stone("cobblestone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.COBBLESTONE));
    });
    public static final DeferredHolder<Block, Block> MOSSY_COBBLESTONE_BRICK = stone("mossy_cobblestone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE));
    });
    // rubble: plain full blocks, cobblestone-grade properties
    public static final DeferredHolder<Block, Block> RUBBLE = Registration.BLOCKS.register("rubble", () -> {
        return new Block(Properties.ofFullCopy(Blocks.COBBLESTONE));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RUBBLE = Registration.BLOCKS.register("mossy_rubble", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE));
    });
    // a full block only - it has no material family in ModMaterials.STONE, so no decorative
    // pieces and no stonecutting recipes. Vanilla has no mossy chiseled stone bricks, so it is
    // crafted (chiseled_stone_bricks + vine; a hand-written recipe in src/main/resources).
    public static final DeferredHolder<Block, Block> MOSSY_CHISELED_STONE_BRICKS = stone("mossy_chiseled_stone_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> GRAVEL_BRICK = stone("gravel_brick", () -> {
        return new ColoredFallingBlock(new ColorRGBA(-8356741), Properties.ofFullCopy(Blocks.GRAVEL));
    });
    ///// plants /////
    // mold / moss
    public static final DeferredHolder<Block, Block> MOLD = Registration.BLOCKS.register("mold", () -> {
        return new Mold(Properties.ofFullCopy(Blocks.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(0)));
    });
    public static final DeferredHolder<Block, Block> LICHEN = Registration.BLOCKS.register("lichen", () -> {
        return new Lichen(Properties.ofFullCopy(Blocks.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(0)));
    });

    public static final DeferredHolder<Block, Block> ROOTS = Registration.BLOCKS.register("roots_head", () -> new RootsHeadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WEEPING_VINES)));
    public static final DeferredHolder<Block, Block> ROOTS_BODY = Registration.BLOCKS.register("roots_body", () -> new RootsBodyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WEEPING_VINES_PLANT)));

    ///// end of plants /////
    ///
    // sewer
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_SEWER = Registration.BLOCKS.register("weathered_copper_sewer_block", () -> new SewerBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> TERRACOTTA_SEWER = Registration.BLOCKS.register("terracotta_sewer_block", () -> new SewerBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));

    // wall ring
    public static final DeferredHolder<Block, Block> WALL_RING = Registration.BLOCKS.register("wall_ring", () -> new WallRingBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));

    // swinging chain: one block per segment, stacked like a vanilla chain.
    // noOcclusion + noCollission because it's drawn by a BlockEntityRenderer and walked through.
    // Light comes from whatever fixture is attached, so it has to be a function of the state.
    public static final DeferredHolder<Block, Block> SWINGING_CHAIN = Registration.BLOCKS.register("swinging_chain",
            () -> new SwingingChainBlock(Properties.of().mapColor(MapColor.METAL)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .noCollission()
                    .lightLevel(SwingingChainBlock::lightEmission)));

    // ------------------------------------------------------------------
    // Wall banners. Every variant is the same block with a different texture - the design, the
    // grime, the tears and the bloodstains all live in the PNG, so a variant costs no code.
    //
    // Cloth, so NO requiresCorrectToolForDrops and no mineable tag (an axe only takes them down
    // faster) - a block that requires the correct tool while belonging to no tool tag can never be
    // mined for drops, and datagen now refuses to generate one.
    // ------------------------------------------------------------------

    private static Properties bannerProps(MapColor mapColor) {
        return Properties.of().mapColor(mapColor)
                .strength(1.0F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .noCollission();
    }

    /** Registers a two-block banner variant and records it in {@link #BANNERS}. */
    private static DeferredHolder<Block, Block> banner(String id, MapColor mapColor) {
        DeferredHolder<Block, Block> block = Registration.BLOCKS.register(id,
                () -> new DungeonBannerBlock(bannerProps(mapColor)));
        BANNERS.add(block);
        return block;
    }

    /**
     * Registers a one-block pennant. Also goes in {@link #BANNERS} - the list drives the datagen
     * loops and the shared BlockEntityType's valid-blocks, and both shapes want all of that. The
     * loot generator tells them apart by class instead, since only the two-block banner needs the
     * one-item-per-pair condition.
     */
    private static DeferredHolder<Block, Block> pennant(String id, MapColor mapColor) {
        DeferredHolder<Block, Block> block = Registration.BLOCKS.register(id,
                () -> new PennantBlock(bannerProps(mapColor)));
        BANNERS.add(block);
        return block;
    }

    public static final DeferredHolder<Block, Block> DUNGEON_BANNER = banner("dungeon_banner", MapColor.COLOR_RED);
    public static final DeferredHolder<Block, Block> GRIMY_BANNER = banner("grimy_banner", MapColor.COLOR_RED);
    public static final DeferredHolder<Block, Block> TATTERED_BANNER = banner("tattered_banner", MapColor.COLOR_RED);
    public static final DeferredHolder<Block, Block> ORC_BANNER = banner("orc_banner", MapColor.TERRACOTTA_GREEN);
    public static final DeferredHolder<Block, Block> TATTERED_ORC_BANNER =
            banner("tattered_orc_banner", MapColor.TERRACOTTA_GREEN);
    public static final DeferredHolder<Block, Block> BLOODSTAINED_ORC_BANNER =
            banner("bloodstained_orc_banner", MapColor.TERRACOTTA_GREEN);
    public static final DeferredHolder<Block, Block> UNDEAD_BANNER = banner("undead_banner", MapColor.COLOR_BLACK);
    public static final DeferredHolder<Block, Block> TATTERED_UNDEAD_BANNER =
            banner("tattered_undead_banner", MapColor.COLOR_BLACK);
    public static final DeferredHolder<Block, Block> BLOODSTAINED_UNDEAD_BANNER =
            banner("bloodstained_undead_banner", MapColor.COLOR_BLACK);
    public static final DeferredHolder<Block, Block> DWARVEN_BANNER = banner("dwarven_banner", MapColor.COLOR_BLUE);
    public static final DeferredHolder<Block, Block> TATTERED_DWARVEN_BANNER =
            banner("tattered_dwarven_banner", MapColor.COLOR_BLUE);
    public static final DeferredHolder<Block, Block> BLOODSTAINED_DWARVEN_BANNER =
            banner("bloodstained_dwarven_banner", MapColor.COLOR_BLUE);
    public static final DeferredHolder<Block, Block> CULT_BANNER = banner("cult_banner", MapColor.COLOR_PURPLE);
    public static final DeferredHolder<Block, Block> TATTERED_CULT_BANNER =
            banner("tattered_cult_banner", MapColor.COLOR_PURPLE);
    public static final DeferredHolder<Block, Block> BLOODSTAINED_CULT_BANNER =
            banner("bloodstained_cult_banner", MapColor.COLOR_PURPLE);
    public static final DeferredHolder<Block, Block> PLAGUE_BANNER = banner("plague_banner", MapColor.TERRACOTTA_YELLOW);
    public static final DeferredHolder<Block, Block> TATTERED_PLAGUE_BANNER =
            banner("tattered_plague_banner", MapColor.TERRACOTTA_YELLOW);
    public static final DeferredHolder<Block, Block> BLOODSTAINED_PLAGUE_BANNER =
            banner("bloodstained_plague_banner", MapColor.TERRACOTTA_YELLOW);

    // one-block pennants, pristine only for now
    public static final DeferredHolder<Block, Block> DUNGEON_PENNANT = pennant("dungeon_pennant", MapColor.COLOR_RED);
    public static final DeferredHolder<Block, Block> ORC_PENNANT = pennant("orc_pennant", MapColor.TERRACOTTA_GREEN);
    public static final DeferredHolder<Block, Block> UNDEAD_PENNANT = pennant("undead_pennant", MapColor.COLOR_BLACK);
    public static final DeferredHolder<Block, Block> DWARVEN_PENNANT = pennant("dwarven_pennant", MapColor.COLOR_BLUE);
    public static final DeferredHolder<Block, Block> CULT_PENNANT = pennant("cult_pennant", MapColor.COLOR_PURPLE);
    public static final DeferredHolder<Block, Block> PLAGUE_PENNANT =
            pennant("plague_pennant", MapColor.TERRACOTTA_YELLOW);

    // plate bracket
    public static final DeferredHolder<Block, Block> IRON_PLATE_BRACKET = Registration.BLOCKS.register("iron_plate_bracket_block", () -> new PlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_plate_bracket_block", () -> new PlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    // weathering props from the trapdoor, waxed from the grate - as these always have been
    public static final CopperFamily COPPER_PLATE_BRACKETS = CopperFamily.register("copper_plate_bracket_block",
            COPPER_TRAPDOORS::props, WeatheringPlateBracketBlock::new, COPPER_GRATES::props, PlateBracketBlock::new);

    // angle/elbow plate bracket
    public static final DeferredHolder<Block, Block> IRON_ANGLE_PLATE_BRACKET = Registration.BLOCKS.register("iron_angle_plate_bracket_block", () -> new AnglePlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_ANGLE_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_angle_plate_bracket_block", () -> new AnglePlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    // weathering props from the trapdoor, waxed from the grate - as these always have been
    public static final CopperFamily COPPER_ANGLE_PLATE_BRACKETS = CopperFamily.register("copper_angle_plate_bracket_block",
            COPPER_TRAPDOORS::props, WeatheringAnglePlateBracketBlock::new, COPPER_GRATES::props, AnglePlateBracketBlock::new);


    // corner plate bracket
    public static final DeferredHolder<Block, Block> IRON_CORNER_PLATE_BRACKET = Registration.BLOCKS.register("iron_corner_plate_bracket_block", () -> new CornerPlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_CORNER_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_corner_plate_bracket_block", () -> new CornerPlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));

    // weathering props from the trapdoor, waxed from the grate - as these always have been
    public static final CopperFamily COPPER_CORNER_PLATE_BRACKETS = CopperFamily.register("copper_corner_plate_bracket_block",
            COPPER_TRAPDOORS::props, WeatheringCornerPlateBracketBlock::new, COPPER_GRATES::props, CornerPlateBracketBlock::new);

    // hay patches
    public static final DeferredHolder<Block, Block> HAY_PATCH = Registration.BLOCKS.register("hay_patch_block", () -> new CarpetBlock(Properties.ofFullCopy(Blocks.YELLOW_CARPET)));
    // noOcclusion: the dirty hay texture has gaps the floor shows through. A carpet otherwise counts
    // as covering the whole top face of the block under it, which stops drawing that face - and the
    // gaps then look through a hole in the ground to the sky.
    public static final DeferredHolder<Block, Block> DIRTY_HAY_PATCH = Registration.BLOCKS.register("dirty_hay_patch_block", () -> new CarpetBlock(Properties.ofFullCopy(Blocks.YELLOW_CARPET).noOcclusion()));

    // greek blocks
    public static final DeferredHolder<Block, Block> STONE_GREEK_BLOCK = stone("stone_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, Block> ANDESITE_GREEK_BLOCK = stone("andesite_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.ANDESITE)));
    public static final DeferredHolder<Block, Block> POLISHED_BASALT_GREEK_BLOCK = stone("polished_basalt_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.POLISHED_BASALT)));

    // Mossy polished basalt. A rotated pillar like vanilla polished basalt, so it keeps the
    // separate top and side textures rather than wrapping the side texture round every face
    // (tools/gen_mossy_textures.py).
    public static final DeferredHolder<Block, Block> MOSSY_POLISHED_BASALT = Registration.BLOCKS.register("mossy_polished_basalt", () -> {
        return new RotatedPillarBlock(Properties.ofFullCopy(Blocks.POLISHED_BASALT));
    });

    // Dungeon doors: a vanilla wood's door reinforced, 2 blocks high, plus 3- and 4-high versions.
    // Ids: <wood>_dungeon_door, <wood>_dungeon_door_3, <wood>_dungeon_door_4. Registered in this
    // order - every 2-high door, then each wood's tall pair - as they always have been.
    public record DungeonDoor(String wood, Block vanillaDoor, int height, DeferredHolder<Block, Block> block) {
    }

    public static final List<DungeonDoor> DUNGEON_DOORS = new ArrayList<>();

    static {
        record Wood(String name, Block door, BlockSetType setType) {
        }
        List<Wood> woods = List.of(new Wood("spruce", Blocks.SPRUCE_DOOR, BlockSetType.SPRUCE),
                new Wood("crimson", Blocks.CRIMSON_DOOR, BlockSetType.CRIMSON),
                new Wood("dark_oak", Blocks.DARK_OAK_DOOR, BlockSetType.DARK_OAK),
                new Wood("mangrove", Blocks.MANGROVE_DOOR, BlockSetType.MANGROVE));
        for (Wood w : woods) {
            DUNGEON_DOORS.add(new DungeonDoor(w.name(), w.door(), 2, Registration.BLOCKS.register(w.name() + "_dungeon_door",
                    () -> new DungeonDoorBlock(Properties.ofFullCopy(w.door()), w.setType()))));
        }
        for (Wood w : woods) {
            for (int height : new int[]{3, 4}) {
                DUNGEON_DOORS.add(new DungeonDoor(w.name(), w.door(), height, Registration.BLOCKS.register(w.name() + "_dungeon_door_" + height,
                        () -> new TallDoorBlock(Properties.ofFullCopy(w.door()), w.setType(), height))));
            }
        }
    }

    public static final CopperFamily COPPER_DOORS = CopperFamily.register("copper_door",
            age -> age == WeatherState.UNAFFECTED
                    ? Properties.ofFullCopy(Blocks.DARK_OAK_DOOR).mapColor(Blocks.COPPER_BLOCK.defaultMapColor()).strength(3.0F, 6.0F).sound(SoundType.COPPER)
                    : ModBlocks.COPPER_DOORS.props(WeatherState.UNAFFECTED).mapColor(COPPER_GRATES.get(age).get().defaultMapColor()),
            (age, p) -> new WeatheringCopperDoorBlock(BlockSetType.IRON, age, p),
            age -> ModBlocks.COPPER_DOORS.props(age), p -> new WaxedCopperDoorBlock(p, BlockSetType.IRON));
    // A cell door for a wall of vanilla iron bars. Vanilla iron door properties - pickaxe, 5.0
    // strength, metal sound - but it opens by hand; see IronBarsDoorBlock for why it keeps the
    // iron block set type anyway.
    public static final DeferredHolder<Block, Block> IRON_BARS_DOOR = Registration.BLOCKS.register("iron_bars_door",
            () -> new IronBarsDoorBlock(Properties.ofFullCopy(Blocks.IRON_DOOR)));

    // Dark iron bars, plain and lightly rusted, each with a matching cell door. Vanilla's own
    // IronBarsBlock, so they connect to each other and to vanilla iron bars exactly as iron bars
    // do. Textures: tools/gen_dark_iron_bars_textures.py. The tarnished pair is one light stage
    // only - rust as staining - and creative-only, like the tarnished dark iron grate.
    public static final List<AgedIronFamily.Age> BARS_AGES = List.of(AgedIronFamily.Age.PLAIN, AgedIronFamily.Age.TARNISHED);
    public static final AgedIronFamily DARK_IRON_BARS = AgedIronFamily.register("dark_iron_bars", BARS_AGES,
            age -> Properties.ofFullCopy(Blocks.IRON_BARS), IronBarsBlock::new);
    // a vanilla ladder in every way but its look: a 3D model of dark iron rails and rungs, inside
    // the ladder's own shape. Iron bars' toughness, and like them it wants a pickaxe.
    public static final DeferredHolder<Block, Block> DARK_IRON_LADDER = Registration.BLOCKS.register("dark_iron_ladder",
            () -> new LadderBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    // the bars' cell doors, one per bars age
    public static final AgedIronFamily DARK_IRON_BARS_DOORS = AgedIronFamily.register("dark_iron_bars_door", BARS_AGES,
            age -> Properties.ofFullCopy(Blocks.IRON_DOOR), IronBarsDoorBlock::new);

    // Sharpened logs: the point of a palisade stake, set on the end of a log, one per vanilla wood.
    // Named after the stripped block they are crafted from, in vanilla's own words for it (log,
    // stem, block), so a creative search for "oak log" finds them. Properties are spelled out rather than copied from a log: vanilla log properties carry a map
    // colour function that reads the AXIS property, which this block does not have, so copying them
    // would crash the first time anything asked for its map colour. Values match the vanilla log -
    // strength 2, its wood's sound, its planks' map colour, and lava-flammable except the nether
    // stems. Every sharpened log is listed in SHARPENED_LOGS, which the datagen providers iterate.
    public static final List<DeferredHolder<Block, Block>> SHARPENED_LOGS = new ArrayList<>();
    // the other palisade pieces, one of each per wood, registered alongside its sharpened log
    public static final List<DeferredHolder<Block, Block>> CHEVALS_DE_FRISE = new ArrayList<>();
    public static final List<DeferredHolder<Block, Block>> WALKWAY_BRACKETS = new ArrayList<>();
    public static final DeferredHolder<Block, Block> SHARPENED_OAK_LOG = sharpened("oak_log", MapColor.WOOD, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_SPRUCE_LOG = sharpened("spruce_log", MapColor.PODZOL, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_BIRCH_LOG = sharpened("birch_log", MapColor.SAND, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_JUNGLE_LOG = sharpened("jungle_log", MapColor.DIRT, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_ACACIA_LOG = sharpened("acacia_log", MapColor.COLOR_ORANGE, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_DARK_OAK_LOG = sharpened("dark_oak_log", MapColor.COLOR_BROWN, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_MANGROVE_LOG = sharpened("mangrove_log", MapColor.COLOR_RED, SoundType.WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_CHERRY_LOG = sharpened("cherry_log", MapColor.TERRACOTTA_WHITE, SoundType.CHERRY_WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_BAMBOO_BLOCK = sharpened("bamboo_block", MapColor.COLOR_YELLOW, SoundType.BAMBOO_WOOD, true);
    public static final DeferredHolder<Block, Block> SHARPENED_CRIMSON_STEM = sharpened("crimson_stem", MapColor.CRIMSON_STEM, SoundType.STEM, false);
    public static final DeferredHolder<Block, Block> SHARPENED_WARPED_STEM = sharpened("warped_stem", MapColor.WARPED_STEM, SoundType.STEM, false);

    /**
     * Registers a wood's palisade pieces - its sharpened log, cheval-de-frise and walkway bracket -
     * so every wood always has all three. `log` is vanilla's word for the wood's log block
     * (oak_log, crimson_stem, bamboo_block); the wood's own name is what precedes its last word.
     */
    private static DeferredHolder<Block, Block> sharpened(String log, MapColor mapColor, SoundType sound, boolean burns) {
        Supplier<Properties> wood = () -> {
            Properties properties = Properties.of().mapColor(mapColor).instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F).sound(sound);
            return burns ? properties.ignitedByLava() : properties;
        };
        DeferredHolder<Block, Block> block = Registration.BLOCKS.register("sharpened_" + log, () -> new PyramidBlock(wood.get()));
        SHARPENED_LOGS.add(block);
        String name = log.substring(0, log.lastIndexOf('_'));
        CHEVALS_DE_FRISE.add(Registration.BLOCKS.register(name + "_cheval_de_frise",
                () -> new ChevalDeFriseBlock(wood.get().noOcclusion())));
        WALKWAY_BRACKETS.add(Registration.BLOCKS.register(name + "_walkway_bracket",
                () -> new WalkwayBracketBlock(wood.get())));
        return block;
    }


    // Spikes: iron bars' strength and sound. Not Properties.ofFullCopy(IRON_BARS): that carries
    // noOcclusion, which would stop the plate's underside ever culling against the floor.
    public static final DeferredHolder<Block, Block> IRON_SPIKES = Registration.BLOCKS.register("iron_spikes",
            () -> new SpikesBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, Block> DARK_IRON_SPIKES = Registration.BLOCKS.register("dark_iron_spikes",
            () -> new SpikesBlock(Properties.ofFullCopy(IRON_SPIKES.get())));

    // Portcullis: lattice cells that join into a gate, and the winch above that raises it. See
    // PortcullisWinchBlock for how the two find each other. Iron bars' strength, and noOcclusion
    // on the lattice because the gate is mostly holes.
    public static final DeferredHolder<Block, Block> PORTCULLIS = Registration.BLOCKS.register("portcullis",
            () -> new PortcullisBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredHolder<Block, Block> PORTCULLIS_WINCH = Registration.BLOCKS.register("portcullis_winch",
            () -> new PortcullisWinchBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));

    // Dungeon furniture: props with moving parts, modelled in Blockbench (blockbench/*.bbmodel,
    // built into models by tools/bbmodel_to_block_models.py)
    public static final DeferredHolder<Block, Block> STONE_SARCOPHAGUS = Registration.BLOCKS.register("stone_sarcophagus",
            () -> new SarcophagusBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredHolder<Block, Block> DEEPSLATE_SARCOPHAGUS = Registration.BLOCKS.register("deepslate_sarcophagus",
            () -> new SarcophagusBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));
    public static final DeferredHolder<Block, Block> IRON_MAIDEN = Registration.BLOCKS.register("iron_maiden",
            () -> new IronMaidenBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredHolder<Block, Block> GIBBET = Registration.BLOCKS.register("gibbet",
            () -> new GibbetBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    // the pillory and the rack, dark oak, each empty and with a skeleton in it: the occupied one is
    // the same block with other models. Wood's strength and sound, and no tool needed.
    public static final DeferredHolder<Block, Block> PILLORY = Registration.BLOCKS.register("pillory",
            () -> new PilloryBlock(tortureWood()));
    public static final DeferredHolder<Block, Block> OCCUPIED_PILLORY = Registration.BLOCKS.register("occupied_pillory",
            () -> new PilloryBlock(tortureWood()));
    public static final DeferredHolder<Block, Block> TORTURE_RACK = Registration.BLOCKS.register("torture_rack",
            () -> new TortureRackBlock(tortureWood()));
    public static final DeferredHolder<Block, Block> OCCUPIED_TORTURE_RACK = Registration.BLOCKS.register("occupied_torture_rack",
            () -> new TortureRackBlock(tortureWood()));
    // Racks in the brazier's ironwork, with the brazier's leniency: no tool needed to take one
    // down. The firewood rack is mostly logs to look at and to hit, so wood's strength and sound;
    // the weapon rack is all iron, and holds its weapons in a block entity.
    public static final DeferredHolder<Block, Block> FIREWOOD_RACK = Registration.BLOCKS.register("firewood_rack",
            () -> new FirewoodRackBlock(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
                    .forceSolidOn().strength(2.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredHolder<Block, Block> WEAPON_RACK = Registration.BLOCKS.register("weapon_rack",
            () -> new WeaponRackBlock(Properties.of().mapColor(MapColor.METAL)
                    .forceSolidOn().strength(3.5F).sound(SoundType.METAL).noOcclusion()));

    // Coffins: the sarcophagus in wood - the same block entity, sealing and guardian - with a
    // hinged lid. One per dungeon-door wood. Planks' properties, which carry no AXIS to trip on.
    public static final List<DeferredHolder<Block, Block>> COFFINS = new ArrayList<>();
    public static final DeferredHolder<Block, Block> SPRUCE_COFFIN = coffin("spruce", Blocks.SPRUCE_PLANKS);
    public static final DeferredHolder<Block, Block> DARK_OAK_COFFIN = coffin("dark_oak", Blocks.DARK_OAK_PLANKS);
    public static final DeferredHolder<Block, Block> CRIMSON_COFFIN = coffin("crimson", Blocks.CRIMSON_PLANKS);
    public static final DeferredHolder<Block, Block> MANGROVE_COFFIN = coffin("mangrove", Blocks.MANGROVE_PLANKS);

    private static DeferredHolder<Block, Block> coffin(String wood, Block planks) {
        DeferredHolder<Block, Block> block = Registration.BLOCKS.register(wood + "_coffin",
                () -> new CoffinBlock(Properties.ofFullCopy(planks)));
        COFFINS.add(block);
        return block;
    }

    // A skull on a pike, two blocks tall. Wood's strength and sound, spelled out: see the note on
    // the sharpened logs about copying a log's properties.
    public static final DeferredHolder<Block, Block> SKULL_PIKE = Registration.BLOCKS.register("skull_pike",
            () -> new TallPropBlock(Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY),
                    TallPropBlock.PIKE_POLE, TallPropBlock.PIKE_SKULL));
    // other heads on the same pike, the same size as the skull: a zombie's, and Steve's, fresh
    public static final DeferredHolder<Block, Block> ZOMBIE_HEAD_PIKE = Registration.BLOCKS.register("zombie_head_pike",
            () -> new TallPropBlock(Properties.ofFullCopy(SKULL_PIKE.get()), TallPropBlock.PIKE_POLE, TallPropBlock.PIKE_SKULL));
    public static final DeferredHolder<Block, Block> BLOODY_STEVE_HEAD_PIKE = Registration.BLOCKS.register("bloody_steve_head_pike",
            () -> new TallPropBlock(Properties.ofFullCopy(SKULL_PIKE.get()), TallPropBlock.PIKE_POLE, TallPropBlock.PIKE_SKULL));

    // The secret passage: a torch sconce that is a lever, doors that are wall until redstone opens
    // them, and a pedestal to put the prize on. The lever sconce copies the torch sconce's
    // properties exactly, so nothing about it gives it away.
    public static final DeferredHolder<Block, Block> LEVER_SCONCE = Registration.BLOCKS.register("lever_sconce",
            () -> new LeverSconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)
                    .noCollission().lightLevel(state -> 14).sound(SoundType.WOOD)));
    public static final DeferredHolder<Block, Block> PEDESTAL = Registration.BLOCKS.register("pedestal",
            () -> new PedestalBlock(Properties.ofFullCopy(Blocks.POLISHED_ANDESITE).noOcclusion()));
    // One hidden door per wall stone Dungeons2 builds in - its motifs' walls, and the cracked and
    // mossy stone bricks its weathering turns them into - each copying its stone's properties.
    public static final Map<DeferredHolder<Block, Block>, Supplier<Block>> HIDDEN_DOORS = new LinkedHashMap<>();

    static {
        hiddenDoor("stone_bricks", () -> Blocks.STONE_BRICKS);
        hiddenDoor("mossy_stone_bricks", () -> Blocks.MOSSY_STONE_BRICKS);
        hiddenDoor("cracked_stone_bricks", () -> Blocks.CRACKED_STONE_BRICKS);
        hiddenDoor("bricks", () -> Blocks.BRICKS);
        hiddenDoor("deepslate_bricks", () -> Blocks.DEEPSLATE_BRICKS);
        hiddenDoor("mud_bricks", () -> Blocks.MUD_BRICKS);
    }

    /** Registers "<source>_hidden_door", singular like the capstones: stone_brick_hidden_door. */
    private static void hiddenDoor(String source, Supplier<Block> block) {
        String name = source.endsWith("bricks") ? source.substring(0, source.length() - 1) : source;
        HIDDEN_DOORS.put(Registration.BLOCKS.register(name + "_hidden_door",
                () -> new HiddenDoorBlock(Properties.ofFullCopy(block.get()).noOcclusion().pushReaction(PushReaction.DESTROY))), block);
    }

    // Loose stone chips strewn on a floor, a carpet that thickens as more is laid on it: it needs
    // something under it.
    public static final DeferredHolder<Block, Block> RUBBLE_SCATTER = Registration.BLOCKS.register("rubble_scatter",
            () -> new RubbleScatterBlock(Properties.of().mapColor(MapColor.STONE).strength(0.5F).sound(SoundType.STONE)
                    .noOcclusion().pushReaction(PushReaction.DESTROY)));

    // Gargoyles: the Monster Manual's gargoyle mob, its own model baked in stone
    // (tools/entity_model.py). Stone's properties, the need for a pickaxe included.
    public static final DeferredHolder<Block, Block> PERCHED_GARGOYLE = Registration.BLOCKS.register("perched_gargoyle",
            () -> new StatueBlock(Properties.ofFullCopy(Blocks.STONE).noOcclusion(), 2, 2, 14, 14, 15.5));
    public static final DeferredHolder<Block, Block> GARGOYLE_BUST = Registration.BLOCKS.register("gargoyle_bust",
            () -> new StatueBlock(Properties.ofFullCopy(Blocks.STONE).noOcclusion(), 2, 4, 14, 12, 14.5));
    public static final DeferredHolder<Block, Block> GARGOYLE_STATUE = Registration.BLOCKS.register("gargoyle_statue",
            () -> new TallPropBlock(Properties.ofFullCopy(Blocks.STONE).noOcclusion(),
                    Shapes.or(Block.box(1, 0, 1, 15, 3, 15), Block.box(3, 3, 3, 13, 16, 13)),
                    Block.box(2, 0, 2, 14, 15.75, 14)));
    // A heap of loose bones, filled in stages. Breaks quickly by hand, like a pile of anything loose.
    public static final DeferredHolder<Block, Block> BONE_PILE = Registration.BLOCKS.register("bone_pile",
            () -> new BonePileBlock(Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.BONE_BLOCK)
                    .noOcclusion().pushReaction(PushReaction.DESTROY)));
    // An iron wheel of candles hung from the ceiling. The dungeon lantern's strength and sound, but
    // like the sconces it needs no tool to take down.
    public static final DeferredHolder<Block, Block> CHANDELIER = Registration.BLOCKS.register("chandelier",
            () -> new ChandelierBlock(Properties.of().mapColor(MapColor.METAL).strength(3.5F).sound(SoundType.LANTERN)
                    .lightLevel(ChandelierBlock.LIGHT_EMISSION).noOcclusion().pushReaction(PushReaction.DESTROY)));
    // Chain fixtures, hung as blocks under a vanilla chain or from a ceiling, as a lantern hangs. On
    // a swinging chain they are its fixture instead (SwingingChainBlock#use), drawn from the same
    // models. The chandelier's strength and leniency. Each shape is its model's: a thin chain or
    // eye above, the fixture's bulk below.
    public static final DeferredHolder<Block, Block> MANACLES = Registration.BLOCKS.register("manacles",
            () -> new HangingFixtureBlock(fixtureProperties(SoundType.CHAIN),
                    Shapes.or(Block.box(6, 7, 6, 10, 16, 10), Block.box(0, 0, 5.5, 14.5, 7.5, 10.5))));
    public static final DeferredHolder<Block, Block> MEAT_HOOK = Registration.BLOCKS.register("meat_hook",
            () -> new HangingFixtureBlock(fixtureProperties(SoundType.CHAIN),
                    Shapes.or(Block.box(6.5, 7.5, 6.5, 9.5, 16, 9.5), Block.box(5.5, 2, 5.5, 10.5, 7.5, 10.5))));
    public static final DeferredHolder<Block, Block> CENSER = Registration.BLOCKS.register("censer",
            () -> new CenserBlock(fixtureProperties(SoundType.LANTERN).lightLevel(CenserBlock.LIGHT_EMISSION),
                    Shapes.or(Block.box(7, 10, 7, 9, 16, 9), Block.box(5, 1.5, 5, 11, 10, 11))));

    // A cauldron of coloured brew at a boil. Vanilla cauldron's properties exactly, so it needs a
    // pickaxe to drop, as a cauldron does (tagged by hand in ModBlockTagGenerator).
    public static final DeferredHolder<Block, Block> BUBBLING_CAULDRON = Registration.BLOCKS.register("bubbling_cauldron",
            () -> new BubblingCauldronBlock(Properties.ofFullCopy(Blocks.CAULDRON)));

    private static Properties fixtureProperties(SoundType sound) {
        return Properties.of().mapColor(MapColor.METAL).strength(3.5F).sound(sound).noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    // bones & bodies
    // copy(STONE) alone left canOcclude=true, which is wrong for a 6px-tall sprawl: it culled the
    // neighbouring faces, shaded the bones as if they filled the cell, and hid the water of a
    // waterlogged skeleton entirely. noOcclusion() is what makes the waterlogging visible.
    public static final DeferredHolder<Block, Block> SKELETON = Registration.BLOCKS.register("skeleton",
            () -> new SkeletonBlock(Block.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.BONE_BLOCK)));
    
    // Capstones: the pyramid point in a stone, to cap a pillar, a tower or a gatepost. One for
    // every overworld bricks block (chiseled ones excepted - their carving would be sliced
    // diagonally across the facets - and the two-block large bricks), plus polished blackstone,
    // polished andesite and the sandstones. Nether, end, prismarine and quartz bricks were
    // deliberately left out. Declared last so every mod source block is registered before them. Each
    // takes its source's properties, and datagen reads its texture off the source's id.
    public static final Map<DeferredHolder<Block, Block>, Supplier<Block>> CAPSTONES = new LinkedHashMap<>();

    static {
        capstone("bricks", () -> Blocks.BRICKS);
        capstone("stone_bricks", () -> Blocks.STONE_BRICKS);
        capstone("mossy_stone_bricks", () -> Blocks.MOSSY_STONE_BRICKS);
        capstone("cracked_stone_bricks", () -> Blocks.CRACKED_STONE_BRICKS);
        capstone("deepslate_bricks", () -> Blocks.DEEPSLATE_BRICKS);
        capstone("cracked_deepslate_bricks", () -> Blocks.CRACKED_DEEPSLATE_BRICKS);
        capstone("polished_blackstone_bricks", () -> Blocks.POLISHED_BLACKSTONE_BRICKS);
        capstone("cracked_polished_blackstone_bricks", () -> Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        capstone("mud_bricks", () -> Blocks.MUD_BRICKS);
        capstone("polished_blackstone", () -> Blocks.POLISHED_BLACKSTONE);
        capstone("polished_andesite", () -> Blocks.POLISHED_ANDESITE);
        capstone("sandstone", () -> Blocks.SANDSTONE);
        capstone("cut_sandstone", () -> Blocks.CUT_SANDSTONE);
        capstone("smooth_sandstone", () -> Blocks.SMOOTH_SANDSTONE);
        capstone("red_sandstone", () -> Blocks.RED_SANDSTONE);
        capstone("cut_red_sandstone", () -> Blocks.CUT_RED_SANDSTONE);
        capstone("smooth_red_sandstone", () -> Blocks.SMOOTH_RED_SANDSTONE);
        for (DeferredHolder<Block, Block> source : List.of(SQUARE_STONE_BRICK, MOSSY_SQUARE_STONE_BRICK,
                SQUARE_MUD_BRICK, MOSSY_SQUARE_MUD_BRICK, SQUARE_DEEPSLATE_BRICK, MOSSY_SQUARE_DEEPSLATE_BRICK,
                MOSSY_DEEPSLATE_BRICKS, POLISHED_ANDESITE_BRICKS, MOSSY_POLISHED_ANDESITE_BRICKS, MOSSY_BRICKS,
                LARGE_BRICKS, MOSSY_LARGE_BRICKS, SQUARE_BRICK, MOSSY_SQUARE_BRICK, COBBLESTONE_BRICK,
                MOSSY_COBBLESTONE_BRICK, GRAVEL_BRICK)) {
            capstone(source.getId().getPath(), source);
        }
    }

    /** Registers "<source>_capstone", singular like vanilla's stairs: stone_bricks -> stone_brick_capstone. */
    private static void capstone(String source, Supplier<Block> block) {
        String name = source.endsWith("bricks") ? source.substring(0, source.length() - 1) : source;
        CAPSTONES.put(Registration.BLOCKS.register(name + "_capstone",
                () -> new PyramidBlock(Properties.ofFullCopy(block.get()))), block);
    }

    // Tapestries: woven scenes hung on a rod, 4 wide and 3 tall, each pristine and worn (textures:
    // tools/gen_tapestry_textures.py). Cloth: wool's strength and sound, no tool, it burns.
    public static final List<DeferredHolder<Block, Block>> TAPESTRIES = new ArrayList<>();
    public static final DeferredHolder<Block, Block> DRAGON_TAPESTRY = tapestry("dragon_tapestry");
    public static final DeferredHolder<Block, Block> WORN_DRAGON_TAPESTRY = tapestry("worn_dragon_tapestry");
    public static final DeferredHolder<Block, Block> HUNT_TAPESTRY = tapestry("hunt_tapestry");
    public static final DeferredHolder<Block, Block> WORN_HUNT_TAPESTRY = tapestry("worn_hunt_tapestry");
    public static final DeferredHolder<Block, Block> NECROMANCER_TAPESTRY = tapestry("necromancer_tapestry");
    public static final DeferredHolder<Block, Block> WORN_NECROMANCER_TAPESTRY = tapestry("worn_necromancer_tapestry");
    public static final DeferredHolder<Block, Block> SUMMONING_TAPESTRY = tapestry("summoning_tapestry");
    public static final DeferredHolder<Block, Block> WORN_SUMMONING_TAPESTRY = tapestry("worn_summoning_tapestry");

    private static DeferredHolder<Block, Block> tapestry(String name) {
        DeferredHolder<Block, Block> block = Registration.BLOCKS.register(name,
                () -> new TapestryBlock(Properties.of().mapColor(MapColor.COLOR_BLUE).strength(0.8F).sound(SoundType.WOOL)
                        .noCollission().noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY)));
        TAPESTRIES.add(block);
        return block;
    }

    // Crumbling floors: a stone with faint cracks that gives way when stepped on, spreading through
    // every crumbling block touching it. The stones Dungeons2 floors with (textures:
    // tools/gen_crumbling_floor_textures.py). Each copies its stone's properties; datagen reads the
    // texture off the id. Adding one is a line here and a row in the generator.
    public static final Map<DeferredHolder<Block, Block>, Supplier<Block>> CRUMBLING_FLOORS = new LinkedHashMap<>();

    static {
        crumblingFloor("stone_bricks", () -> Blocks.STONE_BRICKS);
        crumblingFloor("mossy_stone_bricks", () -> Blocks.MOSSY_STONE_BRICKS);
        crumblingFloor("cracked_stone_bricks", () -> Blocks.CRACKED_STONE_BRICKS);
        crumblingFloor("cobblestone", () -> Blocks.COBBLESTONE);
        crumblingFloor("mossy_cobblestone", () -> Blocks.MOSSY_COBBLESTONE);
        crumblingFloor("polished_andesite", () -> Blocks.POLISHED_ANDESITE);
        crumblingFloor("deepslate_bricks", () -> Blocks.DEEPSLATE_BRICKS);
        crumblingFloor("deepslate_tiles", () -> Blocks.DEEPSLATE_TILES);
        crumblingFloor("mud_bricks", () -> Blocks.MUD_BRICKS);
    }

    /** Registers "crumbling_<source>": crumbling_stone_bricks, as vanilla says cracked_stone_bricks. */
    private static void crumblingFloor(String source, Supplier<Block> block) {
        CRUMBLING_FLOORS.put(Registration.BLOCKS.register("crumbling_" + source,
                () -> new CrumblingFloorBlock(Properties.ofFullCopy(block.get()))), block);
    }

    // Catacomb niches: a burial recess in a wall, with a skull and bones in it. A curated handful of
    // crypt stones rather than every material - tuff because the Roman catacombs are cut in it,
    // sandstone for desert tombs. Adding one is a line here. Each copies its source's properties,
    // and datagen reads the stone texture off the source's id, as for the capstones.
    public static final Map<DeferredHolder<Block, Block>, Supplier<Block>> CATACOMB_NICHES = new LinkedHashMap<>();

    static {
        catacombNiche("stone_bricks", () -> Blocks.STONE_BRICKS);
        catacombNiche("mossy_stone_bricks", () -> Blocks.MOSSY_STONE_BRICKS);
        catacombNiche("cracked_stone_bricks", () -> Blocks.CRACKED_STONE_BRICKS);
        catacombNiche("deepslate_bricks", () -> Blocks.DEEPSLATE_BRICKS);
        catacombNiche("cracked_deepslate_bricks", () -> Blocks.CRACKED_DEEPSLATE_BRICKS);
        catacombNiche("tuff", () -> Blocks.TUFF);
        catacombNiche("sandstone", () -> Blocks.SANDSTONE);
    }

    /** Registers "<source>_catacomb_niche", singular like the capstones: stone_brick_catacomb_niche. */
    private static void catacombNiche(String source, Supplier<Block> block) {
        String name = source.endsWith("bricks") ? source.substring(0, source.length() - 1) : source;
        CATACOMB_NICHES.put(Registration.BLOCKS.register(name + "_catacomb_niche",
                () -> new CatacombNicheBlock(Properties.ofFullCopy(block.get()))), block);
    }

    // ------------------------------------------------------------------
    // Decorative block-types (DecorType) in their materials (ModMaterials).
    // Add a material   -> one entry in ModMaterials.STONE.
    // Add a block-type -> one DecorType value (and a datagen case for it).
    // Every block made here is recorded in DECOR, which datagen iterates.
    // ------------------------------------------------------------------

    public static final List<DecorType.DecorBlock> DECOR = new ArrayList<>();

    private static void decor(ModMaterials.Material material, DecorType type, Block propsFrom) {
        DECOR.add(new DecorType.DecorBlock(material, type, Registration.BLOCKS.register(type.id(material),
                () -> type.create(Properties.ofFullCopy(propsFrom)))));
    }

    static {
        for (ModMaterials.Material m : ModMaterials.STONE) {
            for (DecorType type : DecorType.STONE_TYPES) {
                decor(m, type, m.base());
            }
        }
    }

    /**
     * The block-types only some materials have. Called by Registration after every block above,
     * which fixes their place in the registration (and creative tab) order.
     */
    public static void registerTrimDecor() {
        ModMaterials.BARRED_WINDOWS.forEach(name -> decor(ModMaterials.get(name), DecorType.BARRED_WINDOW, ModMaterials.get(name).base()));
        ModMaterials.BARRED_WINDOWS.forEach(name -> decor(ModMaterials.get(name), DecorType.BARRED_WINDOW_FACADE, ModMaterials.get(name).base()));
        ModMaterials.LEDGES.forEach(name -> decor(ModMaterials.get(name), DecorType.LEDGE,
                ModMaterials.LEDGE_PROPS.getOrDefault(name, ModMaterials.get(name).base())));
        ModMaterials.CORBELS.forEach(name -> decor(ModMaterials.get(name), DecorType.CORBEL, ModMaterials.get(name).base()));
    }

    /** The decorative block of this type in this material. */
    public static DeferredHolder<Block, Block> decor(String material, DecorType type) {
        return DECOR.stream().filter(d -> d.type() == type && d.material().name().equals(material))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no " + type + " in " + material)).block();
    }

    private static Properties tortureWood() {
        return Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.5F, 3.0F).sound(SoundType.WOOD)
                .ignitedByLava().noOcclusion();
    }

    public static void register(IEventBus bus) {
        Registration.registerBlocks(bus);
    }

}

