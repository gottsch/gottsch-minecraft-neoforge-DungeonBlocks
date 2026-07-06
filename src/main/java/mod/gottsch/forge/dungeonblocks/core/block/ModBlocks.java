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

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Maps;

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import mod.gottsch.neo.gottschcore.block.FacingBlock;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @author Mark Gottschling on Jan 12, 2020
 */
public class ModBlocks {
    // map from registry block to registry item
    public static final Map<DeferredHolder<Block, ? extends Block>, DeferredHolder<Item, ? extends Item>> MAP = Maps.newHashMap();

    // ------------------------------------------------------------------
    // Copper helpers. Weathering copper has 4 ages (unaffected -> exposed ->
    // weathered -> oxidized) plus a waxed counterpart per age. Properties are
    // always copied from an EXPLICIT source block named right at the call site,
    // so a waxed block can never accidentally copy the wrong weather state.
    // ------------------------------------------------------------------

    /** Register a weathering-copper block of the given age, copying properties from {@code propsFrom}. */
    private static DeferredHolder<Block, Block> weathering(String id, DeferredHolder<Block, Block> propsFrom,
            WeatherState age, BiFunction<WeatherState, Properties, Block> factory) {
        return Registration.BLOCKS.register(id, () -> factory.apply(age, Properties.ofFullCopy(propsFrom.get())));
    }

    /** Register a block whose properties are copied from {@code propsFrom} (waxed copper variants, etc.). */
    private static DeferredHolder<Block, Block> copperLike(String id, DeferredHolder<Block, Block> propsFrom,
            Function<Properties, Block> factory) {
        return Registration.BLOCKS.register(id, () -> factory.apply(Properties.ofFullCopy(propsFrom.get())));
    }

    // NEW 10/26/2023
    // wall sconce
    public static final DeferredHolder<Block, Block> TORCH_SCONCE = Registration.BLOCKS.register("torch_sconce_block",
            () -> new TorchSconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)
                    .noCollission().lightLevel((light) -> {
                        return 14;
                    }).sound(SoundType.WOOD)));

    public static final DeferredHolder<Block, Block> CANDLE_SCONCE = Registration.BLOCKS.register("candle_sconce_block",
            () -> new SconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion().lightLevel(SconceBlock.LIGHT_EMISSION)));
    public static final DeferredHolder<Block, Block> DUNGEON_LANTERN = Registration.BLOCKS.register("dungeon_lantern", () -> new DungeonLanternBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(DungeonLanternBlock.LIGHT_EMISSION).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredHolder<Block, Block> BRAZIER = Registration.BLOCKS.register("brazier_block", () -> new BrazierBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().strength(3.5F).sound(SoundType.METAL).lightLevel(BrazierBlock.LIGHT_EMISSION).noOcclusion()));

    // grate
    public static final DeferredHolder<Block, Block> DARK_IRON_GRATE = Registration.BLOCKS.register("dark_iron_grate", () -> new HeavyGrateBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()));

    public static final DeferredHolder<Block, Block> COPPER_GRATE = Registration.BLOCKS.register("copper_grate", () -> {
        return new WeatheringCopperGrateBlock(WeatheringCopper.WeatherState.UNAFFECTED, Properties.of().strength(3.0F, 6.0F).sound(SoundType.COPPER).mapColor(MapColor.WARPED_STEM).noOcclusion().requiresCorrectToolForDrops().isValidSpawn((a, b, c, d) -> {
            return false;
        }).isRedstoneConductor((a, b, c) -> {
            return false;
        }).isSuffocating((a, b, c) -> {
            return false;
        }).isViewBlocking((a, b, c) -> {
            return false;
        }));
    });
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_GRATE = copperLike("exposed_copper_grate", COPPER_GRATE,
            p -> new WeatheringCopperGrateBlock(WeatherState.EXPOSED, p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_GRATE = copperLike("weathered_copper_grate", COPPER_GRATE,
            p -> new WeatheringCopperGrateBlock(WeatherState.WEATHERED, p.mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_GRATE = copperLike("oxidized_copper_grate", COPPER_GRATE,
            p -> new WeatheringCopperGrateBlock(WeatherState.OXIDIZED, p.mapColor(MapColor.WARPED_NYLIUM)));
    public static final DeferredHolder<Block, Block> WAXED_COPPER_GRATE = copperLike("waxed_copper_grate", COPPER_GRATE, WaterloggedCubeBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_GRATE = copperLike("waxed_exposed_copper_grate", EXPOSED_COPPER_GRATE, WaterloggedCubeBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_GRATE = copperLike("waxed_weathered_copper_grate", WEATHERED_COPPER_GRATE, WaterloggedCubeBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_GRATE = copperLike("waxed_oxidized_copper_grate", OXIDIZED_COPPER_GRATE, WaterloggedCubeBlock::new);

    public static final DeferredHolder<Block, Block> COPPER_HEAVY_GRATE = weathering("copper_heavy_grate", COPPER_GRATE, WeatherState.UNAFFECTED, WeatheringHeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_HEAVY_GRATE = weathering("exposed_copper_heavy_grate", EXPOSED_COPPER_GRATE, WeatherState.EXPOSED, WeatheringHeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_HEAVY_GRATE = weathering("weathered_copper_heavy_grate", WEATHERED_COPPER_GRATE, WeatherState.WEATHERED, WeatheringHeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_HEAVY_GRATE = weathering("oxidized_copper_heavy_grate", OXIDIZED_COPPER_GRATE, WeatherState.OXIDIZED, WeatheringHeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_HEAVY_GRATE = copperLike("waxed_copper_heavy_grate", COPPER_GRATE, HeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_HEAVY_GRATE = copperLike("waxed_exposed_copper_heavy_grate", EXPOSED_COPPER_GRATE, HeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_HEAVY_GRATE = copperLike("waxed_weathered_copper_heavy_grate", WEATHERED_COPPER_GRATE, HeavyGrateBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_HEAVY_GRATE = copperLike("waxed_oxidized_copper_heavy_grate", OXIDIZED_COPPER_GRATE, HeavyGrateBlock::new);

    // valve wheel
    public static final DeferredHolder<Block, Block> COPPER_VALVE_WHEEL = weathering("copper_valve_wheel", COPPER_GRATE, WeatherState.UNAFFECTED, WeatheringCopperValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_VALVE_WHEEL = weathering("exposed_copper_valve_wheel", EXPOSED_COPPER_GRATE, WeatherState.EXPOSED, WeatheringCopperValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_VALVE_WHEEL = weathering("weathered_copper_valve_wheel", WEATHERED_COPPER_GRATE, WeatherState.WEATHERED, WeatheringCopperValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_VALVE_WHEEL = weathering("oxidized_copper_valve_wheel", OXIDIZED_COPPER_GRATE, WeatherState.OXIDIZED, WeatheringCopperValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_VALVE_WHEEL = copperLike("waxed_copper_valve_wheel", COPPER_GRATE, ValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_VALVE_WHEEL = copperLike("waxed_exposed_copper_valve_wheel", EXPOSED_COPPER_GRATE, ValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_VALVE_WHEEL = copperLike("waxed_weathered_copper_valve_wheel", WEATHERED_COPPER_GRATE, ValveWheelBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_VALVE_WHEEL = copperLike("waxed_oxidized_copper_valve_wheel", OXIDIZED_COPPER_GRATE, ValveWheelBlock::new);

    // trapdoor
    public static final DeferredHolder<Block, Block> COPPER_TRAPDOOR = weathering("copper_trapdoor", COPPER_GRATE, WeatherState.UNAFFECTED, WeatheringCopperTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_TRAPDOOR = weathering("exposed_copper_trapdoor", EXPOSED_COPPER_GRATE, WeatherState.EXPOSED, WeatheringCopperTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_TRAPDOOR = weathering("weathered_copper_trapdoor", WEATHERED_COPPER_GRATE, WeatherState.WEATHERED, WeatheringCopperTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_TRAPDOOR = weathering("oxidized_copper_trapdoor", OXIDIZED_COPPER_GRATE, WeatherState.OXIDIZED, WeatheringCopperTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_TRAPDOOR = copperLike("waxed_copper_trapdoor", COPPER_GRATE, p -> new TrapDoorBlock(BlockSetType.DARK_OAK, p));
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_TRAPDOOR = copperLike("waxed_exposed_copper_trapdoor", EXPOSED_COPPER_GRATE, p -> new TrapDoorBlock(BlockSetType.DARK_OAK, p));
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_TRAPDOOR = copperLike("waxed_weathered_copper_trapdoor", WEATHERED_COPPER_GRATE, p -> new TrapDoorBlock(BlockSetType.DARK_OAK, p));
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_TRAPDOOR = copperLike("waxed_oxidized_copper_trapdoor", OXIDIZED_COPPER_GRATE, p -> new TrapDoorBlock(BlockSetType.DARK_OAK, p));

    // heavy trapdoor
    public static final DeferredHolder<Block, Block> DARK_IRON_HEAVY_TRAPDOOR = Registration.BLOCKS.register("dark_iron_heavy_trapdoor", () -> {
        return new HeavyTrapDoorBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion());
    });

    public static final DeferredHolder<Block, Block> COPPER_HEAVY_TRAPDOOR = weathering("copper_heavy_trapdoor", COPPER_TRAPDOOR, WeatherState.UNAFFECTED, WeatheringHeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_HEAVY_TRAPDOOR = weathering("exposed_copper_heavy_trapdoor", EXPOSED_COPPER_TRAPDOOR, WeatherState.EXPOSED, WeatheringHeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_HEAVY_TRAPDOOR = weathering("weathered_copper_heavy_trapdoor", WEATHERED_COPPER_TRAPDOOR, WeatherState.WEATHERED, WeatheringHeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_HEAVY_TRAPDOOR = weathering("oxidized_copper_heavy_trapdoor", OXIDIZED_COPPER_TRAPDOOR, WeatherState.OXIDIZED, WeatheringHeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_HEAVY_TRAPDOOR = copperLike("waxed_copper_heavy_trapdoor", COPPER_TRAPDOOR, HeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_HEAVY_TRAPDOOR = copperLike("waxed_exposed_copper_heavy_trapdoor", EXPOSED_COPPER_TRAPDOOR, HeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_HEAVY_TRAPDOOR = copperLike("waxed_weathered_copper_heavy_trapdoor", WEATHERED_COPPER_TRAPDOOR, HeavyTrapDoorBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_HEAVY_TRAPDOOR = copperLike("waxed_oxidized_copper_heavy_trapdoor", OXIDIZED_COPPER_TRAPDOOR, HeavyTrapDoorBlock::new);

    public static final DeferredHolder<Block, Block> SQUARE_STONE_BRICK = Registration.BLOCKS.register("square_stone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_STONE_BRICK = Registration.BLOCKS.register("mossy_square_stone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> LEFT_LARGE_STONE_BRICK = Registration.BLOCKS.register("left_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_STONE_BRICK = Registration.BLOCKS.register("right_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_STONE_BRICK = Registration.BLOCKS.register("mossy_left_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_STONE_BRICK = Registration.BLOCKS.register("mossy_right_large_stone_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_BRICKS = Registration.BLOCKS.register("mossy_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_BRICK_STAIRS = Registration.BLOCKS.register("mossy_brick_stairs", () -> {
        return new StairBlock(Blocks.BRICKS.defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, Block> LARGE_BRICKS = Registration.BLOCKS.register("large_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LARGE_BRICKS = Registration.BLOCKS.register("mossy_large_bricks", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, StairBlock> LARGE_BRICK_STAIRS = Registration.BLOCKS.register("large_brick_stairs", () -> {
        return new StairBlock(LARGE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, StairBlock> MOSSY_LARGE_BRICK_STAIRS = Registration.BLOCKS.register("mossy_large_brick_stairs", () -> {
        return new StairBlock(MOSSY_LARGE_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICK_STAIRS));
    });
    public static final DeferredHolder<Block, Block> SQUARE_BRICK = Registration.BLOCKS.register("square_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_SQUARE_BRICK = Registration.BLOCKS.register("mossy_square_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> LEFT_LARGE_BRICK = Registration.BLOCKS.register("left_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> RIGHT_LARGE_BRICK = Registration.BLOCKS.register("right_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_LEFT_LARGE_BRICK = Registration.BLOCKS.register("mossy_left_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });
    public static final DeferredHolder<Block, Block> MOSSY_RIGHT_LARGE_BRICK = Registration.BLOCKS.register("mossy_right_large_brick", () -> {
        return new FacingBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS));
    });

    public static final DeferredHolder<Block, Block> COBBLESTONE_BRICK = Registration.BLOCKS.register("cobblestone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.COBBLESTONE));
    });
    public static final DeferredHolder<Block, Block> MOSSY_COBBLESTONE_BRICK = Registration.BLOCKS.register("mossy_cobblestone_brick", () -> {
        return new Block(Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE));
    });
    public static final DeferredHolder<Block, Block> GRAVEL_BRICK = Registration.BLOCKS.register("gravel_brick", () -> {
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

    // plate bracket
    public static final DeferredHolder<Block, Block> IRON_PLATE_BRACKET = Registration.BLOCKS.register("iron_plate_bracket_block", () -> new PlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_plate_bracket_block", () -> new PlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> COPPER_PLATE_BRACKET = weathering("copper_plate_bracket_block", COPPER_TRAPDOOR, WeatherState.UNAFFECTED, WeatheringPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_PLATE_BRACKET = weathering("exposed_copper_plate_bracket_block", EXPOSED_COPPER_TRAPDOOR, WeatherState.EXPOSED, WeatheringPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_PLATE_BRACKET = weathering("weathered_copper_plate_bracket_block", WEATHERED_COPPER_TRAPDOOR, WeatherState.WEATHERED, WeatheringPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_PLATE_BRACKET = weathering("oxidized_copper_plate_bracket_block", OXIDIZED_COPPER_TRAPDOOR, WeatherState.OXIDIZED, WeatheringPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_PLATE_BRACKET = copperLike("waxed_copper_plate_bracket_block", COPPER_GRATE, PlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_PLATE_BRACKET = copperLike("waxed_exposed_copper_plate_bracket_block", EXPOSED_COPPER_GRATE, PlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_PLATE_BRACKET = copperLike("waxed_weathered_copper_plate_bracket_block", WEATHERED_COPPER_GRATE, PlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_PLATE_BRACKET = copperLike("waxed_oxidized_copper_plate_bracket_block", OXIDIZED_COPPER_GRATE, PlateBracketBlock::new);

    // angle/elbow plate bracket
    public static final DeferredHolder<Block, Block> IRON_ANGLE_PLATE_BRACKET = Registration.BLOCKS.register("iron_angle_plate_bracket_block", () -> new AnglePlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_ANGLE_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_angle_plate_bracket_block", () -> new AnglePlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> COPPER_ANGLE_PLATE_BRACKET = weathering("copper_angle_plate_bracket_block", COPPER_TRAPDOOR, WeatherState.UNAFFECTED, WeatheringAnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_ANGLE_PLATE_BRACKET = weathering("exposed_copper_angle_plate_bracket_block", EXPOSED_COPPER_TRAPDOOR, WeatherState.EXPOSED, WeatheringAnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_ANGLE_PLATE_BRACKET = weathering("weathered_copper_angle_plate_bracket_block", WEATHERED_COPPER_TRAPDOOR, WeatherState.WEATHERED, WeatheringAnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_ANGLE_PLATE_BRACKET = weathering("oxidized_copper_angle_plate_bracket_block", OXIDIZED_COPPER_TRAPDOOR, WeatherState.OXIDIZED, WeatheringAnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_ANGLE_PLATE_BRACKET = copperLike("waxed_copper_angle_plate_bracket_block", COPPER_GRATE, AnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_ANGLE_PLATE_BRACKET = copperLike("waxed_exposed_copper_angle_plate_bracket_block", EXPOSED_COPPER_GRATE, AnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_ANGLE_PLATE_BRACKET = copperLike("waxed_weathered_copper_angle_plate_bracket_block", WEATHERED_COPPER_GRATE, AnglePlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_ANGLE_PLATE_BRACKET = copperLike("waxed_oxidized_copper_angle_plate_bracket_block", OXIDIZED_COPPER_GRATE, AnglePlateBracketBlock::new);


    // corner plate bracket
    public static final DeferredHolder<Block, Block> IRON_CORNER_PLATE_BRACKET = Registration.BLOCKS.register("iron_corner_plate_bracket_block", () -> new CornerPlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final DeferredHolder<Block, Block> DARK_IRON_CORNER_PLATE_BRACKET = Registration.BLOCKS.register("dark_iron_corner_plate_bracket_block", () -> new CornerPlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));

    public static final DeferredHolder<Block, Block> COPPER_CORNER_PLATE_BRACKET = weathering("copper_corner_plate_bracket_block", COPPER_TRAPDOOR, WeatherState.UNAFFECTED, WeatheringCornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_CORNER_PLATE_BRACKET = weathering("exposed_copper_corner_plate_bracket_block", EXPOSED_COPPER_TRAPDOOR, WeatherState.EXPOSED, WeatheringCornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_CORNER_PLATE_BRACKET = weathering("weathered_copper_corner_plate_bracket_block", WEATHERED_COPPER_TRAPDOOR, WeatherState.WEATHERED, WeatheringCornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_CORNER_PLATE_BRACKET = weathering("oxidized_copper_corner_plate_bracket_block", OXIDIZED_COPPER_TRAPDOOR, WeatherState.OXIDIZED, WeatheringCornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_COPPER_CORNER_PLATE_BRACKET = copperLike("waxed_copper_corner_plate_bracket_block", COPPER_GRATE, CornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_CORNER_PLATE_BRACKET = copperLike("waxed_exposed_copper_corner_plate_bracket_block", EXPOSED_COPPER_GRATE, CornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_WEATHERED_COPPER_CORNER_PLATE_BRACKET = copperLike("waxed_weathered_copper_corner_plate_bracket_block", WEATHERED_COPPER_GRATE, CornerPlateBracketBlock::new);
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_CORNER_PLATE_BRACKET = copperLike("waxed_oxidized_copper_corner_plate_bracket_block", OXIDIZED_COPPER_GRATE, CornerPlateBracketBlock::new);

    // hay patches
    public static final DeferredHolder<Block, Block> HAY_PATCH = Registration.BLOCKS.register("hay_patch_block", () -> new CarpetBlock(Properties.ofFullCopy(Blocks.YELLOW_CARPET)));
    public static final DeferredHolder<Block, Block> DIRTY_HAY_PATCH = Registration.BLOCKS.register("dirty_hay_patch_block", () -> new CarpetBlock(Properties.ofFullCopy(Blocks.YELLOW_CARPET)));

    // greek blocks
    public static final DeferredHolder<Block, Block> STONE_GREEK_BLOCK = Registration.BLOCKS.register("stone_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, Block> ANDESITE_GREEK_BLOCK = Registration.BLOCKS.register("andesite_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.ANDESITE)));
    public static final DeferredHolder<Block, Block> POLISHED_BASALT_GREEK_BLOCK = Registration.BLOCKS.register("polished_basalt_greek_block", () -> new FacingBlock(Properties.ofFullCopy(Blocks.POLISHED_BASALT)));

    // doors
    public static final DeferredHolder<Block, Block> SPRUCE_DUNGEON_DOOR = Registration.BLOCKS.register("spruce_dungeon_door", () -> new DungeonDoorBlock(Properties.ofFullCopy(Blocks.SPRUCE_DOOR), BlockSetType.SPRUCE));
    public static final DeferredHolder<Block, Block> CRIMSON_DUNGEON_DOOR = Registration.BLOCKS.register("crimson_dungeon_door", () -> new DungeonDoorBlock(Properties.ofFullCopy(Blocks.CRIMSON_DOOR), BlockSetType.CRIMSON));
    public static final DeferredHolder<Block, Block> DARK_OAK_DUNGEON_DOOR = Registration.BLOCKS.register("dark_oak_dungeon_door", () -> new DungeonDoorBlock(Properties.ofFullCopy(Blocks.DARK_OAK_DOOR), BlockSetType.DARK_OAK));
    public static final DeferredHolder<Block, Block> MANGROVE_DUNGEON_DOOR = Registration.BLOCKS.register("mangrove_dungeon_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(Blocks.MANGROVE_DOOR)));

    public static final DeferredHolder<Block, Block> COPPER_DOOR = Registration.BLOCKS.register("copper_door", () -> {
        return new WeatheringCopperDoorBlock(BlockSetType.IRON, WeatheringCopper.WeatherState.UNAFFECTED, Properties.ofFullCopy(Blocks.DARK_OAK_DOOR).mapColor(Blocks.COPPER_BLOCK.defaultMapColor()).strength(3.0F, 6.0F).sound(SoundType.COPPER));
    });
    public static final DeferredHolder<Block, Block> EXPOSED_COPPER_DOOR = Registration.BLOCKS.register("exposed_copper_door", () -> {
        return new WeatheringCopperDoorBlock(BlockSetType.IRON, WeatheringCopper.WeatherState.EXPOSED, Properties.ofFullCopy((BlockBehaviour)COPPER_DOOR.get()).mapColor(((Block)EXPOSED_COPPER_GRATE.get()).defaultMapColor()));
    });
    public static final DeferredHolder<Block, Block> WEATHERED_COPPER_DOOR = Registration.BLOCKS.register("weathered_copper_door", () -> {
        return new WeatheringCopperDoorBlock(BlockSetType.IRON, WeatheringCopper.WeatherState.WEATHERED, Properties.ofFullCopy((BlockBehaviour)COPPER_DOOR.get()).mapColor(((Block)WEATHERED_COPPER_GRATE.get()).defaultMapColor()));
    });
    public static final DeferredHolder<Block, Block> OXIDIZED_COPPER_DOOR = Registration.BLOCKS.register("oxidized_copper_door", () -> {
        return new WeatheringCopperDoorBlock(BlockSetType.IRON, WeatheringCopper.WeatherState.OXIDIZED, Properties.ofFullCopy((BlockBehaviour)COPPER_DOOR.get()).mapColor(((Block)OXIDIZED_COPPER_GRATE.get()).defaultMapColor()));
    });
    public static final DeferredHolder<Block, Block> WAXED_COPPER_DOOR = Registration.BLOCKS.register("waxed_copper_door", () -> {
        return new WaxedCopperDoorBlock(Properties.ofFullCopy((BlockBehaviour)COPPER_DOOR.get()), BlockSetType.IRON);
    });
    public static final DeferredHolder<Block, Block> WAXED_EXPOSED_COPPER_DOOR = Registration.BLOCKS.register("waxed_exposed_copper_door", () -> {
        return new WaxedCopperDoorBlock(Properties.ofFullCopy((BlockBehaviour)EXPOSED_COPPER_DOOR.get()), BlockSetType.IRON);
    });
    public static final DeferredHolder<Block, Block> WAX_WEATHERED_COPPER_DOOR = Registration.BLOCKS.register("waxed_weathered_copper_door", () -> {
        return new WaxedCopperDoorBlock(Properties.ofFullCopy((BlockBehaviour)WEATHERED_COPPER_DOOR.get()), BlockSetType.IRON);
    });
    public static final DeferredHolder<Block, Block> WAXED_OXIDIZED_COPPER_DOOR = Registration.BLOCKS.register("waxed_oxidized_copper_door", () -> {
        return new WaxedCopperDoorBlock(Properties.ofFullCopy((BlockBehaviour)OXIDIZED_COPPER_DOOR.get()), BlockSetType.IRON);
    });

    // bones & bodies
    public static final DeferredHolder<Block, Block> SKELETON = Registration.BLOCKS.register("skeleton", () -> new SkeletonBlock(Block.Properties.ofFullCopy(Blocks.STONE)));
    
    // ------------------------------------------------------------------
    // Stone block families (data-driven). See ModMaterials.STONE.
    // Add a material   -> one entry in ModMaterials.STONE.
    // Add a block-type -> one register(...) line in this loop.
    // Properties are normalized to Properties.ofFullCopy(material base block).
    // ------------------------------------------------------------------
    static {
        for (ModMaterials.Material m : ModMaterials.STONE) {
            String id = m.name();
            Registration.BLOCKS.register(id + "_facade_block",         () -> new FacadeBlock(m.props()));
            Registration.BLOCKS.register(id + "_quarter_facade_block", () -> new QuarterFacadeBlock(m.props()));
            Registration.BLOCKS.register(id + "_fluted_block",         () -> new FlutedBlock(m.props()));
            Registration.BLOCKS.register(id + "_fluted_facade_block",  () -> new FlutedFacadeBlock(m.props()));
            Registration.BLOCKS.register(id + "_sill_block",           () -> new SillBlock(m.props()));
            Registration.BLOCKS.register(id + "_double_sill_block",    () -> new DoubleSillBlock(m.props()));
            Registration.BLOCKS.register(id + "_cornice_block",        () -> new CorniceBlock(m.props()));
            Registration.BLOCKS.register(id + "_crown_molding_block",  () -> new CrownMoldingBlock(m.props()));
            Registration.BLOCKS.register(id + "_pillar_base_block",    () -> new PillarBaseBlock(m.props()));
            Registration.BLOCKS.register(id + "_pillar_block",         () -> new PillarBlock(m.props()));
            Registration.BLOCKS.register(id + "_arrow_slit_block",     () -> new FacingBlock(m.props()));
        }
    }

    /**
     *
     */
    public static void register(IEventBus bus) {
        Registration.registerBlocks(bus);
    }

}

