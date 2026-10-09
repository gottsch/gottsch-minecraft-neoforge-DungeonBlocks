package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.core.block.*;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import mod.gottsch.forge.dungeonblocks.core.state.properties.DoorSegment;
import mod.gottsch.forge.dungeonblocks.core.state.properties.FacadeShape;
import mod.gottsch.neo.gottschcore.block.FacingHalfBlock;
import mod.gottsch.neo.gottschcore.block.IBasedBlock;
import mod.gottsch.neo.gottschcore.block.IFacingBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
        // every decorative block, by its type, textured by its material
        ModBlocks.DECOR.forEach(this::decorBlock);
        // the square bricks' facades are registered by hand, outside DECOR
        facadeBlock(ModBlocks.SQUARE_STONE_BRICK_FACADE_BLOCK, "facade", modLoc("block/square_stone_brick"));
        facadeBlock(ModBlocks.SQUARE_MUD_BRICK_FACADE_BLOCK, "facade", modLoc("block/square_mud_brick"));
        facadeBlock(ModBlocks.SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK, "facade", modLoc("block/square_deepslate_brick"));
        facadeBlock(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK, "facade", modLoc("block/mossy_square_deepslate_brick"));
        facadeBlock(ModBlocks.SQUARE_STONE_BRICK_QUARTER_FACADE_BLOCK, "quarter_facade", modLoc("block/square_stone_brick"));
        facadeBlock(ModBlocks.SQUARE_MUD_BRICK_QUARTER_FACADE_BLOCK, "quarter_facade", modLoc("block/square_mud_brick"));
        facadeBlock(ModBlocks.SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK, "quarter_facade", modLoc("block/square_deepslate_brick"));
        facadeBlock(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK, "quarter_facade", modLoc("block/mossy_square_deepslate_brick"));

        // dark iron and copper families: every age (and waxed twin) of a shape is the same model,
        // textured by its age
        ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.forEach((age, b) -> heavyTrapDoorBlock(b, darkIronTexture(age), true));
        ModBlocks.COPPER_HEAVY_TRAPDOORS.forEach((age, b) -> heavyTrapDoorBlock(b, copperTexture(age), true));

        ModBlocks.COPPER_GRATES.forEach((age, b) -> simpleSingleTexture(b, modLoc("block/template_cube_cutout"),
                modLoc("block/" + CopperFamily.id(age, "copper_grate"))));

        ModBlocks.DARK_IRON_GRATES.forEach((age, b) -> heavyGrateBlock(b, darkIronTexture(age)));
        ModBlocks.COPPER_HEAVY_GRATES.forEach((age, b) -> heavyGrateBlock(b, copperTexture(age)));

        ModBlocks.COPPER_VALVE_WHEELS.forEach((age, b) -> valveWheelBlock(b, copperTexture(age)));

        plateBracketBlock(ModBlocks.IRON_PLATE_BRACKET, modLoc("block/iron_plate"));
        plateBracketBlock(ModBlocks.DARK_IRON_PLATE_BRACKET, modLoc("block/dark_iron"));
        ModBlocks.COPPER_PLATE_BRACKETS.forEach((age, b) -> plateBracketBlock(b, copperTexture(age)));

        anglePlateBracketBlock(ModBlocks.IRON_ANGLE_PLATE_BRACKET, modLoc("block/iron_plate"));
        anglePlateBracketBlock(ModBlocks.DARK_IRON_ANGLE_PLATE_BRACKET, modLoc("block/dark_iron"));
        ModBlocks.COPPER_ANGLE_PLATE_BRACKETS.forEach((age, b) -> anglePlateBracketBlock(b, copperTexture(age)));

        cornerPlateBracketBlock(ModBlocks.IRON_CORNER_PLATE_BRACKET, modLoc("block/iron_plate"));
        cornerPlateBracketBlock(ModBlocks.DARK_IRON_CORNER_PLATE_BRACKET, modLoc("block/dark_iron"));
        ModBlocks.COPPER_CORNER_PLATE_BRACKETS.forEach((age, b) -> cornerPlateBracketBlock(b, copperTexture(age)));

        wallRingBlock(ModBlocks.WALL_RING);
        hayPatchBlock(ModBlocks.HAY_PATCH);
        hayPatchBlock(ModBlocks.DIRTY_HAY_PATCH, modLoc("block/dirty_hay"));

        sewerBlock(ModBlocks.WEATHERED_COPPER_SEWER, modLoc("block/weathered_copper_pipe"), mcLoc("block/weathered_copper"));
        sewerBlock(ModBlocks.TERRACOTTA_SEWER, mcLoc("block/terracotta"), mcLoc("block/terracotta"));

        // pattern
        greekBlock(ModBlocks.STONE_GREEK_BLOCK, modLoc("block/stone_greek_block"));
        greekBlock(ModBlocks.ANDESITE_GREEK_BLOCK, modLoc("block/andesite_greek_block"));
        greekBlock(ModBlocks.POLISHED_BASALT_GREEK_BLOCK, modLoc("block/polished_basalt_greek_block"));
        axisBlock((RotatedPillarBlock) ModBlocks.MOSSY_POLISHED_BASALT.get(),
                modLoc("block/mossy_polished_basalt_side"), modLoc("block/mossy_polished_basalt_top"));

        // roots (weeping-vines style hanging plants): cross model, cutout render
        simpleBlock(ModBlocks.ROOTS.get(), models().cross("roots_head", modLoc("block/roots_head")).renderType("minecraft:cutout"));
        simpleBlock(ModBlocks.ROOTS_BODY.get(), models().cross("roots_body", modLoc("block/roots_body")).renderType("minecraft:cutout"));

        // copper doors (waxed variants reuse the un-waxed door textures)
        ModBlocks.COPPER_DOORS.forEach((age, b) -> copperDoor(b, CopperFamily.id(age, "copper_door")));
        edgedDoor(ModBlocks.IRON_BARS_DOOR, "iron_bars_door");
        ModBlocks.DARK_IRON_BARS_DOORS.forEach((age, b) -> edgedDoor(b, age.id("dark_iron_bars_door")));
        ModBlocks.DARK_IRON_BARS.forEach((age, b) -> ironBars(b));
        // the Blockbench model faces north, against a south wall, as vanilla's ladder does
        getVariantBuilder(ModBlocks.DARK_IRON_LADDER.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc("block/dark_iron_ladder")))
                .rotationY(yaw(state.getValue(LadderBlock.FACING))).build(), LadderBlock.WATERLOGGED);
        ModBlocks.SHARPENED_LOGS.forEach(this::sharpenedLog);
        ModBlocks.CAPSTONES.forEach((block, source) -> capstone(block, source.get()));
        directionalBlock(ModBlocks.IRON_SPIKES.get(), objModel("iron_spikes", "spikes",
                mcLoc("block/iron_block"), mcLoc("block/iron_block")));
        directionalBlock(ModBlocks.DARK_IRON_SPIKES.get(), objModel("dark_iron_spikes", "spikes",
                modLoc("block/dark_iron"), modLoc("block/dark_iron")));
        // the cheval-de-frise OBJ's beam runs along x; axis z is the same model turned a quarter.
        // Bark on the beam and stakes, the log's end grain on the beam's ends, stripped wood tips.
        ModBlocks.CHEVALS_DE_FRISE.forEach(block -> {
            String name = block.getId().getPath();
            String log = DataGenMaps.logOf(DataGenMaps.woodOf(name, "cheval_de_frise"));
            ModelFile cheval = objModel(name, "cheval_de_frise",
                    mcLoc("block/" + log), mcLoc("block/" + log + "_top"), mcLoc("block/stripped_" + log));
            getVariantBuilder(block.get()).forAllStates(state ->
                    ConfiguredModel.builder().modelFile(cheval)
                            .rotationY(state.getValue(ChevalDeFriseBlock.AXIS) == Direction.Axis.Z ? 90 : 0).build());
        });
        // portcullis: the lattice spans along x in the OBJ, so axis z is a quarter turn; the bottom
        // row swaps in the model with spiked tips
        ModelFile portcullis = objModel("portcullis", "portcullis", modLoc("block/dark_iron"));
        ModelFile portcullisBottom = objModel("portcullis_bottom", "portcullis_bottom", modLoc("block/dark_iron"));
        getVariantBuilder(ModBlocks.PORTCULLIS.get()).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(state.getValue(PortcullisBlock.BOTTOM) ? portcullisBottom : portcullis)
                        .rotationY(state.getValue(PortcullisBlock.AXIS) == Direction.Axis.Z ? 90 : 0).build());
        ModelFile winch = objModel("portcullis_winch", "portcullis_winch", modLoc("block/dark_iron"),
                mcLoc("block/stripped_spruce_log"), mcLoc("block/stripped_spruce_log_top"), modLoc("block/dark_iron"));
        getVariantBuilder(ModBlocks.PORTCULLIS_WINCH.get()).forAllStates(state ->
                ConfiguredModel.builder().modelFile(winch)
                        .rotationY(state.getValue(PortcullisWinchBlock.AXIS) == Direction.Axis.Z ? 90 : 0).build());
        furniture();
        // the bracket OBJ is authored facing north; horizontalBlock turns it like any facing block
        ModBlocks.WALKWAY_BRACKETS.forEach(block -> {
            String name = block.getId().getPath();
            String log = DataGenMaps.logOf(DataGenMaps.woodOf(name, "walkway_bracket"));
            horizontalBlock(block.get(), objModel(name, "walkway_bracket",
                    mcLoc("block/stripped_" + log), mcLoc("block/stripped_" + log + "_top")));
        });

        // copper trapdoors (waxed variants reuse the un-waxed trapdoor textures)
        ModBlocks.COPPER_TRAPDOORS.forEach((age, b) -> copperTrapDoor(b, CopperFamily.id(age, "copper_trapdoor")));

        // dungeon doors: vanilla's door textures. The tall dark oak doors use the mod's own dark oak
        // dungeon door textures. A tall door's middle repeats its top texture where that tiles (spruce's
        // plain boards, dark oak's panels); mangrove and crimson have their own middle, and a top and
        // bottom with the handle moved wholly onto the bottom segment (tools/gen_tall_door_middle_textures.py)
        ModBlocks.DUNGEON_DOORS.forEach(door -> {
            ResourceLocation bottom = mcLoc("block/" + door.wood() + "_door_bottom");
            ResourceLocation top = mcLoc("block/" + door.wood() + "_door_top");
            if (door.height() == 2) {
                dungeonDoorBlock((DoorBlock) door.block().get(), bottom, top);
                return;
            }
            ResourceLocation middle = top;
            switch (door.wood()) {
                case "dark_oak" -> {
                    top = middle = modLoc("block/dungeon_dark_oak_door_top");
                    bottom = modLoc("block/dungeon_dark_oak_door_bottom");
                }
                case "mangrove" -> {
                    top = modLoc("block/dungeon_mangrove_tall_door_top");
                    middle = modLoc("block/dungeon_mangrove_door_middle");
                }
                case "crimson" -> {
                    top = modLoc("block/dungeon_crimson_tall_door_top");
                    middle = modLoc("block/dungeon_crimson_door_middle");
                    bottom = modLoc("block/dungeon_crimson_tall_door_bottom");
                }
                default -> { }
            }
            tallDungeonDoorBlock((TallDoorBlock) door.block().get(), bottom, middle, top);
        });

        // light source
        torchSconceBlock(ModBlocks.TORCH_SCONCE);
        angleCobwebBlock(ModBlocks.ANGLE_COBWEB_1);
        angleCobwebBlock(ModBlocks.ANGLE_COBWEB_2);
        candleSconceBlock(ModBlocks.CANDLE_SCONCE);
        brazierBlock(ModBlocks.BRAZIER);

        simpleBlock(ModBlocks.SQUARE_STONE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_SQUARE_STONE_BRICK.get());
        simpleBlock(ModBlocks.SQUARE_MUD_BRICK.get());
        // the facade / quarter facade variants are listed with the decorative blocks above
        stairsBlock(ModBlocks.SQUARE_STONE_BRICK_STAIRS.get(), modLoc("block/square_stone_brick"));
        stairsBlock(ModBlocks.MOSSY_SQUARE_STONE_BRICK_STAIRS.get(), modLoc("block/mossy_square_stone_brick"));
        // the double-slab state renders the full block's own model rather than a second copy of it
        slabBlock(ModBlocks.SQUARE_STONE_BRICK_SLAB.get(), modLoc("block/square_stone_brick"), modLoc("block/square_stone_brick"));
        slabBlock(ModBlocks.MOSSY_SQUARE_STONE_BRICK_SLAB.get(), modLoc("block/mossy_square_stone_brick"), modLoc("block/mossy_square_stone_brick"));
        stairsBlock(ModBlocks.SQUARE_MUD_BRICK_STAIRS.get(), modLoc("block/square_mud_brick"));
        largeBrickPair(ModBlocks.LEFT_LARGE_STONE_BRICK, ModBlocks.RIGHT_LARGE_STONE_BRICK);
        largeBrickPair(ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK);
        largeBrickPair(ModBlocks.LEFT_LARGE_MUD_BRICK, ModBlocks.RIGHT_LARGE_MUD_BRICK);
        simpleBlock(ModBlocks.MOSSY_SQUARE_MUD_BRICK.get());
        largeBrickPair(ModBlocks.MOSSY_LEFT_LARGE_MUD_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_MUD_BRICK);

        simpleBlock(ModBlocks.SQUARE_DEEPSLATE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK.get());
        stairsBlock(ModBlocks.SQUARE_DEEPSLATE_BRICK_STAIRS.get(), modLoc("block/square_deepslate_brick"));
        stairsBlock(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_STAIRS.get(), modLoc("block/mossy_square_deepslate_brick"));
        slabBlock(ModBlocks.SQUARE_DEEPSLATE_BRICK_SLAB.get(), modLoc("block/square_deepslate_brick"), modLoc("block/square_deepslate_brick"));
        slabBlock(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_SLAB.get(), modLoc("block/mossy_square_deepslate_brick"), modLoc("block/mossy_square_deepslate_brick"));
        largeBrickPair(ModBlocks.LEFT_LARGE_DEEPSLATE_BRICK, ModBlocks.RIGHT_LARGE_DEEPSLATE_BRICK);
        largeBrickPair(ModBlocks.MOSSY_LEFT_LARGE_DEEPSLATE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_DEEPSLATE_BRICK);
        simpleBlock(ModBlocks.CHISELED_DEEPSLATE_BRICKS.get());
        simpleBlock(ModBlocks.MOSSY_CHISELED_DEEPSLATE_BRICKS.get());
        // the eleven decorative types for these three come off the ModMaterials.STONE loop above;
        // only the full block and the stairs are generated here
        simpleBlock(ModBlocks.MOSSY_DEEPSLATE_BRICKS.get());
        simpleBlock(ModBlocks.MOSSY_DEEPSLATE_TILES.get());
        simpleBlock(ModBlocks.MOSSY_COBBLED_DEEPSLATE.get());
        simpleBlock(ModBlocks.MOSSY_TUFF.get());
        stairsBlock(ModBlocks.MOSSY_DEEPSLATE_BRICK_STAIRS.get(), modLoc("block/mossy_deepslate_bricks"));
        simpleBlock(ModBlocks.POLISHED_ANDESITE_BRICKS.get());
        stairsBlock(ModBlocks.POLISHED_ANDESITE_BRICK_STAIRS.get(), modLoc("block/polished_andesite_bricks"));
        simpleBlock(ModBlocks.MOSSY_POLISHED_ANDESITE_BRICKS.get());
        stairsBlock(ModBlocks.MOSSY_POLISHED_ANDESITE_BRICK_STAIRS.get(), modLoc("block/mossy_polished_andesite_bricks"));

        simpleBlock(ModBlocks.MOSSY_BRICKS.get());
        stairsBlock(ModBlocks.MOSSY_BRICK_STAIRS.get(), modLoc("block/mossy_bricks"));

        simpleBlock(ModBlocks.LARGE_BRICKS.get());
        simpleBlock(ModBlocks.MOSSY_LARGE_BRICKS.get());
        stairsBlock(ModBlocks.LARGE_BRICK_STAIRS.get(), modLoc("block/large_bricks"));
        stairsBlock(ModBlocks.MOSSY_LARGE_BRICK_STAIRS.get(), modLoc("block/mossy_large_bricks"));
        simpleBlock(ModBlocks.SQUARE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_SQUARE_BRICK.get());
        largeBrickPair(ModBlocks.LEFT_LARGE_BRICK, ModBlocks.RIGHT_LARGE_BRICK);
        largeBrickPair(ModBlocks.MOSSY_LEFT_LARGE_BRICK, ModBlocks.MOSSY_RIGHT_LARGE_BRICK);

        simpleBlock(ModBlocks.COBBLESTONE_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_COBBLESTONE_BRICK.get());
        simpleBlock(ModBlocks.RUBBLE.get());
        simpleBlock(ModBlocks.MOSSY_RUBBLE.get());
        simpleBlock(ModBlocks.GRAVEL_BRICK.get());
        simpleBlock(ModBlocks.MOSSY_CHISELED_STONE_BRICKS.get());

        swingingChainBlock(ModBlocks.SWINGING_CHAIN);
        ModBlocks.BANNERS.forEach(this::dungeonBannerBlock);

        // slab tables. The two textures are the FOOT half and the HEAD half, in that order - pass
        // different ones to get a table that reads differently at each end.
        slabTableBlock(ModBlocks.STONE_SLAB_TABLE, mcLoc("block/stone"), mcLoc("block/stone"));
        slabTableBlock(ModBlocks.STONE_BRICKS_SLAB_TABLE, mcLoc("block/stone_bricks"), mcLoc("block/stone_bricks"));
        slabTableBlock(ModBlocks.MOSSY_STONE_BRICKS_SLAB_TABLE, mcLoc("block/mossy_stone_bricks"), mcLoc("block/mossy_stone_bricks"));
        slabTableBlock(ModBlocks.SMOOTH_STONE_SLAB_TABLE, mcLoc("block/smooth_stone"), mcLoc("block/smooth_stone"));
        // smooth sandstone has no texture of its own — vanilla draws it with sandstone_top,
        // the same override ModMaterials.STONE carries for it
        slabTableBlock(ModBlocks.SMOOTH_SANDSTONE_SLAB_TABLE, mcLoc("block/sandstone_top"), mcLoc("block/sandstone_top"));
    }

    /**
     * The swinging chain has no baked geometry at all — {@code SwingingChainRenderer} draws every
     * segment, because a static model can't sway. The generated model exists only to give the block a
     * particle texture for break/step effects, and both {@code top} values map to it (a single
     * {@code ""} variant would not match a block that has properties).
     */
    private void swingingChainBlock(DeferredHolder<Block, Block> block) {
        BlockModelBuilder model = models()
                .withExistingParent(block.getId().getPath(), mcLoc("block/block"))
                .texture("particle", mcLoc("block/chain"));
        getVariantBuilder(block.get())
                .forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
    }

    /**
     * Like the swinging chain, the banner has no baked geometry - {@code DungeonBannerRenderer}
     * draws the cloth because a waving cloth cannot live in the chunk mesh. The generated model
     * exists only to give break and step effects a particle texture, and every facing maps to it.
     */
    private void dungeonBannerBlock(DeferredHolder<Block, Block> block) {
        BlockModelBuilder model = models()
                .withExistingParent(block.getId().getPath(), mcLoc("block/block"))
                .texture("particle", modLoc("block/" + block.getId().getPath()));
        getVariantBuilder(block.get())
                .forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
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

    /**
     * A block model over one of the OBJs from tools/gen_obj_models.py. Rooted at block_no_ao because
     * those shapes are not axis-aligned, and a model's own ambientocclusion flag is ignored unless it
     * is the root. `textures` pairs each of the OBJ's material slots with a texture, in order; the
     * first also serves as the particle texture.
     */
    private BlockModelBuilder objModel(String name, String obj, ResourceLocation... textures) {
        String[] slots = switch (obj) {
            case "pyramid" -> new String[] {"facet", "base"};
            case "sill", "double_sill" -> new String[] {"stone"};
            case "spikes" -> new String[] {"plate", "spike"};
            case "cheval_de_frise" -> new String[] {"bark", "end", "tip"};
            case "walkway_bracket" -> new String[] {"wood", "end"};
            case "portcullis", "portcullis_bottom" -> new String[] {"bar"};
            case "portcullis_winch" -> new String[] {"iron", "drum", "drum_end", "chain_band"};
            case "iron_maiden_spikes_lower_closed", "iron_maiden_spikes_lower_open",
                 "iron_maiden_spikes_upper_closed", "iron_maiden_spikes_upper_open" -> new String[] {"spike"};
            case "coffin_head_body", "coffin_head_lid", "coffin_foot_body", "coffin_foot_lid",
                 "coffin_item" -> new String[] {"wood", "lining", "trim"};
            case "gargoyle_perched", "gargoyle_statue_lower", "gargoyle_statue_upper", "gargoyle_statue_item",
                 "gargoyle_bust" -> new String[] {"stone", "plinth"};
            default -> throw new IllegalArgumentException("unknown OBJ model " + obj);
        };
        BlockModelBuilder model = models().withExistingParent(name, modLoc("block/block_no_ao"))
                .customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc("models/block/" + obj + ".obj"))
                .flipV(true)
                .end()
                .texture("particle", textures[0]);
        for (int i = 0; i < slots.length; i++) {
            model.texture(slots[i], textures[i]);
        }
        return model;
    }

    /**
     * The sarcophagi, iron maiden, gibbet, racks, coffins, niches, skull pike, bone pile and
     * chandelier. Their models are built from blockbench/*.bbmodel by
     * tools/bbmodel_to_block_models.py into src/main/resources - the coffins' from
     * tools/gen_obj_models.py - and only referenced here. All are authored facing north, so a
     * FACING turns them by that facing's yaw + 180.
     */
    private void furniture() {
        // sarcophagus: per-material children of the stone templates, one per part and lid frame
        Map<DeferredHolder<Block, Block>, ResourceLocation[]> materials = Map.of(
                ModBlocks.STONE_SARCOPHAGUS, new ResourceLocation[] {mcLoc("block/chiseled_stone_bricks"),
                        mcLoc("block/smooth_stone"), mcLoc("block/polished_andesite")},
                ModBlocks.DEEPSLATE_SARCOPHAGUS, new ResourceLocation[] {mcLoc("block/chiseled_deepslate"),
                        mcLoc("block/polished_deepslate"), mcLoc("block/deepslate_tiles")});
        materials.forEach((block, tex) -> {
            String name = block.getId().getPath();
            // The block shows only its body: SarcophagusRenderer always draws the lid, from the
            // lid-only models (registered as additional models in ClientSetup). "closed", body and
            // lid together, is for the item, which has no renderer to draw its lid.
            for (String part : new String[] {"head", "foot"}) {
                sarcophagusModel(name, part, "lid", tex);
                sarcophagusModel(name, part, "closed", tex);
            }
            getVariantBuilder(block.get()).forAllStates(state -> {
                String part = state.getValue(SarcophagusBlock.PART) == BedPart.HEAD ? "head" : "foot";
                return ConfiguredModel.builder().modelFile(sarcophagusModel(name, part, "body", tex))
                        .rotationY(yaw(state.getValue(SarcophagusBlock.FACING))).build();
            });
        });

        // iron maiden: multipart, so each state layers the Blockbench body over the OBJ spikes
        MultiPartBlockStateBuilder maiden = getMultipartBuilder(ModBlocks.IRON_MAIDEN.get());
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                for (boolean open : new boolean[] {false, true}) {
                    String suffix = (half == DoubleBlockHalf.LOWER ? "lower" : "upper") + "_" + (open ? "open" : "closed");
                    ModelFile body = models().getExistingFile(modLoc("block/iron_maiden_" + suffix));
                    ModelFile spikes = objModel("iron_maiden_spikes_" + suffix, "iron_maiden_spikes_" + suffix,
                            modLoc("block/polished_dark_iron"));
                    for (ModelFile model : new ModelFile[] {body, spikes}) {
                        maiden.part().modelFile(model).rotationY(yaw(facing)).addModel()
                                .condition(IronMaidenBlock.FACING, facing)
                                .condition(IronMaidenBlock.HALF, half)
                                .condition(IronMaidenBlock.OPEN, open).end();
                    }
                }
            }
        }

        // gibbet: one model per part
        getVariantBuilder(ModBlocks.GIBBET.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc("block/gibbet_"
                        + state.getValue(GibbetBlock.PART).getSerializedName())))
                .rotationY(yaw(state.getValue(GibbetBlock.FACING))).build());

        // pillory and rack: one Blockbench model per part and state, the occupied ones their own
        for (DeferredHolder<Block, Block> block : List.of(ModBlocks.PILLORY, ModBlocks.OCCUPIED_PILLORY)) {
            String name = block.getId().getPath();
            getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/" + name + "_"
                            + (state.getValue(PilloryBlock.HALF) == DoubleBlockHalf.LOWER ? "lower"
                            : "upper_" + (state.getValue(PilloryBlock.OPEN) ? "open" : "closed")))))
                    .rotationY(yaw(state.getValue(PilloryBlock.FACING))).build());
        }
        for (DeferredHolder<Block, Block> block : List.of(ModBlocks.TORTURE_RACK, ModBlocks.OCCUPIED_TORTURE_RACK)) {
            String name = block.getId().getPath();
            getVariantBuilder(block.get()).forAllStates(state -> {
                String part = state.getValue(TortureRackBlock.PART).getSerializedName();
                // the empty rack's middle does not change with tension: one model for all three
                // (tools/bbmodel_to_block_models.py writes it once)
                String model = block == ModBlocks.TORTURE_RACK && part.equals("middle")
                        ? name + "_middle"
                        : name + "_" + part + "_" + state.getValue(TortureRackBlock.TENSION);
                return ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(modLoc("block/" + model)))
                        .rotationY(yaw(state.getValue(TortureRackBlock.FACING))).build();
            });
        }

        // racks: horizontalBlock's default turn is the same yaw + 180. The firewood rack has a model
        // per fill stage. The weapon rack's model is its frame alone - WeaponRackRenderer draws
        // whatever hangs in it.
        horizontalBlock(ModBlocks.FIREWOOD_RACK.get(), state -> models().getExistingFile(
                modLoc("block/firewood_rack_" + state.getValue(FirewoodRackBlock.FIREWOOD))));
        horizontalBlock(ModBlocks.WEAPON_RACK.get(), models().getExistingFile(modLoc("block/weapon_rack")));

        // coffins: OBJs from tools/gen_obj_models.py, filled per wood - planks outside, red wool
        // lining, an iron cross. Like the sarcophagus the block shows only its body: the lid-only
        // models are for SarcophagusRenderer, and the whole coffin at half size is the item's.
        ModBlocks.COFFINS.forEach(block -> {
            String name = block.getId().getPath();
            String wood = name.substring(0, name.length() - "_coffin".length());
            ResourceLocation[] tex = {mcLoc("block/" + wood + "_planks"), mcLoc("block/red_wool"), modLoc("block/dark_iron")};
            Map<BedPart, ModelFile> bodies = Map.of(
                    BedPart.HEAD, objModel(name + "_head_body", "coffin_head_body", tex),
                    BedPart.FOOT, objModel(name + "_foot_body", "coffin_foot_body", tex));
            objModel(name + "_head_lid", "coffin_head_lid", tex);
            objModel(name + "_foot_lid", "coffin_foot_lid", tex);
            objModel(name + "_item", "coffin_item", tex);
            getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(bodies.get(state.getValue(CoffinBlock.PART)))
                    .rotationY(yaw(state.getValue(CoffinBlock.FACING))).build());
        });

        // catacomb niches: a child of the Blockbench template for each REMAINS, the stone's texture
        // read off the source block's id, as for the capstones
        ModBlocks.CATACOMB_NICHES.forEach((block, source) -> {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(source.get());
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
            Map<CatacombNicheBlock.Remains, ModelFile> niches = new EnumMap<>(CatacombNicheBlock.Remains.class);
            for (CatacombNicheBlock.Remains remains : CatacombNicheBlock.Remains.values()) {
                String suffix = "_" + remains.getSerializedName();
                niches.put(remains, models().withExistingParent(block.getId().getPath() + suffix,
                        modLoc("block/template_catacomb_niche" + suffix)).texture("stone", texture).texture("particle", texture));
            }
            horizontalBlock(block.get(), state -> niches.get(state.getValue(CatacombNicheBlock.REMAINS)));
        });

        // skull pike: one model per half
        // tapestries: a model per part, each the cloth's 16x16 window of the one 64x48 texture, a
        // plane hanging from the rod's centreline, 1px off the wall; the top row carries the rod, flush
        // against the wall, which runs 1px past each end.
        // Authored facing north, the wall to the south; column 0 is the east end, the viewer's left.
        ModBlocks.TAPESTRIES.forEach(block -> {
            String name = block.getId().getPath();
            ModelFile[][] parts = new ModelFile[TapestryBlock.WIDTH][TapestryBlock.HEIGHT];
            float rowV = 16F / TapestryBlock.HEIGHT;
            float colU = 16F / TapestryBlock.WIDTH;
            for (int c = 0; c < TapestryBlock.WIDTH; c++) {
                for (int r = 0; r < TapestryBlock.HEIGHT; r++) {
                    float v0 = (TapestryBlock.HEIGHT - 1 - r) * rowV;
                    BlockModelBuilder model = models().withExistingParent(name + "_" + c + "_" + r, mcLoc("block/block"))
                            .renderType("minecraft:cutout").ao(false)
                            .texture("cloth", modLoc("block/" + name)).texture("rod", mcLoc("block/dark_oak_log"))
                            .texture("particle", modLoc("block/" + name));
                    model.element().from(0, 0, 15).to(16, 16, 15)
                            .face(Direction.NORTH).uvs(c * colU, v0, (c + 1) * colU, v0 + rowV).texture("#cloth").end()
                            .face(Direction.SOUTH).uvs((c + 1) * colU, v0, c * colU, v0 + rowV).texture("#cloth").end()
                            .end();
                    if (r == TapestryBlock.HEIGHT - 1) {
                        float x0 = c == TapestryBlock.WIDTH - 1 ? -1 : 0;
                        float x1 = c == 0 ? 17 : 16;
                        model.element().from(x0, 14, 14).to(x1, 16, 16)
                                .allFaces((dir, face) -> face.uvs(0, 0, dir.getAxis() == Direction.Axis.X ? 2 : 16, 2).texture("#rod"))
                                .end();
                    }
                    parts[c][r] = model;
                }
            }
            getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(parts[state.getValue(TapestryBlock.COLUMN)][state.getValue(TapestryBlock.ROW)])
                    .rotationY(yaw(state.getValue(TapestryBlock.FACING))).build());
        });

        // crumbling floors: the stone with faint cracks, and the cracks opened while it shakes
        ModBlocks.CRUMBLING_FLOORS.keySet().forEach(block -> {
            String name = block.getId().getPath();
            ModelFile still = models().cubeAll(name, modLoc("block/" + name));
            ModelFile shaking = models().cubeAll(name + "_shaking", modLoc("block/" + name + "_shaking"));
            getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(state.getValue(CrumblingFloorBlock.SHAKING) ? shaking : still).build());
        });

        // the other heads share the skull pike's bare lower half
        for (DeferredHolder<Block, Block> pike : List.of(ModBlocks.ZOMBIE_HEAD_PIKE, ModBlocks.BLOODY_STEVE_HEAD_PIKE)) {
            tallProp(pike, models().getExistingFile(modLoc("block/skull_pike_lower")),
                    models().getExistingFile(modLoc("block/" + pike.getId().getPath() + "_upper")));
        }
        tallProp(ModBlocks.SKULL_PIKE, models().getExistingFile(modLoc("block/skull_pike_lower")),
                models().getExistingFile(modLoc("block/skull_pike_upper")));

        // the secret passage. The lever sconce is the torch sconce to the pixel until it is
        // pulled; its pulled model (hand-authored) tips the same torch 22.5 degrees further out.
        ModelFile sconce = models().getExistingFile(modLoc("block/torch_sconce_block"));
        ModelFile pulled = models().getExistingFile(modLoc("block/lever_sconce_pulled"));
        myHorizontalBlock(ModBlocks.LEVER_SCONCE.get(), state -> state.getValue(LeverSconceBlock.POWERED) ? pulled : sconce);
        // hidden doors: vanilla's door models in the wall's own texture, top and bottom alike, so a
        // shut one is two more blocks of wall
        ModBlocks.HIDDEN_DOORS.forEach((block, source) -> {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(source.get());
            ResourceLocation wall = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
            doorBlock((DoorBlock) block.get(), wall, wall);
        });
        simpleBlock(ModBlocks.PEDESTAL.get(), models().getExistingFile(modLoc("block/pedestal")));

        // chain fixtures hung as blocks: the same Blockbench models SwingingChainRenderer draws in a
        // swinging chain's bottom link, which are symmetrical enough to need no facing
        simpleBlock(ModBlocks.MANACLES.get(), models().getExistingFile(modLoc("block/chain_fixture_manacles")));
        simpleBlock(ModBlocks.MEAT_HOOK.get(), models().getExistingFile(modLoc("block/chain_fixture_meat_hook")));
        ModelFile censer = models().getExistingFile(modLoc("block/chain_fixture_censer"));
        ModelFile censerLit = models().getExistingFile(modLoc("block/chain_fixture_censer_lit"));
        getVariantBuilder(ModBlocks.CENSER.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(CenserBlock.LIT) ? censerLit : censer).build());

        // bubbling cauldron: vanilla's full cauldron, its brew a greyscale boil tinted per colour
        // (ClientSetup), so every colour is the one model
        simpleBlock(ModBlocks.BUBBLING_CAULDRON.get(), models()
                .withExistingParent("bubbling_cauldron", mcLoc("block/template_cauldron_full"))
                .texture("content", modLoc("block/bubbling_brew"))
                .texture("inside", mcLoc("block/cauldron_inner"))
                .texture("particle", mcLoc("block/cauldron_side"))
                .texture("top", mcLoc("block/cauldron_top"))
                .texture("bottom", mcLoc("block/cauldron_bottom"))
                .texture("side", mcLoc("block/cauldron_side")));

        // rubble scatter: a model per stage, each given a random quarter turn per block, so a floor
        // of it does not repeat
        for (int chips = 1; chips <= RubbleScatterBlock.FULL; chips++) {
            getVariantBuilder(ModBlocks.RUBBLE_SCATTER.get()).partialState().with(RubbleScatterBlock.CHIPS, chips)
                    .setModels(ConfiguredModel.allYRotations(
                            models().getExistingFile(modLoc("block/rubble_scatter_" + chips)), 0, false));
        }

        // gargoyles: the gargoyle mob's model baked in stone (tools/gen_obj_models.py), on smooth
        // stone plinths. Cutout: the wings' membranes are ragged, with holes through them.
        ResourceLocation gargoyle = modLoc("block/gargoyle_statue");
        ResourceLocation plinth = mcLoc("block/smooth_stone");
        horizontalBlock(ModBlocks.PERCHED_GARGOYLE.get(),
                objModel("perched_gargoyle", "gargoyle_perched", gargoyle, plinth).renderType("minecraft:cutout"));
        horizontalBlock(ModBlocks.GARGOYLE_BUST.get(),
                objModel("gargoyle_bust", "gargoyle_bust", gargoyle, plinth).renderType("minecraft:cutout"));
        tallProp(ModBlocks.GARGOYLE_STATUE,
                objModel("gargoyle_statue_lower", "gargoyle_statue_lower", gargoyle, plinth).renderType("minecraft:cutout"),
                objModel("gargoyle_statue_upper", "gargoyle_statue_upper", gargoyle, plinth).renderType("minecraft:cutout"));
        objModel("gargoyle_statue_item", "gargoyle_statue_item", gargoyle, plinth).renderType("minecraft:cutout");

        // bone pile: a model per stage
        horizontalBlock(ModBlocks.BONE_PILE.get(), state -> models().getExistingFile(
                modLoc("block/bone_pile_" + state.getValue(BonePileBlock.BONES))));

        // chandelier: lit and unlit differ only in the candles' wicks
        getVariantBuilder(ModBlocks.CHANDELIER.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc(state.getValue(ChandelierBlock.LIT)
                        ? "block/chandelier_lit" : "block/chandelier"))).build());
    }

    /** A TallPropBlock: a model per half, turned to its FACING. */
    private void tallProp(DeferredHolder<Block, Block> block, ModelFile lower, ModelFile upper) {
        getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(TallPropBlock.HALF) == DoubleBlockHalf.LOWER ? lower : upper)
                .rotationY(yaw(state.getValue(TallPropBlock.FACING))).build());
    }

    private ModelFile sarcophagusModel(String name, String part, String variant, ResourceLocation[] tex) {
        return models().withExistingParent(name + "_" + part + "_" + variant,
                        modLoc("block/template_sarcophagus_" + part + "_" + variant))
                .texture("side", tex[0]).texture("lid", tex[1]).texture("effigy", tex[2])
                .texture("particle", tex[1]);
    }

    /** The y rotation that turns a north-authored model to face `facing`. */
    private static int yaw(Direction facing) {
        return ((int) facing.toYRot() + 180) % 360;
    }

    /**
     * A sharpened log: stripped-log facets over the pyramid, with the stripped log's end grain on
     * the (normally hidden) base. The OBJ points up; directionalBlock turns it to each FACING the way
     * vanilla turns an end rod.
     */
    public void sharpenedLog(DeferredHolder<Block, Block> block) {
        String name = block.getId().getPath();
        // ids follow vanilla's stripped block word for word: sharpened_oak_log <- stripped_oak_log
        String stripped = "stripped_" + name.substring("sharpened_".length());
        directionalBlock(block.get(), objModel(name, "pyramid",
                mcLoc("block/" + stripped), mcLoc("block/" + stripped + "_top")));
    }

    /**
     * A capstone: the pyramid in its source stone. The texture is the source block's own id, in its
     * own namespace - true of every source - except the smooth sandstones, whose blocks have no
     * texture of their own and use the sandstone top, as vanilla does.
     */
    public void capstone(DeferredHolder<Block, Block> block, Block source) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(source);
        String path = switch (id.getPath()) {
            case "smooth_sandstone" -> "sandstone_top";
            case "smooth_red_sandstone" -> "red_sandstone_top";
            default -> id.getPath();
        };
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + path);
        directionalBlock(block.get(), objModel(block.getId().getPath(), "pyramid", texture, texture));
    }

    /**
     * Iron bars in another texture: children of vanilla's six iron bars models with the texture
     * swapped, joined by the same multipart rules as vanilla's own iron_bars blockstate - a post
     * always, a lone post when unconnected, a cap when connected one way only, a side per
     * connection. The vanilla parents declare ambientocclusion false at their root, so it holds.
     */
    public void ironBars(DeferredHolder<Block, Block> block) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/" + name);
        Map<String, ModelFile> parts = new java.util.HashMap<>();
        for (String part : new String[] {"post_ends", "post", "cap", "cap_alt", "side", "side_alt"}) {
            parts.put(part, models().withExistingParent(name + "_" + part, mcLoc("block/iron_bars_" + part))
                    .texture("particle", texture).texture("bars", texture).texture("edge", texture)
                    .renderType("minecraft:cutout_mipped"));
        }
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());
        builder.part().modelFile(parts.get("post_ends")).addModel().end();
        builder.part().modelFile(parts.get("post")).addModel()
                .condition(IronBarsBlock.NORTH, false).condition(IronBarsBlock.EAST, false)
                .condition(IronBarsBlock.SOUTH, false).condition(IronBarsBlock.WEST, false).end();
        // [model, y rotation, the one connected side]
        Object[][] caps = {{"cap", 0, IronBarsBlock.NORTH}, {"cap", 90, IronBarsBlock.EAST},
                {"cap_alt", 0, IronBarsBlock.SOUTH}, {"cap_alt", 90, IronBarsBlock.WEST}};
        for (Object[] cap : caps) {
            MultiPartBlockStateBuilder.PartBuilder part = builder.part().modelFile(parts.get((String) cap[0]))
                    .rotationY((Integer) cap[1]).addModel();
            for (BooleanProperty side : new BooleanProperty[] {IronBarsBlock.NORTH, IronBarsBlock.EAST,
                    IronBarsBlock.SOUTH, IronBarsBlock.WEST}) {
                part.condition(side, side == cap[2]);
            }
            part.end();
        }
        builder.part().modelFile(parts.get("side")).addModel().condition(IronBarsBlock.NORTH, true).end();
        builder.part().modelFile(parts.get("side")).rotationY(90).addModel().condition(IronBarsBlock.EAST, true).end();
        builder.part().modelFile(parts.get("side_alt")).addModel().condition(IronBarsBlock.SOUTH, true).end();
        builder.part().modelFile(parts.get("side_alt")).rotationY(90).addModel().condition(IronBarsBlock.WEST, true).end();
    }

    /**
     * A vanilla-shaped cutout door whose thin side edges and caps come from their own texture,
     * {@code <textureName>_edge}, instead of being cut out of the face textures.
     *
     * <p>Vanilla's door models cut BOTH side edges from face columns 0-2 - the hinge side - and the
     * caps from the end rows. On a solid door that is invisible; on a see-through one the edges show
     * gaps between bars, and the handle edge shows hinges. The {@code template_edged_door_*} parents
     * are vanilla's eight door models with those five faces pointed at {@code #edge}; the strip
     * layout it expects is documented in tools/gen_iron_bars_door_textures.py.
     */
    public void edgedDoor(DeferredHolder<Block, Block> block, String textureName) {
        String name = block.getId().getPath();
        ResourceLocation bottom = modLoc("block/" + textureName + "_bottom");
        ResourceLocation top = modLoc("block/" + textureName + "_top");
        ResourceLocation edge = modLoc("block/" + textureName + "_edge");
        ModelFile[] m = new ModelFile[8];
        String[] parts = {"bottom_left", "bottom_left_open", "bottom_right", "bottom_right_open",
                "top_left", "top_left_open", "top_right", "top_right_open"};
        for (int i = 0; i < parts.length; i++) {
            m[i] = models().withExistingParent(name + "_" + parts[i], modLoc("block/template_edged_door_" + parts[i]))
                    .texture("bottom", bottom).texture("top", top).texture("edge", edge)
                    .renderType("minecraft:cutout");
        }
        doorBlock((DoorBlock) block.get(), m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]);
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

    /**
     * Like {@link #allDirectionBlock}, but also applies AngleCobwebBlock.ROTATION as the yaw for the
     * UP/DOWN states, so a floor or ceiling mount still has all four quarter-turns instead of
     * collapsing to one fixed orientation.
     *
     * <p>{@link AngleCobwebBlock#HALF} picks the <em>model</em> rather than a rotation:
     * {@code TOP} gathers the web at the ceiling, {@code BOTTOM} at the floor, and the two differ
     * only in the strand's vertical uv. It cannot be a rotation &mdash; blockstates rotate about x
     * and y only, and an {@code x: 180} flip would carry the sheet round to the opposite face of
     * the cell and so change which wall the web belongs to.</p>
     */
    public void angleCobwebBlock(DeferredHolder<Block, Block> block) {
        String path = block.getId().getPath();
        ModelFile ceilingModel = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/" + path));
        ModelFile floorModel = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/" + path + "_floor"));
        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(FACING);
            int xRot = 0;
            int yRot;
            if (dir == Direction.DOWN) {
                xRot = 90;
                yRot = state.getValue(AngleCobwebBlock.ROTATION) * 90;
            } else if (dir == Direction.UP) {
                xRot = -90;
                yRot = state.getValue(AngleCobwebBlock.ROTATION) * 90;
            } else {
                yRot = ((int) dir.toYRot() + 180) % 360;
            }
            ModelFile model = state.getValue(AngleCobwebBlock.HALF) == Half.TOP ? ceilingModel : floorModel;
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot % 360)
                    .build();
        });
    }

    public void candleSconceBlock(DeferredHolder<Block, Block> block) {
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

            /*
             * The models are drawn for a north-facing block, so the base rotation just
             * turns the piece to its facing. LEFT and RIGHT are relative to that facing,
             * and a left-hand corner is the right-hand model given one more quarter-turn
             * clockwise - the same rule for all four facings, and the same extra 90
             * degrees IFacadeShapeBlock#getBlockShapeIndex gives the collision box.
             */
            int yRot = (int)facing.getOpposite().toYRot();
            if (shape == FacadeShape.INNER_LEFT || shape == FacadeShape.OUTER_LEFT) {
                yRot = (yRot + 90) % 360;
            }

            ModelFile model = switch (shape) {
                case STRAIGHT -> normal;
                case INNER_LEFT, INNER_RIGHT -> inner;
                case OUTER_LEFT, OUTER_RIGHT -> outer;
            };

            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).uvLock(true).build();
        }, new Property[]{WaterloggedNonCubeFacingBlock.WATERLOGGED, FacadeShapeBlock.WATERLOGGED});
    }

    public void basedBlock(DeferredHolder<Block, ? extends Block> block, String baseName, ResourceLocation texture) {
        ModelFile model = models().singleTexture(block.getId().getPath(), modLoc("block/" + baseName), "0", texture);
        myDirectionalBlock((Block)block.get(), (ModelFile)model);
    }

    public void wallRingBlock(DeferredHolder<Block, ? extends Block> block) {
       ModelFile model = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/wall_ring"));
       ModelFile openModel = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/wall_ring_open"));
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
                    Direction facing = state.getValue(FacadeShapeBlock.FACING);
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
        ModelFile brazier_soul_lit = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/brazier_soul_lit_block"));
        ModelFile brazier_embers = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/brazier_embers_block"));
        ModelFile brazier = models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/brazier_block"));
        brazierBlock(block.get(), brazier, brazier_embers, brazier_lit, brazier_soul_lit);
    }

    public void brazierBlock(Block block, ModelFile brazier, ModelFile brazier_embers, ModelFile brazier_lit, ModelFile brazier_soul_lit) {
        // one model per FIRE value; WATERLOGGED is left unspecified since it changes no geometry.
        getVariantBuilder(block)
                .partialState().with(BrazierBlock.FIRE, BrazierBlock.BrazierFire.NONE).addModels(new ConfiguredModel(brazier))
                .partialState().with(BrazierBlock.FIRE, BrazierBlock.BrazierFire.EMBERS).addModels(new ConfiguredModel(brazier_embers))
                .partialState().with(BrazierBlock.FIRE, BrazierBlock.BrazierFire.SOUL).addModels(new ConfiguredModel(brazier_soul_lit))
                .partialState().with(BrazierBlock.FIRE, BrazierBlock.BrazierFire.LIT).addModels(new ConfiguredModel(brazier_lit));
    }

    /**
     * A bed-like two-block table. Both halves share the {@code block/slab_table} geometry - it is
     * symmetric in both horizontal axes, so the HEAD needs no separate rotation - and differ only in
     * the texture their child model resolves.
     *
     * <p>The template is authored facing NORTH while {@link Direction#toYRot()} puts SOUTH at 0, so
     * the +180 offset is what makes facing=north come out unrotated.
     */
    public void slabTableBlock(DeferredHolder<Block, Block> block, ResourceLocation footTexture, ResourceLocation headTexture) {
        String name = block.getId().getPath();
        ModelFile foot = slabTableHalf(name + "_foot", footTexture);
        ModelFile head = slabTableHalf(name + "_head", headTexture);

        getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(SlabTableBlock.PART) == BedPart.FOOT ? foot : head)
                .rotationY(((int) state.getValue(SlabTableBlock.FACING).toYRot() + 180) % 360)
                .build());
    }

    /** The blockstate and models of one decorative block, by its type. */
    private void decorBlock(DecorType.DecorBlock decor) {
        DeferredHolder<Block, Block> b = decor.block();
        ResourceLocation texture = decor.material().texture();
        String name = b.getId().getPath();
        switch (decor.type()) {
            case ARROW_SLIT -> arrowSlitBlock(b, texture);
            case BARRED_WINDOW -> barredWindowBlock(b, texture);
            case BARRED_WINDOW_FACADE -> barredWindowFacadeBlock(b, texture);
            case CORBEL -> horizontalSingleTexture(b, modLoc(ModelProvider.BLOCK_FOLDER + "/corbel_block"), texture);
            // sills are OBJs (tools/gen_obj_models.py): their slopes are true slopes
            case DOUBLE_SILL -> myHorizontalBlock(b.get(), objModel(name, "double_sill", texture));
            case SILL -> myHorizontalBlock(b.get(), objModel(name, "sill", texture));
            case FLUTED_FACADE -> flutedFacadeBlock(b, texture);
            case FLUTED -> simpleSingleTexture(b, modLoc("block/fluted_block_base"), texture);
            case LEDGE -> ledgeBlock(b, texture);
            case CORNICE -> facadeBlock(b, "cornice", texture);
            case CROWN_MOLDING -> facadeBlock(b, "crown_molding", texture);
            case QUARTER_FACADE -> facadeBlock(b, "quarter_facade", texture);
            case FACADE -> facadeBlock(b, "facade", texture);
            case PILLAR_BASE -> basedBlock(b, "pillar_base_block_base", texture);
            case PILLAR -> basedBlock(b, "pillar_block_base", texture);
        }
    }

    /** Vanilla's copper block texture at an age: copper_block, exposed_copper, ... */
    private ResourceLocation copperTexture(WeatherState age) {
        return mcLoc("block/" + (age == WeatherState.UNAFFECTED ? "copper_block" : CopperFamily.id(age, "copper")));
    }

    /** The mod's dark iron texture at a rust stage: dark_iron, tarnished_dark_iron, ... */
    private ResourceLocation darkIronTexture(AgedIronFamily.Age age) {
        return modLoc("block/" + age.id("dark_iron"));
    }

    private ModelFile slabTableHalf(String name, ResourceLocation texture) {
        return models().withExistingParent(name, modLoc("block/slab_table")).texture("all", texture);
    }

    public ModelFile rectangleLeft(String name, ResourceLocation texture, ResourceLocation texture2) {
        return ((BlockModelBuilder)((BlockModelBuilder)models().withExistingParent(name, modLoc("block/rectangle_left"))).texture("left", texture)).texture("right", texture2);
    }

    public ModelFile rectangleRight(String name, ResourceLocation texture, ResourceLocation texture2) {
        return ((BlockModelBuilder)((BlockModelBuilder)models().withExistingParent(name, modLoc("block/rectangle_right"))).texture("right", texture)).texture("left", texture2);
    }

    /** A large brick: two blocks, its left and right halves, each textured with both. */
    public void largeBrickPair(DeferredHolder<Block, Block> left, DeferredHolder<Block, Block> right) {
        rectangleLeftHorizontalBlock(left, right);
        rectangleRightHorizontalBlock(right, left);
    }

    public void rectangleLeftHorizontalBlock(DeferredHolder<Block, Block> block, DeferredHolder<Block, Block> block2) {
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
            // NO uvLock. uvLock rotates the UVs of faces perpendicular to the rotation axis, so with a
            // y rotation it only touches up/down faces. The candle texture (minecraft:block/candle*)
            // only has pixels in columns 0-1, so a rotated UV lands in the empty right-hand side of the
            // sheet and every candle top sampled fully transparent texels - rendering black in the solid
            // layer, or invisible once the models correctly declared render_type cutout. torch_sconce
            // never set uvLock and has always been fine. uvLock is for world-aligned tiling textures
            // (see the facade corner code), not for a hand-authored model like this one.
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) facing.getOpposite().toYRot())
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

    /**
     * Blockstate/model generation for {@link TallDoorBlock}. The BOTTOM and TOP models reuse the
     * same ornate-hinge templates as the 2-tall dungeon doors; every MIDDLE segment - regardless
     * of how many a given door has - shares one of just two models (mirrored left/right, same as
     * BOTTOM/TOP), rotation-agnostic since it needs no separate open state, so a 3-tall and a
     * 4-tall door of the same wood need no extra models between them.
     */
    public void tallDungeonDoorBlock(TallDoorBlock block, ResourceLocation bottom, ResourceLocation middle, ResourceLocation top) {
        String baseName = key(block).toString();
        ModelFile bottomLeft = doorBottomLeft(baseName + "_bottom_left", bottom, top);
        ModelFile bottomLeftOpen = doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top);
        ModelFile bottomRight = doorBottomRight(baseName + "_bottom_right", bottom, top);
        ModelFile bottomRightOpen = doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top);
        ModelFile topLeft = doorTopLeft(baseName + "_top_left", bottom, top);
        ModelFile topLeftOpen = doorTopLeftOpen(baseName + "_top_left_open", bottom, top);
        ModelFile topRight = doorTopRight(baseName + "_top_right", bottom, top);
        ModelFile topRightOpen = doorTopRightOpen(baseName + "_top_right_open", bottom, top);
        ModelFile middleLeft = models().withExistingParent(baseName + "_middle_left", "dungeonblocks:block/dungeon_door_middle_left")
                .texture("middle", middle);
        ModelFile middleRight = models().withExistingParent(baseName + "_middle_right", "dungeonblocks:block/dungeon_door_middle_right")
                .texture("middle", middle);
        tallDoorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen,
                middleLeft, middleRight, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    /**
     * Generalizes the vanilla-style door blockstate (normally emitted by the built-in
     * {@code BlockStateProvider#doorBlock}, which is hardwired to the 2-value HALF property) to
     * {@link TallDoorBlock}'s 3-value SEGMENT property. The Y-rotation formula is reverse-engineered
     * from the vanilla/dungeon-door blockstate output: a closed door rotates with FACING
     * ({@code (facing.toYRot() + 90) % 360}), and an open door additionally rotates +90 (hinge
     * LEFT) or -90 (hinge RIGHT) off of that, since the open model is pre-built swung into the room.
     */
    private void tallDoorBlock(TallDoorBlock block,
            ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen,
            ModelFile middleLeft, ModelFile middleRight,
            ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            DoorSegment segment = state.getValue(TallDoorBlock.SEGMENT);
            boolean open = state.getValue(TallDoorBlock.OPEN);
            boolean hingeRight = state.getValue(TallDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            Direction facing = state.getValue(TallDoorBlock.FACING);

            ModelFile model;
            switch (segment) {
                case BOTTOM:
                    model = hingeRight ? (open ? bottomRightOpen : bottomRight) : (open ? bottomLeftOpen : bottomLeft);
                    break;
                case TOP:
                    model = hingeRight ? (open ? topRightOpen : topRight) : (open ? topLeftOpen : topLeft);
                    break;
                default:
                    model = hingeRight ? middleRight : middleLeft;
                    break;
            }

            int closedYRot = ((int) facing.toYRot() + 90) % 360;
            int yRot = open ? (closedYRot + (hingeRight ? 270 : 90)) % 360 : closedYRot;

            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        }, TallDoorBlock.POWERED);
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