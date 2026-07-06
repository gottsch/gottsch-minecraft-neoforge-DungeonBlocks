package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.core.block.*;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import mod.gottsch.forge.dungeonblocks.core.state.properties.FacadeShape;
import mod.gottsch.neo.gottschcore.block.FacingHalfBlock;
import mod.gottsch.neo.gottschcore.block.IBasedBlock;
import mod.gottsch.neo.gottschcore.block.IFacingBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;

/**
 *
 */
public class ModBlockStateProvider extends BlockStateProvider {
    // In MC 1.21, Property equality is identity-based, so datagen must reference the SAME
    // FACING instance the blocks actually use (gottschcore's), not a freshly-created one.
    private static EnumProperty<Direction> FACING = IFacingBlock.FACING;
    private static EnumProperty<Direction> BASE = IBasedBlock.BASE;
    private static final int DEFAULT_ANGLE_OFFSET = 180;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DungeonBlocks.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        DataGenMaps maps = new DataGenMaps();

        Registration.BLOCKS.getEntries().stream()
                .filter(b -> {
                    for(String n : maps.names) {
                        if (b.getId().getPath().contains(n)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(b -> {
                    String name = b.getId().getPath();
                    String material;
                    if (name.contains("arrow_slit")) {
                        material = b.getId().getPath().split("_arrow_slit_block")[0];
                        arrowSlitBlock(b, maps.t2.get(material));
                    } else if (name.contains("barred_window_block")) {
                        material = b.getId().getPath().split("_barred_window_block")[0];
                        barredWindowBlock(b, maps.t2.get(material));
                    } else if (name.contains("barred_window_facade")) {
                        material = b.getId().getPath().split("_barred_window_facade_block")[0];
                        barredWindowFacadeBlock(b, maps.t2.get(material));
                    } else if (name.contains("corbel")) {
                        material = b.getId().getPath().split("_corbel_block")[0];
                        horizontalSingleTexture(b, modLoc(ModelProvider.BLOCK_FOLDER + "/corbel_block"), maps.t2.get(material));
                    } else if (name.contains("double_sill")) {
                        material = b.getId().getPath().split("_double_sill_block")[0];
                        horizontalSingleTexture(b, modLoc("block/double_sill_block_base"), (ResourceLocation)maps.t2.get(material));
                    } else if (name.contains("sill") && !name.contains("double")) {
                        material = b.getId().getPath().split("_sill_block")[0];
                        horizontalSingleTexture(b, modLoc("block/sill_block_base"), (ResourceLocation)maps.t2.get(material));
                    } else if (name.contains("fluted_facade")) {
                        material = b.getId().getPath().split("_fluted_facade_block")[0];
                        flutedFacadeBlock(b, (ResourceLocation)maps.t2.get(material));
                    } else if (name.contains("fluted") && !name.contains("facade")) {
                        material = b.getId().getPath().split("_fluted_block")[0];
                        simpleSingleTexture(b, modLoc("block/fluted_block_base"), (ResourceLocation)maps.t2.get(material));
                    } else if (name.contains("ledge")) {
                        material = b.getId().getPath().split("_ledge_block")[0];
//                        DungeonBlocks.LOGGER.info("ledge processing material ->{} to texture ->{} ", material, maps.t2.get(material));
                        ledgeBlock(b, maps.t2.get(material));
                    } else if (name.contains("cornice")) {
                        material = b.getId().getPath().split("_cornice")[0];
                        facadeBlock(b, "cornice", maps.t2.get(material));
                    } else if (name.contains("crown_molding")) {
                        material = b.getId().getPath().split("_crown_molding")[0];
                        facadeBlock(b, "crown_molding", maps.t2.get(material));
                    } else if (name.contains("quarter_facade")) {
                            material = b.getId().getPath().split("_quarter_facade")[0];
                            facadeBlock(b, "quarter_facade", maps.t2.get(material));
                    } else if (name.contains("facade")) {
                        material = b.getId().getPath().split("_facade")[0];
                        facadeBlock(b, "facade", maps.t2.get(material));
                    } else if (name.contains("pillar_base")) {
                        material = b.getId().getPath().split("_pillar_base")[0];
                        simpleSingleTexture(b, modLoc("block/pillar_base_block_base"), (ResourceLocation)maps.t2.get(material));
                    } else if (name.contains("pillar_block")) {
                        material = b.getId().getPath().split("_pillar_block")[0];
                        basedBlock(b, "pillar_block_base", (ResourceLocation)maps.t2.get(material));
                    }
                });

        heavyTrapDoorBlock(ModBlocks.DARK_IRON_HEAVY_TRAPDOOR, modLoc("block/dark_iron"), true);
        heavyTrapDoorBlock(ModBlocks.COPPER_HEAVY_TRAPDOOR, mcLoc("block/copper_block"), true);
        heavyTrapDoorBlock(ModBlocks.EXPOSED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/exposed_copper"), true);
        heavyTrapDoorBlock(ModBlocks.WEATHERED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/weathered_copper"), true);
        heavyTrapDoorBlock(ModBlocks.OXIDIZED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/oxidized_copper"), true);
        
        heavyTrapDoorBlock(ModBlocks.WAXED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/copper_block"), true);
        heavyTrapDoorBlock(ModBlocks.WAXED_EXPOSED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/exposed_copper"), true);
        heavyTrapDoorBlock(ModBlocks.WAXED_WEATHERED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/weathered_copper"), true);
        heavyTrapDoorBlock(ModBlocks.WAXED_OXIDIZED_COPPER_HEAVY_TRAPDOOR, mcLoc("block/oxidized_copper"), true);

        simpleSingleTexture(ModBlocks.COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/copper_grate"));
        simpleSingleTexture(ModBlocks.EXPOSED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/exposed_copper_grate"));
        simpleSingleTexture(ModBlocks.WEATHERED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/weathered_copper_grate"));
        simpleSingleTexture(ModBlocks.OXIDIZED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/oxidized_copper_grate"));
        simpleSingleTexture(ModBlocks.WAXED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/copper_grate"));
        simpleSingleTexture(ModBlocks.WAXED_EXPOSED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/exposed_copper_grate"));
        simpleSingleTexture(ModBlocks.WAXED_WEATHERED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/weathered_copper_grate"));
        simpleSingleTexture(ModBlocks.WAXED_OXIDIZED_COPPER_GRATE, modLoc("block/template_cube_cutout"), modLoc("block/oxidized_copper_grate"));

        heavyGrateBlock(ModBlocks.DARK_IRON_GRATE, modLoc("block/dark_iron"));
        heavyGrateBlock(ModBlocks.COPPER_HEAVY_GRATE, mcLoc("block/copper_block"));
        heavyGrateBlock(ModBlocks.EXPOSED_COPPER_HEAVY_GRATE, mcLoc("block/exposed_copper"));
        heavyGrateBlock(ModBlocks.WEATHERED_COPPER_HEAVY_GRATE, mcLoc("block/weathered_copper"));
        heavyGrateBlock(ModBlocks.OXIDIZED_COPPER_HEAVY_GRATE, mcLoc("block/oxidized_copper"));
        heavyGrateBlock(ModBlocks.WAXED_COPPER_HEAVY_GRATE, mcLoc("block/copper_block"));
        heavyGrateBlock(ModBlocks.WAXED_EXPOSED_COPPER_HEAVY_GRATE, mcLoc("block/exposed_copper"));
        heavyGrateBlock(ModBlocks.WAXED_WEATHERED_COPPER_HEAVY_GRATE, mcLoc("block/weathered_copper"));
        heavyGrateBlock(ModBlocks.WAXED_OXIDIZED_COPPER_HEAVY_GRATE, mcLoc("block/oxidized_copper"));

        valveWheelBlock(ModBlocks.COPPER_VALVE_WHEEL, mcLoc("block/copper_block"));
        valveWheelBlock(ModBlocks.EXPOSED_COPPER_VALVE_WHEEL, mcLoc("block/exposed_copper"));
        valveWheelBlock(ModBlocks.WEATHERED_COPPER_VALVE_WHEEL, mcLoc("block/weathered_copper"));
        valveWheelBlock(ModBlocks.OXIDIZED_COPPER_VALVE_WHEEL, mcLoc("block/oxidized_copper"));
        valveWheelBlock(ModBlocks.WAXED_COPPER_VALVE_WHEEL, mcLoc("block/copper_block"));
        valveWheelBlock(ModBlocks.WAXED_EXPOSED_COPPER_VALVE_WHEEL, mcLoc("block/exposed_copper"));
        valveWheelBlock(ModBlocks.WAXED_WEATHERED_COPPER_VALVE_WHEEL, mcLoc("block/weathered_copper"));
        valveWheelBlock(ModBlocks.WAXED_OXIDIZED_COPPER_VALVE_WHEEL, mcLoc("block/oxidized_copper"));


        plateBracketBlock(ModBlocks.IRON_PLATE_BRACKET, modLoc("block/iron_plate"));
        plateBracketBlock(ModBlocks.DARK_IRON_PLATE_BRACKET, modLoc("block/dark_iron"));
        plateBracketBlock(ModBlocks.COPPER_PLATE_BRACKET, mcLoc("block/copper_block"));
        plateBracketBlock(ModBlocks.EXPOSED_COPPER_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        plateBracketBlock(ModBlocks.WEATHERED_COPPER_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        plateBracketBlock(ModBlocks.OXIDIZED_COPPER_PLATE_BRACKET, mcLoc("block/oxidized_copper"));
        plateBracketBlock(ModBlocks.WAXED_COPPER_PLATE_BRACKET, mcLoc("block/copper_block"));
        plateBracketBlock(ModBlocks.WAXED_EXPOSED_COPPER_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        plateBracketBlock(ModBlocks.WAXED_WEATHERED_COPPER_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        plateBracketBlock(ModBlocks.WAXED_OXIDIZED_COPPER_PLATE_BRACKET, mcLoc("block/oxidized_copper"));

        anglePlateBracketBlock(ModBlocks.IRON_ANGLE_PLATE_BRACKET, modLoc("block/iron_plate"));
        anglePlateBracketBlock(ModBlocks.DARK_IRON_ANGLE_PLATE_BRACKET, modLoc("block/dark_iron"));
        anglePlateBracketBlock(ModBlocks.COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/copper_block"));
        anglePlateBracketBlock(ModBlocks.EXPOSED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        anglePlateBracketBlock(ModBlocks.WEATHERED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        anglePlateBracketBlock(ModBlocks.OXIDIZED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/oxidized_copper"));

        anglePlateBracketBlock(ModBlocks.WAXED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/copper_block"));
        anglePlateBracketBlock(ModBlocks.WAXED_EXPOSED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        anglePlateBracketBlock(ModBlocks.WAXED_WEATHERED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        anglePlateBracketBlock(ModBlocks.WAXED_OXIDIZED_COPPER_ANGLE_PLATE_BRACKET, mcLoc("block/oxidized_copper"));

        cornerPlateBracketBlock(ModBlocks.IRON_CORNER_PLATE_BRACKET, modLoc("block/iron_plate"));
        cornerPlateBracketBlock(ModBlocks.DARK_IRON_CORNER_PLATE_BRACKET, modLoc("block/dark_iron"));
        cornerPlateBracketBlock(ModBlocks.COPPER_CORNER_PLATE_BRACKET, mcLoc("block/copper_block"));
        cornerPlateBracketBlock(ModBlocks.EXPOSED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        cornerPlateBracketBlock(ModBlocks.WEATHERED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        cornerPlateBracketBlock(ModBlocks.OXIDIZED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/oxidized_copper"));

        cornerPlateBracketBlock(ModBlocks.WAXED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/copper_block"));
        cornerPlateBracketBlock(ModBlocks.WAXED_EXPOSED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/exposed_copper"));
        cornerPlateBracketBlock(ModBlocks.WAXED_WEATHERED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/weathered_copper"));
        cornerPlateBracketBlock(ModBlocks.WAXED_OXIDIZED_COPPER_CORNER_PLATE_BRACKET, mcLoc("block/oxidized_copper"));

        wallRingBlock(ModBlocks.WALL_RING);
        hayPatchBlock(ModBlocks.HAY_PATCH);
        hayPatchBlock(ModBlocks.DIRTY_HAY_PATCH, modLoc("block/dirty_hay"));
//        basedBlock(ModBlocks.ANGLE_PLATE_BRACKET_BLOCK, "angle_plate_bracket_block");

        sewerBlock(ModBlocks.WEATHERED_COPPER_SEWER, modLoc("block/weathered_copper_pipe"), mcLoc("block/weathered_copper"));
        sewerBlock(ModBlocks.TERRACOTTA_SEWER, mcLoc("block/terracotta"), mcLoc("block/terracotta"));

        // pattern
        greekBlock(ModBlocks.STONE_GREEK_BLOCK, modLoc("block/stone_greek_block"));
        greekBlock(ModBlocks.ANDESITE_GREEK_BLOCK, modLoc("block/andesite_greek_block"));
        greekBlock(ModBlocks.POLISHED_BASALT_GREEK_BLOCK, modLoc("block/polished_basalt_greek_block"));

        // roots (weeping-vines style hanging plants): cross model, cutout render
        simpleBlock(ModBlocks.ROOTS.get(), models().cross("roots_head", modLoc("block/roots_head")).renderType("minecraft:cutout"));
        simpleBlock(ModBlocks.ROOTS_BODY.get(), models().cross("roots_body", modLoc("block/roots_body")).renderType("minecraft:cutout"));

        // copper doors (waxed variants reuse the un-waxed door textures)
        copperDoor(ModBlocks.COPPER_DOOR, "copper_door");
        copperDoor(ModBlocks.EXPOSED_COPPER_DOOR, "exposed_copper_door");
        copperDoor(ModBlocks.WEATHERED_COPPER_DOOR, "weathered_copper_door");
        copperDoor(ModBlocks.OXIDIZED_COPPER_DOOR, "oxidized_copper_door");
        copperDoor(ModBlocks.WAXED_COPPER_DOOR, "copper_door");
        copperDoor(ModBlocks.WAXED_EXPOSED_COPPER_DOOR, "exposed_copper_door");
        copperDoor(ModBlocks.WAX_WEATHERED_COPPER_DOOR, "weathered_copper_door");
        copperDoor(ModBlocks.WAXED_OXIDIZED_COPPER_DOOR, "oxidized_copper_door");

        // copper trapdoors (waxed variants reuse the un-waxed trapdoor textures)
        copperTrapDoor(ModBlocks.COPPER_TRAPDOOR, "copper_trapdoor");
        copperTrapDoor(ModBlocks.EXPOSED_COPPER_TRAPDOOR, "exposed_copper_trapdoor");
        copperTrapDoor(ModBlocks.WEATHERED_COPPER_TRAPDOOR, "weathered_copper_trapdoor");
        copperTrapDoor(ModBlocks.OXIDIZED_COPPER_TRAPDOOR, "oxidized_copper_trapdoor");
        copperTrapDoor(ModBlocks.WAXED_COPPER_TRAPDOOR, "copper_trapdoor");
        copperTrapDoor(ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR, "exposed_copper_trapdoor");
        copperTrapDoor(ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR, "weathered_copper_trapdoor");
        copperTrapDoor(ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, "oxidized_copper_trapdoor");

        // doors
        dungeonDoorBlock((DoorBlock)ModBlocks.SPRUCE_DUNGEON_DOOR.get(), mcLoc("block/spruce_door_bottom"), mcLoc("block/spruce_door_top"));
        dungeonDoorBlock((DoorBlock)ModBlocks.DARK_OAK_DUNGEON_DOOR.get(), mcLoc("block/dark_oak_door_bottom"), mcLoc("block/dark_oak_door_top"));
        dungeonDoorBlock((DoorBlock)ModBlocks.CRIMSON_DUNGEON_DOOR.get(), mcLoc("block/crimson_door_bottom"), mcLoc("block/crimson_door_top"));
        dungeonDoorBlock((DoorBlock)ModBlocks.MANGROVE_DUNGEON_DOOR.get(), mcLoc("block/mangrove_door_bottom"), mcLoc("block/mangrove_door_top"));

        // light source
        torchSconceBlock(ModBlocks.TORCH_SCONCE);
        candleSconceBlock(ModBlocks.CANDLE_SCONCE);
        brazierBlock(ModBlocks.BRAZIER);

        simpleBlock(ModBlocks.SQUARE_STONE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_SQUARE_STONE_BRICK.get());
        rectangleLeftHorizontalBlock(ModBlocks.LEFT_LARGE_STONE_BRICK, ModBlocks.RIGHT_LARGE_STONE_BRICK);
        rectangleRightHorizontalBlock(ModBlocks.RIGHT_LARGE_STONE_BRICK, ModBlocks.LEFT_LARGE_STONE_BRICK);
        rectangleLeftHorizontalBlock(ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK);
        rectangleRightHorizontalBlock(ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK, ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK);

        simpleBlock(ModBlocks.MOSSY_BRICKS.get());
        stairsBlock(ModBlocks.MOSSY_BRICK_STAIRS.get(), modLoc("block/mossy_bricks"));

        simpleBlock(ModBlocks.LARGE_BRICKS.get());
        simpleBlock(ModBlocks.MOSSY_LARGE_BRICKS.get());
        stairsBlock(ModBlocks.LARGE_BRICK_STAIRS.get(), modLoc("block/large_bricks"));
        stairsBlock(ModBlocks.MOSSY_LARGE_BRICK_STAIRS.get(), modLoc("block/mossy_large_bricks"));
        simpleBlock(ModBlocks.SQUARE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_SQUARE_BRICK.get());
        rectangleLeftHorizontalBlock(ModBlocks.LEFT_LARGE_BRICK, ModBlocks.RIGHT_LARGE_BRICK);
        rectangleRightHorizontalBlock(ModBlocks.RIGHT_LARGE_BRICK, ModBlocks.LEFT_LARGE_BRICK);
        rectangleLeftHorizontalBlock(ModBlocks.MOSSY_LEFT_LARGE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_BRICK);
        rectangleRightHorizontalBlock(ModBlocks.MOSSY_RIGHT_LARGE_BRICK, ModBlocks.MOSSY_LEFT_LARGE_BRICK);

        simpleBlock(ModBlocks.COBBLESTONE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_COBBLESTONE_BRICK.get());
        simpleBlock(ModBlocks.GRAVEL_BRICK.get());
    }

    private void blockWithItem(DeferredHolder<Block, ? extends Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
        simpleBlock(blockRegistryObject.get());
    }

    private void simpleSingleTexture(DeferredHolder<Block, ? extends Block> block, ResourceLocation modelName, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modelName, "0", texture);
        simpleBlock((Block)block.get(), model);
    }

    private void horizontalSingleTexture(DeferredHolder<Block, ? extends Block> block, ResourceLocation modelName, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modelName, "0", texture);
        myHorizontalBlock((Block)block.get(), (ModelFile)model);
    }

    private BlockModelBuilder barredWindow(String name, ResourceLocation texture) {
        return models().singleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/barred_window_block"), "0", texture);
    }

    public void barredWindowBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        barredWindowBlock(block.getId().getPath(), block.get(), texture);
    }

    public void barredWindowBlock(String name, Block block, ResourceLocation texture) {
        ModelFile model = barredWindow(name, texture);
        myHorizontalBlock(block, model);
    }

    public void barredWindowFacadeBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/barred_window_facade_block"), "0", texture);
        myHorizontalBlock(block.get(), model);
    }

    public void dungeonDoorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        String name = key(block).toString();
        dungeonDoorBlock(block, name, bottom, top);
    }

    public void greekBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().cubeAll(block.getId().getPath(), texture);
        myHorizontalBlock(block.get(), model);
    }

    public void arrowSlitBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/arrow_slit_block"), "0", texture);
        myHorizontalBlock(block.get(), model);
    }

    /** Generates the full vanilla-style door blockstate + models (cutout) from the named bottom/top textures. */
    public void copperDoor(DeferredHolder<Block, ? extends Block> block, String textureName) {
        doorBlockWithRenderType((DoorBlock) block.get(),
                modLoc("block/" + textureName + "_bottom"),
                modLoc("block/" + textureName + "_top"), "minecraft:cutout");
    }

    /** Generates the full vanilla-style (orientable) trapdoor blockstate + models (cutout) from the named texture. */
    public void copperTrapDoor(DeferredHolder<Block, ? extends Block> block, String textureName) {
        trapdoorBlockWithRenderType((TrapDoorBlock) block.get(),
                modLoc("block/" + textureName), true, "minecraft:cutout");
    }

    public void torchSconceBlock(DeferredHolder<Block, ? extends Block> block) {
        ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/torch_sconce_block"));
        myHorizontalBlock(block.get(), model);
    }

    public void candleSconceBlock(DeferredHolder<Block, ? extends Block> block) {
        ModelFile empty = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_block"));

        ModelFile one_lit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_one_candle_lit_block"));
        ModelFile one_unlit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_one_candle_block"));

        ModelFile two_lit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_two_candles_lit_block"));
        ModelFile two_unlit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_two_candles_block"));

        ModelFile three_lit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_three_candles_lit_block"));
        ModelFile three_unlit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/candle_sconce_three_candles_block"));

        myCandleSconceBlock(block.get(), empty, one_lit, one_unlit, two_lit, two_unlit, three_lit, three_unlit);
    }

    @Deprecated
    public void flutedFacadeBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        String name = block.getId().getPath();
        ModelFile normal = ((BlockModelBuilder)models().withExistingParent(name, "dungeonblocks:block/fluted_facade_block_base")).texture("0", texture);
        ModelFile inner = ((BlockModelBuilder)models().withExistingParent(name + "_inner", "dungeonblocks:block/fluted_facade_inner_block_base")).texture("0", texture);
        ModelFile outer = ((BlockModelBuilder)models().withExistingParent(name + "_outer", "dungeonblocks:block/fluted_facade_outer_block_base")).texture("0", texture);
        facadeBlock((Block)block.get(), normal, inner, outer);
    }

    @Deprecated
    public void ledgeBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        String name = block.getId().getPath();
        ModelFile ledge = models().withExistingParent(name, "dungeonblocks:block/ledge_block").texture("0", texture);
        ModelFile inner = models().withExistingParent(name + "_inner", "dungeonblocks:block/" + "ledge_block_inner").texture("0", texture);
        ModelFile outer = models().withExistingParent(name + "_outer", "dungeonblocks:block/" + "ledge_block_outer").texture("0", texture);
        facadeBlock(block.get(), ledge, inner, outer);
    }

    // TODO all _block_base models should be renamed to template_[BLOCK_NAME]
    public void facadeBlock(DeferredHolder<Block, ? extends Block> block, String baseName, ResourceLocation texture) {
        String name = block.getId().getPath();
        ModelFile base = models().withExistingParent(name, "dungeonblocks:block/" + baseName + "_block_base").texture("0", texture);
        ModelFile inner = models().withExistingParent(name + "_inner", "dungeonblocks:block/" + baseName + "_inner_block_base").texture("0", texture);
        ModelFile outer = models().withExistingParent(name + "_outer", "dungeonblocks:block/" + baseName + "_outer_block_base").texture("0", texture);
        facadeBlock(block.get(), base, inner, outer);
    }

    public void facadeBlock(Block block, ModelFile normal, ModelFile inner, ModelFile outer) {
        getVariantBuilder(block).forAllStatesExcept((state) -> {
            Direction facing = (Direction)state.getValue(IFacingBlock.FACING);
            FacadeShape shape = (FacadeShape)state.getValue(IFacadeShapeBlock.SHAPE);
            int yRot = (int)facing.getOpposite().toYRot();
            if (facing == Direction.NORTH && (shape == FacadeShape.OUTER_RIGHT || shape == FacadeShape.INNER_LEFT) || facing == Direction.SOUTH && (shape == FacadeShape.OUTER_LEFT || shape == FacadeShape.INNER_RIGHT)) {
                yRot += 90;
            }

            return ConfiguredModel.builder().modelFile(shape == FacadeShape.STRAIGHT ? normal : (shape != FacadeShape.INNER_LEFT && shape != FacadeShape.INNER_RIGHT ? outer : inner)).rotationY(yRot).uvLock(true).build();
        }, new Property[]{WaterloggedNonCubeFacingBlock.WATERLOGGED, FacadeShapeBlock.WATERLOGGED});
    }

    public void basedBlock(DeferredHolder<Block, ? extends Block> block, String baseName, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc("block/" + baseName), "0", texture);
        myDirectionalBlock((Block)block.get(), (ModelFile)model);
    }

    public void wallRingBlock(DeferredHolder<Block, ? extends Block> block) {
       ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/wall_ring"));
       ModelFile openModel = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/wall_ring_open"));
       // TODO get the extended model

        // TODO be more like trapdoor. if down, use the extended model, else use normal
        // also, up and down need special case to rotate x:
//       myHorizontalBlock(block.get(), model);
        wallRingBlock(block.get(), model, openModel);
    }

    public void wallRingBlock(Block block, ModelFile ring, ModelFile ringOpen) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            ModelFile model = ring;
            int xRot = 0;
            int yRot = 0;
            Direction dir = state.getValue(FACING);
            if (dir == Direction.DOWN) {
                model = ringOpen;
                xRot = 90;
            }
            else if (dir == Direction.UP) {
                xRot = -90;
            } else {
                yRot = ((int) state.getValue(FACING).toYRot() + 180) % 360;
            }

            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, WallRingBlock.WATERLOGGED, WaterloggedNonCubeFacingBlock.WATERLOGGED);
    }

    public void plateBracketBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/plate_bracket_block"), "0", texture);
        allDirectionBlock(block.get(), model);
    }

    public void anglePlateBracketBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/angle_plate_bracket_block"), "0", texture);
        facingHalfBlock((FacingHalfBlock) block.get(), model);
    }

    public void cornerPlateBracketBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/corner_plate_bracket_block"), "0", texture);
        facingHalfBlock((FacingHalfBlock) block.get(), model);
    }

    public void allDirectionBlock(DeferredHolder<Block, ? extends Block> block, String name) {
        ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/" + name));
        allDirectionBlock(block.get(), model);
    }

    public void allDirectionBlock(Block block, ModelFile model) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = 0;
            Direction dir = state.getValue(FACING);
            if (dir == Direction.DOWN) {
                xRot = 90;
            }
            else if (dir == Direction.UP) {
                xRot = -90;
            } else {
                yRot = ((int) state.getValue(FACING).toYRot() + 180) % 360;
            }
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, PlateBracketBlock.WATERLOGGED, WaterloggedNonCubeFacingBlock.WATERLOGGED);
    }

    public void hayPatchBlock(DeferredHolder<Block, ? extends Block> block) {
        ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/hay_patch_block"));
        simpleBlock(block.get(), model);
    }
    public void hayPatchBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/dirty_hay_patch_block"));
        simpleBlock(block.get(), model);
    }

    public void heavyGrateBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc("block/template_heavy_grate_block"), "0", texture);
        myDirectionalBlock((Block)block.get(), (ModelFile)model);
    }

    public void valveWheelBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc("block/template_valve_wheel"), "0", texture);
        allDirectionBlock((Block)block.get(), (ModelFile)model);
    }

    @Deprecated
    public void _sewerBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, ResourceLocation texture1) {
        ModelFile model = twoTextures(
                block.getId().getPath(),
                modLoc(ModelProvider.BLOCK_FOLDER + "/template_sewer_block"), "0", texture, "1", texture1);

//        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/template_sewer_block"), "0", texture);
     //        ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/sewer_block"));
        // TODO get the extended model

        myHorizontalBlock(block.get(), model);
    }

    public void sewerBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, ResourceLocation texture1) {
        String name = block.getId().getPath();
        ModelFile model = twoTextures(name, modLoc(ModelProvider.BLOCK_FOLDER + "/template_sewer_block"), "0", texture, "1", texture1);
        ModelFile corner = twoTextures(name + "_corner", modLoc(ModelProvider.BLOCK_FOLDER + "/template_sewer_block_corner"), "0", texture, "1", texture1);

        sewerBlock(block.get(), model, corner);
    }

    public void sewerBlock(Block block, ModelFile sewer, ModelFile corner) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction facing = state.getValue(LedgeBlock.FACING);
                    SewerBlock.SewerShape shape = state.getValue(SewerBlock.SHAPE);
                    int yRot = ((int) state.getValue(FACING).toYRot() + DEFAULT_ANGLE_OFFSET) % 360;
                   yRot = switch(shape) {
                       case STRAIGHT -> yRot;
                       case TOP_LEFT -> 180;
                       case BOTTOM_LEFT -> 90;
                       case TOP_RIGHT -> 270;
                       case BOTTOM_RIGHT -> 0;
                   };

                    return ConfiguredModel.builder()
                            .modelFile(shape == SewerBlock.SewerShape.STRAIGHT ? sewer : corner)
                            .rotationY(yRot)
                            .uvLock(true)
                            .build();
                });
    }


    public void brazierBlock(DeferredHolder<Block, ? extends Block> block) {
        ModelFile brazier_lit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/brazier_lit_block"));
        ModelFile brazier = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/brazier_block"));
        brazierBlock(block.get(), brazier, brazier_lit);
    }

    public void brazierBlock(Block block, ModelFile brazier, ModelFile brazier_lit) {
        getVariantBuilder(block)
                .partialState().with(BrazierBlock.LIT, true).addModels(new ConfiguredModel(brazier_lit))
                .partialState().with(BrazierBlock.LIT, false).addModels(new ConfiguredModel(brazier));
    }

    public ModelFile rectangleLeft(String name, ResourceLocation texture, ResourceLocation texture2) {
        return ((BlockModelBuilder)((BlockModelBuilder)models().withExistingParent(name, modLoc("block/rectangle_left"))).texture("left", texture)).texture("right", texture2);
    }

    public ModelFile rectangleRight(String name, ResourceLocation texture, ResourceLocation texture2) {
        return ((BlockModelBuilder)((BlockModelBuilder)models().withExistingParent(name, modLoc("block/rectangle_right"))).texture("right", texture)).texture("left", texture2);
    }

    public void rectangleLeftHorizontalBlock(DeferredHolder<Block, ? extends Block> block, DeferredHolder<Block, ? extends Block> block2) {
        ModelFile model = rectangleLeft(block.getId().getPath(), modLoc("block/" + block.getId().getPath()), modLoc("block/" + block2.getId().getPath()));
        myHorizontalBlock((Block)block.get(), model);
    }

    public void rectangleRightHorizontalBlock(DeferredHolder<Block, ? extends Block> block, DeferredHolder<Block, ? extends Block> block2) {
        ModelFile model = rectangleRight(block.getId().getPath(), modLoc("block/" + block.getId().getPath()), modLoc("block/" + block2.getId().getPath()));
        myHorizontalBlock((Block)block.get(), model);
    }

    public void myHorizontalBlock(Block block, ModelFile model) {
        myHorizontalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void myHorizontalBlock(Block block, ModelFile model, int angleOffset) {
        myHorizontalBlock(block, $ -> model, angleOffset);
    }

    public void myHorizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        myHorizontalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void myHorizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(modelFunc.apply(state))
                        .rotationY(((int) state.getValue(FACING).toYRot() + angleOffset) % 360)
                        .build()
                );
    }

    public void myDirectionalBlock(Block block, ModelFile model) {
        myDirectionalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void myDirectionalBlock(Block block, ModelFile model, int angleOffset) {
        myDirectionalBlock(block, $ -> model, angleOffset);
    }

    public void myDirectionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        myDirectionalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void myDirectionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BASE);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }

    private void myCandleSconceBlock(Block block, ModelFile empty, ModelFile oneLit, ModelFile oneUnlit, ModelFile twoLit, ModelFile twoUnlit, ModelFile threeLit, ModelFile threeUnlit) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            ModelFile model = empty;
            boolean isLit = state.getValue(SconceBlock.LIT);
            int candles = state.getValue(SconceBlock.CANDLES);
            Direction facing = state.getValue(SconceBlock.FACING);

            if (candles == 0) {
            }
            else if (candles == 1) {
                model = isLit ? oneLit : oneUnlit;
            } else if (candles == 2) {
                model = isLit ? twoLit : twoUnlit;
            } else if (candles == 3) {
                model = isLit ? threeLit : threeUnlit;
            }
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) facing.getOpposite().toYRot())
                    .uvLock(true)
                    .build();
        }, SconceBlock.WATERLOGGED);
    }

    private void dungeonDoorBlock(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
        ModelFile bottomLeft = doorBottomLeft(baseName + "_bottom_left", bottom, top);
        ModelFile bottomLeftOpen = doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top);
        ModelFile bottomRight = doorBottomRight(baseName + "_bottom_right", bottom, top);
        ModelFile bottomRightOpen = doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top);
        ModelFile topLeft = doorTopLeft(baseName + "_top_left", bottom, top);
        ModelFile topLeftOpen = doorTopLeftOpen(baseName + "_top_left_open", bottom, top);
        ModelFile topRight = doorTopRight(baseName + "_top_right", bottom, top);
        ModelFile topRightOpen = doorTopRightOpen(baseName + "_top_right_open", bottom, top);
        doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    private BlockModelBuilder door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
        return models().withExistingParent(name,  "dungeonblocks:block/" + model)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public BlockModelBuilder doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_bottom_left", bottom, top);
    }

    public BlockModelBuilder doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_bottom_left_open", bottom, top);
    }

    public BlockModelBuilder doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_bottom_right", bottom, top);
    }

    public BlockModelBuilder doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_bottom_right_open", bottom, top);
    }

    public BlockModelBuilder doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_top_left", bottom, top);
    }

    public BlockModelBuilder doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_top_left_open", bottom, top);
    }

    public BlockModelBuilder doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_top_right", bottom, top);
    }

    public BlockModelBuilder doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
        return door(name, "dungeon_door_top_right_open", bottom, top);
    }

    public void heavyTrapDoorBlock(DeferredHolder<Block, ? extends Block> block, ResourceLocation texture, boolean orientable) {
        myTrapdoorBlockInternal((TrapDoorBlock)block.get(), block.getId().getPath(), texture, orientable);
    }

    private void myTrapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
        ModelFile bottom = orientable ? myTrapdoorBottom(baseName + "_bottom", texture) : myTrapdoorBottom(baseName + "_bottom", texture);
        ModelFile top = orientable ? myTrapdoorTop(baseName + "_top", texture) : myTrapdoorTop(baseName + "_top", texture);
        ModelFile open = orientable ? myTrapdoorOpen(baseName + "_open", texture) : myTrapdoorOpen(baseName + "_open", texture);
        trapdoorBlock(block, bottom, top, open, orientable);
    }

    public BlockModelBuilder myTrapdoorBottom(String name, ResourceLocation texture) {
        return models().singleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/template_heavy_trapdoor_block_bottom"), "1", texture);
    }

    public BlockModelBuilder myTrapdoorTop(String name, ResourceLocation texture) {
        return models().singleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/template_heavy_trapdoor_block_top"), "1", texture);
    }

    public BlockModelBuilder myTrapdoorOpen(String name, ResourceLocation texture) {
        return models().singleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/template_heavy_trapdoor_block_open"), "1", texture);
    }

    public BlockModelBuilder twoTextures(String name, ResourceLocation parent,
                                         String textureKey1, ResourceLocation texture1,
                                         String textureKey2, ResourceLocation texture2) {
        return models().withExistingParent(name, parent)
                .texture(textureKey1, texture1)
                .texture(textureKey2, texture2);
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    /*
     * this is based off of vanilla stairs, however, this method does not include SHAPE state,
     * so there are no state definitions generated for 'corner' blocks where two of the same block meet.
     */
    public void facingHalfBlock(FacingHalfBlock block, ModelFile model) { //}, ModelFile stairsInner, ModelFile stairsOuter) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(FacingHalfBlock.FACING);
                    Half half = state.getValue(FacingHalfBlock.HALF);
                    int yRot = 0;
                    if (facing != Direction.UP && facing != Direction.DOWN) {
                        // need to spin it around (models always face north by default instead of south)
                        yRot = (int) facing.getOpposite().toYRot();
                    }
                    yRot %= 360;
                    boolean uvlock = yRot != 0 || half == Half.BOTTOM; // Don't set uvlock for states that have no rotation
                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationX(half == Half.TOP ? 0 : -90)
                            .rotationY(yRot)
                            .uvLock(uvlock)
                            .build();
                }, StairBlock.WATERLOGGED);
    }
}