package mod.gottsch.forge.dungeonblocks.core.block;

import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @author Mark Gottschling on Jan 12, 2020
 */
public class LedgeBlocks {
    public static DeferredHolder<Block, Block> STONE_LEDGE = Registration.BLOCKS.register("stone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> SMOOTH_STONE_LEDGE = Registration.BLOCKS.register("smooth_stone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> COBBLESTONE_LEDGE = Registration.BLOCKS.register("cobblestone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> MOSSY_COBBLESTONE_LEDGE = Registration.BLOCKS.register("mossy_cobblestone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> BRICKS_LEDGE = Registration.BLOCKS.register("bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> STONE_BRICKS_LEDGE = Registration.BLOCKS.register("stone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> MOSSY_STONE_BRICKS_LEDGE = Registration.BLOCKS.register("mossy_stone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static DeferredHolder<Block, Block> LIGHT_GRAY_CONCRETE_LEDGE = Registration.BLOCKS.register("light_gray_concrete_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));


    public static DeferredHolder<Block, Block> GRANITE_LEDGE = Registration.BLOCKS.register("granite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRANITE)));
    public static DeferredHolder<Block, Block> ANDESITE_LEDGE = Registration.BLOCKS.register("andesite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE)));
    public static DeferredHolder<Block, Block> DIORITE_LEDGE = Registration.BLOCKS.register("diorite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE)));
    public static DeferredHolder<Block, Block> POLISHED_GRANITE_LEDGE = Registration.BLOCKS.register("polished_granite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_GRANITE)));
    public static DeferredHolder<Block, Block> POLISHED_ANDESITE_LEDGE = Registration.BLOCKS.register("polished_andesite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_ANDESITE)));
    public static DeferredHolder<Block, Block> POLISHED_DIORITE_LEDGE = Registration.BLOCKS.register("polished_diorite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE)));

    public static DeferredHolder<Block, Block> BLACKSTONE_LEDGE = Registration.BLOCKS.register("blackstone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE)));
    public static DeferredHolder<Block, Block> POLISHED_BLACKSTONE_LEDGE = Registration.BLOCKS.register("polished_blackstone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE)));
    public static DeferredHolder<Block, Block> POLISHED_BLACKSTONE_BRICKS_LEDGE = Registration.BLOCKS.register("polished_blackstone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static DeferredHolder<Block, Block> DEEPSLATE_LEDGE = Registration.BLOCKS.register("deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));
    public static DeferredHolder<Block, Block> DEEPSLATE_BRICKS_LEDGE = Registration.BLOCKS.register("deepslate_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));
    public static DeferredHolder<Block, Block> COBBLED_DEEPSLATE_LEDGE = Registration.BLOCKS.register("cobbled_deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));
    public static DeferredHolder<Block, Block> POLISHED_DEEPSLATE_LEDGE = Registration.BLOCKS.register("polished_deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));
    public static DeferredHolder<Block, Block> DEEPSLATE_TILES_LEDGE = Registration.BLOCKS.register("deepslate_tiles_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)));

    public static void register() {}
}
