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

import java.util.Map;

import com.google.common.collect.Maps;

import mod.gottsch.neo.dungeonblocks.core.config.DungeonBlocksConfig.BlockID;
import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import mod.gottsch.neo.gottschcore.block.FacingBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on Jan 12, 2020
 */
public class ModBlocks {
    // map from registry block to registry item
    public static final Map<RegistryObject<Block>, RegistryObject<Item>> MAP = Maps.newHashMap();

    // NEW 10/26/2023
    // wall sconce
    public static final RegistryObject<Block> TORCH_SCONCE = Registration.BLOCKS.register("torch_sconce_block",
            () -> new TorchSconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)
                    .noCollission().lightLevel((light) -> {
                        return 14;
                    }).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CANDLE_SCONCE = Registration.BLOCKS.register("candle_sconce_block",
            () -> new SconceBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion().lightLevel(SconceBlock.LIGHT_EMISSION)));
    public static final RegistryObject<Block> DUNGEON_LANTERN = Registration.BLOCKS.register("dungeon_lantern", () -> new DungeonLanternBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(DungeonLanternBlock.LIGHT_EMISSION).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> BRAZIER = Registration.BLOCKS.register("brazier_block", () -> new BrazierBlock(Properties.of().mapColor(MapColor.METAL)
            .forceSolidOn().strength(3.5F).sound(SoundType.METAL).lightLevel(BrazierBlock.LIGHT_EMISSION).noOcclusion()));

    // grate
    public static final RegistryObject<Block> DARK_IRON_GRATE = Registration.BLOCKS.register("dark_iron_grate", () -> new GrateBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()));
    public static final RegistryObject<Block> WEATHERED_COPPER_GRATE = Registration.BLOCKS.register("weathered_copper_grate", () -> new GrateBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()));

    // grate trapdoor
    public static final RegistryObject<Block> DARK_IRON_GRATE_TRAPDOOR = Registration.BLOCKS.register("dark_iron_grate_trapdoor", () -> new GrateTrapDoorBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()));
    public static final RegistryObject<Block> WEATHERED_COPPER_GRATE_TRAPDOOR = Registration.BLOCKS.register("weathered_copper_grate_trapdoor", () -> new GrateTrapDoorBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion()));

    // sewer
    public static final RegistryObject<Block> WEATHERED_COPPER_SEWER = Registration.BLOCKS.register("weathered_copper_sewer_block", () -> new SewerBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> TERRACOTTA_SEWER = Registration.BLOCKS.register("terracotta_sewer_block", () -> new SewerBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));


    public static final RegistryObject<Block> WALL_RING = Registration.BLOCKS.register("wall_ring", () -> new WallRingBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> PLATE_BRACKET_BLOCK = Registration.BLOCKS.register("plate_bracket_block", () -> new PlateBracketBlock(Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F)));

    // hay patches
    public static final RegistryObject<Block> HAY_PATCH = Registration.BLOCKS.register("hay_patch_block", () -> new CarpetBlock(Properties.copy(Blocks.YELLOW_CARPET)));
    public static final RegistryObject<Block> DIRTY_HAY_PATCH = Registration.BLOCKS.register("dirty_hay_patch_block", () -> new CarpetBlock(Properties.copy(Blocks.YELLOW_CARPET)));

    // greek blocks
    public static final RegistryObject<Block> STONE_GREEK_BLOCK = Registration.BLOCKS.register("stone_greek_block", () -> new FacingBlock(Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ANDESITE_GREEK_BLOCK = Registration.BLOCKS.register("andesite_greek_block", () -> new FacingBlock(Properties.copy(Blocks.ANDESITE)));
    public static final RegistryObject<Block> POLISHED_BASALT_GREEK_BLOCK = Registration.BLOCKS.register("polished_basalt_greek_block", () -> new FacingBlock(Properties.copy(Blocks.POLISHED_BASALT)));

    // doors
    public static final RegistryObject<Block> SPRUCE_DUNGEON_DOOR = Registration.BLOCKS.register("spruce_dungeon_door", () -> new DungeonDoorBlock(Properties.copy(Blocks.SPRUCE_DOOR), BlockSetType.SPRUCE));
    public static final RegistryObject<Block> CRIMSON_DUNGEON_DOOR = Registration.BLOCKS.register("crimson_dungeon_door", () -> new DungeonDoorBlock(Properties.copy(Blocks.CRIMSON_DOOR), BlockSetType.CRIMSON));
    public static final RegistryObject<Block> DARK_OAK_DUNGEON_DOOR = Registration.BLOCKS.register("dark_oak_dungeon_door", () -> new DungeonDoorBlock(Properties.copy(Blocks.DARK_OAK_DOOR), BlockSetType.DARK_OAK));
    public static final RegistryObject<Block> MANGROVE_DUNGEON_DOOR = Registration.BLOCKS.register("mangrove_dungeon_door", () -> new DoorBlock(Properties.copy(Blocks.MANGROVE_DOOR), BlockSetType.MANGROVE));

    // facade
    public static final RegistryObject<Block> STONE_FACADE = Registration.BLOCKS.register(BlockID.STONE_FACADE_ID, () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_FACADE = Registration.BLOCKS.register(BlockID.COBBLESTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_FACADE = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_FACADE = Registration.BLOCKS.register(BlockID.BRICKS_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.STONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(Properties.copy(Blocks.CRACKED_STONE_BRICKS)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_FACADE = Registration.BLOCKS.register(BlockID.OBSIDIAN_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));

    public static final RegistryObject<Block> SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_FACADE = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_FACADE = Registration.BLOCKS.register(BlockID.GRANITE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_FACADE = Registration.BLOCKS.register(BlockID.DIORITE_FACADE_ID,
            () -> new FacadeBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static final RegistryObject<Block> ANDESITE_FACADE = Registration.BLOCKS.register(BlockID.ANDESITE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));

    public static final RegistryObject<Block> BLACKSTONE_FACADE = Registration.BLOCKS.register(BlockID.BLACKSTONE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_BLACKSTONE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_POLISHED_BLACKSTONE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_FACADE = Registration.BLOCKS.register(BlockID.GILDED_BLACKSTONE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_BLACKSTONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));

    public static final RegistryObject<Block> DEEPSLATE_FACADE = Registration.BLOCKS.register(BlockID.DEEPSLATE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.DEEPSLATE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_FACADE = Registration.BLOCKS.register(BlockID.CRACKED_DEEPSLATE_BRICKS_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_FACADE = Registration.BLOCKS.register(BlockID.COBBLED_DEEPSLATE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_DEEPSLATE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_DEEPSLATE_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_FACADE = Registration.BLOCKS.register(BlockID.DEEPSLATE_TILES_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_FACADE = Registration.BLOCKS.register(BlockID.CRACKED_DEEPSLATE_TILES_FACADE_ID,
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));


    // quarter facade
    public static RegistryObject<Block> STONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.STONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> SMOOTH_STONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> COBBLESTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.COBBLESTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> MOSSY_COBBLESTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static RegistryObject<Block> BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static RegistryObject<Block> STONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.STONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> MOSSY_STONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> CRACKED_STONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> CHISELED_STONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> OBSIDIAN_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.OBSIDIAN_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static RegistryObject<Block> SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> SMOOTH_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> CHISELED_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> CUT_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> RED_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> SMOOTH_RED_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> CHISELED_RED_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static RegistryObject<Block> CUT_RED_SANDSTONE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static RegistryObject<Block> GRANITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.GRANITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> POLISHED_GRANITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> DIORITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.DIORITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> POLISHED_DIORITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> ANDESITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.ANDESITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> POLISHED_ANDESITE_QUARTER_FACADE = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static RegistryObject<Block> BLACKSTONE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static RegistryObject<Block> GILDED_BLACKSTONE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static RegistryObject<Block> DEEPSLATE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static RegistryObject<Block> COBBLED_DEEPSLATE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static RegistryObject<Block> POLISHED_DEEPSLATE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static RegistryObject<Block> CHISELED_DEEPSLATE_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_TILES_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static RegistryObject<Block> CRACKED_DEEPSLATE_TILES_QUARTER_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_QUARTER_FACADE_ID,
            () -> new QuarterFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // fluted
    public static final RegistryObject<Block> STONE_FLUTED = Registration.BLOCKS.register(BlockID.STONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_FLUTED = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_FLUTED = Registration.BLOCKS.register(BlockID.COBBLESTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_FLUTED = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_FLUTED = Registration.BLOCKS.register(BlockID.BRICKS_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_FLUTED = Registration.BLOCKS.register(BlockID.STONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_FLUTED = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_FLUTED = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_FLUTED = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_FLUTED = Registration.BLOCKS.register(BlockID.OBSIDIAN_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_FLUTED = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_FLUTED = Registration.BLOCKS.register(BlockID.GRANITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_FLUTED = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_FLUTED = Registration.BLOCKS.register(BlockID.DIORITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_FLUTED = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_FLUTED = Registration.BLOCKS.register(BlockID.ANDESITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_FLUTED = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_FLUTED_ID,
            () -> new FlutedBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));

    public static final RegistryObject<Block> BLACKSTONE_FLUTED = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_FLUTED = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_FLUTED = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_FLUTED = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_FLUTED = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_FLUTED = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_FLUTED = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_FLUTED = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_FLUTED = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_FLUTED = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_FLUTED = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_FLUTED = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_FLUTED = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_FLUTED_ID,
            () -> new FlutedBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // fluted facade
    public static final RegistryObject<Block> STONE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.STONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(STONE_FLUTED.get())));
    public static final RegistryObject<Block> SMOOTH_STONE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(SMOOTH_STONE_FLUTED.get())));
    public static final RegistryObject<Block> COBBLESTONE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.COBBLESTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.MOSSY_COBBLESTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.STONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.MOSSY_STONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_STONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_STONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.OBSIDIAN_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.SMOOTH_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CUT_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.RED_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.SMOOTH_RED_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_RED_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CUT_RED_SANDSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.GRANITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_GRANITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.DIORITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_DIORITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_FLUTED_FACADE = Registration.BLOCKS.register(BlockID.ANDESITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_ANDESITE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));

    public static final RegistryObject<Block> BLACKSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_FLUTED_FACADE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_FLUTED_FACADE_ID,
            () -> new FlutedFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // sill
    public static final RegistryObject<Block> STONE_SILL = Registration.BLOCKS.register(BlockID.STONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).noOcclusion()));
    public static final RegistryObject<Block> SMOOTH_STONE_SILL = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_SILL = Registration.BLOCKS.register(BlockID.COBBLESTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_SILL = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_SILL = Registration.BLOCKS.register(BlockID.BRICKS_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_SILL = Registration.BLOCKS.register(BlockID.STONE_BRICKS_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_SILL = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_SILL = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_SILL = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_SILL = Registration.BLOCKS.register(BlockID.OBSIDIAN_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_SILL = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_SILL = Registration.BLOCKS.register(BlockID.GRANITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_SILL = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_SILL = Registration.BLOCKS.register(BlockID.DIORITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_SILL = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_SILL = Registration.BLOCKS.register(BlockID.ANDESITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_SILL = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_SILL_ID,
            () -> new SillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_SILL = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_SILL = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_SILL = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_SILL_ID,
            () -> new SillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // double sill
    public static final RegistryObject<Block> STONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.STONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.COBBLESTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.MOSSY_COBBLESTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.STONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.MOSSY_STONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_STONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_STONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.OBSIDIAN_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.SMOOTH_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.SMOOTH_RED_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_RED_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CUT_RED_SANDSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.GRANITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_GRANITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.DIORITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_DIORITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_DOUBLE_SILL = Registration.BLOCKS.register(BlockID.ANDESITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_ANDESITE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_DOUBLE_SILL = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_DOUBLE_SILL_ID,
            () -> new DoubleSillBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));


    // Cornice
    public static final RegistryObject<Block> STONE_CORNICE = Registration.BLOCKS.register(BlockID.STONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_CORNICE = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_CORNICE = Registration.BLOCKS.register(BlockID.COBBLESTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_CORNICE = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_CORNICE = Registration.BLOCKS.register(BlockID.BRICKS_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_CORNICE = Registration.BLOCKS.register(BlockID.STONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_CORNICE = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_CORNICE = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_CORNICE = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_CORNICE = Registration.BLOCKS.register(BlockID.OBSIDIAN_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_CORNICE = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_CORNICE = Registration.BLOCKS.register(BlockID.GRANITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_CORNICE = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_CORNICE = Registration.BLOCKS.register(BlockID.DIORITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_CORNICE = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_CORNICE = Registration.BLOCKS.register(BlockID.ANDESITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_CORNICE = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_CORNICE_ID,
            () -> new CorniceBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_CORNICE = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_CORNICE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_CORNICE = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_CORNICE = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_CORNICE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_CORNICE = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_CORNICE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_CORNICE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_CORNICE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_CORNICE = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_CORNICE = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_CORNICE = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_CORNICE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_CORNICE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_CORNICE_ID,
            () -> new CorniceBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // crown molding
    public static final RegistryObject<Block> STONE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.STONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.COBBLESTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.MOSSY_COBBLESTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.STONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.MOSSY_STONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CRACKED_STONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CHISELED_STONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.OBSIDIAN_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.SMOOTH_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CHISELED_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CUT_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.RED_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.SMOOTH_RED_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CHISELED_RED_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CUT_RED_SANDSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.GRANITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_GRANITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.DIORITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_DIORITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_CROWN_MOLDING = Registration.BLOCKS.register(BlockID.ANDESITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_ANDESITE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_CROWN_MOLDING = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_CROWN_MOLDING_ID,
            () -> new CrownMoldingBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // pillar base
    public static final RegistryObject<Block> STONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.STONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.COBBLESTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.MOSSY_COBBLESTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_PILLAR_BASE = Registration.BLOCKS.register(BlockID.BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(BlockID.STONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.MOSSY_STONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CRACKED_STONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CHISELED_STONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_BASE = Registration.BLOCKS.register(BlockID.OBSIDIAN_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.SMOOTH_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CHISELED_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.SMOOTH_RED_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CHISELED_RED_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CUT_RED_SANDSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.GRANITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_GRANITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.DIORITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_DIORITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_PILLAR_BASE = Registration.BLOCKS.register(BlockID.ANDESITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_ANDESITE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_PILLAR_BASE = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_PILLAR_BASE_ID,
            () -> new PillarBaseBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    // pillar
    public static final RegistryObject<Block> STONE_PILLAR = Registration.BLOCKS.register(BlockID.STONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> SMOOTH_STONE_PILLAR = Registration.BLOCKS.register(BlockID.SMOOTH_STONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> COBBLESTONE_PILLAR = Registration.BLOCKS.register(BlockID.COBBLESTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_PILLAR = Registration.BLOCKS.register(BlockID.MOSSY_COBBLESTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> BRICKS_PILLAR = Registration.BLOCKS.register(BlockID.BRICKS_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> STONE_BRICKS_PILLAR = Registration.BLOCKS.register(BlockID.STONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICKS_PILLAR = Registration.BLOCKS.register(BlockID.MOSSY_STONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CRACKED_STONE_BRICKS_PILLAR = Registration.BLOCKS.register(BlockID.CRACKED_STONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> CHISELED_STONE_BRICKS_PILLAR = Registration.BLOCKS.register(BlockID.CHISELED_STONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> OBSIDIAN_PILLAR = Registration.BLOCKS.register(BlockID.OBSIDIAN_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(50.0F, 1200.0F)));

    public static final RegistryObject<Block> SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.SMOOTH_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.CHISELED_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.CUT_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> RED_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.RED_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.SMOOTH_RED_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CHISELED_RED_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.CHISELED_RED_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_PILLAR = Registration.BLOCKS.register(BlockID.CUT_RED_SANDSTONE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.SAND).strength(0.8F)));

    public static final RegistryObject<Block> GRANITE_PILLAR = Registration.BLOCKS.register(BlockID.GRANITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_GRANITE_PILLAR = Registration.BLOCKS.register(BlockID.POLISHED_GRANITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> DIORITE_PILLAR = Registration.BLOCKS.register(BlockID.DIORITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_DIORITE_PILLAR = Registration.BLOCKS.register(BlockID.POLISHED_DIORITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> ANDESITE_PILLAR = Registration.BLOCKS.register(BlockID.ANDESITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_PILLAR = Registration.BLOCKS.register(BlockID.POLISHED_ANDESITE_PILLAR_ID,
            () -> new PillarBlock(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> BLACKSTONE_PILLAR = Registration.BLOCKS.register(
            BlockID.BLACKSTONE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_PILLAR = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> CHISELED_POLISHED_BLACKSTONE_PILLAR = Registration.BLOCKS.register(
            BlockID.CHISELED_POLISHED_BLACKSTONE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> GILDED_BLACKSTONE_PILLAR = Registration.BLOCKS.register(
            BlockID.GILDED_BLACKSTONE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.GILDED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_PILLAR = Registration.BLOCKS.register(
            BlockID.POLISHED_BLACKSTONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR = Registration.BLOCKS.register(
            BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_PILLAR = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICKS_PILLAR = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_BRICKS_PILLAR = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_BRICKS_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_PILLAR = Registration.BLOCKS.register(
            BlockID.COBBLED_DEEPSLATE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_PILLAR = Registration.BLOCKS.register(
            BlockID.POLISHED_DEEPSLATE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> CHISELED_DEEPSLATE_PILLAR = Registration.BLOCKS.register(
            BlockID.CHISELED_DEEPSLATE_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_TILES_PILLAR = Registration.BLOCKS.register(
            BlockID.DEEPSLATE_TILES_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> CRACKED_DEEPSLATE_TILES_PILLAR = Registration.BLOCKS.register(
            BlockID.CRACKED_DEEPSLATE_TILES_PILLAR_ID,
            () -> new PillarBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_DEEPSLATE_TILES)));

    /**
     *
     */
    public static void register() {
        Registration.registerBlocks();
    }

}

