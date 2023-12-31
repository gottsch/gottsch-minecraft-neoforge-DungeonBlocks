package mod.gottsch.neo.dungeonblocks.core.block;

import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

public class CorbelBlocks {

    public static RegistryObject<Block> ACACIA_CORBEL = Registration.BLOCKS.register("acacia_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static RegistryObject<Block> BIRCH_CORBEL = Registration.BLOCKS.register("birch_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS)));
      public static RegistryObject<Block> CHERRY_CORBEL = Registration.BLOCKS.register("cherry_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_PLANKS)));
      public static RegistryObject<Block> DARK_OAK_CORBEL = Registration.BLOCKS.register("dark_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static RegistryObject<Block> JUNGLE_CORBEL = Registration.BLOCKS.register("jungle_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static RegistryObject<Block> MANGROVE_CORBEL = Registration.BLOCKS.register("mangrove_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS)));
    public static RegistryObject<Block> OAK_CORBEL = Registration.BLOCKS.register("oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static RegistryObject<Block> SPRUCE_CORBEL = Registration.BLOCKS.register("spruce_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));

    // stripped variants
    public static RegistryObject<Block> STRIPPED_ACACIA_CORBEL = Registration.BLOCKS.register("stripped_acacia_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_WOOD)));
    public static RegistryObject<Block> STRIPPED_BIRCH_CORBEL = Registration.BLOCKS.register("stripped_birch_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_BIRCH_WOOD)));
    public static RegistryObject<Block> STRIPPED_CHERRY_CORBEL = Registration.BLOCKS.register("stripped_cherry_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_CHERRY_WOOD)));
    public static RegistryObject<Block> STRIPPED_DARK_OAK_CORBEL = Registration.BLOCKS.register("stripped_dark_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_DARK_OAK_WOOD)));
    public static RegistryObject<Block> STRIPPED_JUNGLE_CORBEL = Registration.BLOCKS.register("stripped_jungle_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_WOOD)));
    public static RegistryObject<Block> STRIPPED_MANGROVE_CORBEL = Registration.BLOCKS.register("stripped_mangrove_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_WOOD)));
    public static RegistryObject<Block> STRIPPED_OAK_CORBEL = Registration.BLOCKS.register("stripped_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static RegistryObject<Block> STRIPPED_SPRUCE_CORBEL = Registration.BLOCKS.register("stripped_spruce_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_SPRUCE_WOOD)));


    public static RegistryObject<Block> STONE_CORBEL = Registration.BLOCKS.register("stone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> SMOOTH_STONE_CORBEL = Registration.BLOCKS.register("smooth_stone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_STONE)));
    public static RegistryObject<Block> COBBLESTONE_CORBEL = Registration.BLOCKS.register("cobblestone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static RegistryObject<Block> MOSSY_COBBLESTONE_CORBEL = Registration.BLOCKS.register("mossy_cobblestone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE)));

    public static RegistryObject<Block> STONE_BRICKS_CORBEL = Registration.BLOCKS.register("stone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static RegistryObject<Block> MOSSY_STONE_BRICKS_CORBEL = Registration.BLOCKS.register("mossy_stone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_STONE_BRICKS)));

    public static RegistryObject<Block> GRANITE_CORBEL = Registration.BLOCKS.register("granite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.GRANITE)));
    public static RegistryObject<Block> ANDESITE_CORBEL = Registration.BLOCKS.register("andesite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static RegistryObject<Block> DIORITE_CORBEL = Registration.BLOCKS.register("diorite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static RegistryObject<Block> POLISHED_GRANITE_CORBEL = Registration.BLOCKS.register("polished_granite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE)));
    public static RegistryObject<Block> POLISHED_ANDESITE_CORBEL = Registration.BLOCKS.register("polished_andesite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static RegistryObject<Block> POLISHED_DIORITE_CORBEL = Registration.BLOCKS.register("polished_diorite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE)));

    public static RegistryObject<Block> BLACKSTONE_CORBEL = Registration.BLOCKS.register("blackstone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_CORBEL = Registration.BLOCKS.register("polished_blackstone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_CORBEL = Registration.BLOCKS.register("polished_blackstone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static RegistryObject<Block> DEEPSLATE_CORBEL = Registration.BLOCKS.register("deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_BRICKS_CORBEL = Registration.BLOCKS.register("deepslate_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static RegistryObject<Block> COBBLED_DEEPSLATE_CORBEL = Registration.BLOCKS.register("cobbled_deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static RegistryObject<Block> POLISHED_DEEPSLATE_CORBEL = Registration.BLOCKS.register("polished_deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_TILES_CORBEL = Registration.BLOCKS.register("deepslate_tiles_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));

    public static void register() {
        // this method exists simply to ensure that the static objects are registered before
        // the DeferredRegistry is called.
    }
}
