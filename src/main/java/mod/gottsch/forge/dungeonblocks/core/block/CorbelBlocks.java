package mod.gottsch.forge.dungeonblocks.core.block;

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CorbelBlocks {

    public static DeferredHolder<Block, Block> ACACIA_CORBEL = Registration.BLOCKS.register("acacia_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));
    public static DeferredHolder<Block, Block> BIRCH_CORBEL = Registration.BLOCKS.register("birch_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS)));
      public static DeferredHolder<Block, Block> CHERRY_CORBEL = Registration.BLOCKS.register("cherry_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS)));
      public static DeferredHolder<Block, Block> DARK_OAK_CORBEL = Registration.BLOCKS.register("dark_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS)));
    public static DeferredHolder<Block, Block> JUNGLE_CORBEL = Registration.BLOCKS.register("jungle_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS)));
    public static DeferredHolder<Block, Block> MANGROVE_CORBEL = Registration.BLOCKS.register("mangrove_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS)));
    public static DeferredHolder<Block, Block> OAK_CORBEL = Registration.BLOCKS.register("oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static DeferredHolder<Block, Block> SPRUCE_CORBEL = Registration.BLOCKS.register("spruce_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)));

    // stripped variants
    public static DeferredHolder<Block, Block> STRIPPED_ACACIA_CORBEL = Registration.BLOCKS.register("stripped_acacia_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_ACACIA_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_BIRCH_CORBEL = Registration.BLOCKS.register("stripped_birch_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_BIRCH_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_CHERRY_CORBEL = Registration.BLOCKS.register("stripped_cherry_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CHERRY_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_DARK_OAK_CORBEL = Registration.BLOCKS.register("stripped_dark_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_DARK_OAK_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_JUNGLE_CORBEL = Registration.BLOCKS.register("stripped_jungle_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_JUNGLE_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_MANGROVE_CORBEL = Registration.BLOCKS.register("stripped_mangrove_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_OAK_CORBEL = Registration.BLOCKS.register("stripped_oak_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));
    public static DeferredHolder<Block, Block> STRIPPED_SPRUCE_CORBEL = Registration.BLOCKS.register("stripped_spruce_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_WOOD)));


    public static DeferredHolder<Block, Block> STONE_CORBEL = Registration.BLOCKS.register("stone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> SMOOTH_STONE_CORBEL = Registration.BLOCKS.register("smooth_stone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE)));
    public static DeferredHolder<Block, Block> COBBLESTONE_CORBEL = Registration.BLOCKS.register("cobblestone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static DeferredHolder<Block, Block> MOSSY_COBBLESTONE_CORBEL = Registration.BLOCKS.register("mossy_cobblestone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE)));

    public static DeferredHolder<Block, Block> STONE_BRICKS_CORBEL = Registration.BLOCKS.register("stone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static DeferredHolder<Block, Block> MOSSY_STONE_BRICKS_CORBEL = Registration.BLOCKS.register("mossy_stone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

    public static DeferredHolder<Block, Block> GRANITE_CORBEL = Registration.BLOCKS.register("granite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRANITE)));
    public static DeferredHolder<Block, Block> ANDESITE_CORBEL = Registration.BLOCKS.register("andesite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE)));
    public static DeferredHolder<Block, Block> DIORITE_CORBEL = Registration.BLOCKS.register("diorite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE)));
    public static DeferredHolder<Block, Block> POLISHED_GRANITE_CORBEL = Registration.BLOCKS.register("polished_granite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_GRANITE)));
    public static DeferredHolder<Block, Block> POLISHED_ANDESITE_CORBEL = Registration.BLOCKS.register("polished_andesite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_ANDESITE)));
    public static DeferredHolder<Block, Block> POLISHED_DIORITE_CORBEL = Registration.BLOCKS.register("polished_diorite_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE)));

    public static DeferredHolder<Block, Block> BLACKSTONE_CORBEL = Registration.BLOCKS.register("blackstone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE)));
    public static DeferredHolder<Block, Block> POLISHED_BLACKSTONE_CORBEL = Registration.BLOCKS.register("polished_blackstone_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE)));
    public static DeferredHolder<Block, Block> POLISHED_BLACKSTONE_BRICKS_CORBEL = Registration.BLOCKS.register("polished_blackstone_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static DeferredHolder<Block, Block> DEEPSLATE_CORBEL = Registration.BLOCKS.register("deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));
    public static DeferredHolder<Block, Block> DEEPSLATE_BRICKS_CORBEL = Registration.BLOCKS.register("deepslate_bricks_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));
    public static DeferredHolder<Block, Block> COBBLED_DEEPSLATE_CORBEL = Registration.BLOCKS.register("cobbled_deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE)));
    public static DeferredHolder<Block, Block> POLISHED_DEEPSLATE_CORBEL = Registration.BLOCKS.register("polished_deepslate_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DEEPSLATE)));
    public static DeferredHolder<Block, Block> DEEPSLATE_TILES_CORBEL = Registration.BLOCKS.register("deepslate_tiles_corbel_block",
            () -> new CorbelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

    public static void register() {
        // this method exists simply to ensure that the static objects are registered before
        // the DeferredRegistry is called.
    }
}
