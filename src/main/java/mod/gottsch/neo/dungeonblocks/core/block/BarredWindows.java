package mod.gottsch.neo.dungeonblocks.core.block;

import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * @author Mark Gottschling on Nov 11, 2023
 */
public class BarredWindows {
    // barred windows and facades
    public static DeferredBlock<Block> STONE_BARRED_WINDOW = Registration.BLOCKS.register("stone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static DeferredBlock<Block> SMOOTH_STONE_BARRED_WINDOW = Registration.BLOCKS.register("smooth_stone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_STONE)));
    public static DeferredBlock<Block> COBBLESTONE_BARRED_WINDOW = Registration.BLOCKS.register("cobblestone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static DeferredBlock<Block> MOSSY_COBBLESTONE_BARRED_WINDOW = Registration.BLOCKS.register("mossy_cobblestone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE)));
    public static DeferredBlock<Block> BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("bricks_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static DeferredBlock<Block> STONE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("stone_bricks_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static DeferredBlock<Block> MOSSY_STONE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("mossy_stone_bricks_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static DeferredBlock<Block> CRACKED_STONE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("cracked_stone_bricks_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_STONE_BRICKS)));
    public static DeferredBlock<Block> CHISELED_STONE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("chiseled_stone_bricks_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_STONE_BRICKS)));
    public static DeferredBlock<Block> OBSIDIAN_BARRED_WINDOW = Registration.BLOCKS.register("obsidian_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));

    public static DeferredBlock<Block> SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SANDSTONE)));
    public static DeferredBlock<Block> SMOOTH_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("smooth_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_SANDSTONE)));
    public static DeferredBlock<Block> CHISELED_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("chiseled_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE)));
    public static DeferredBlock<Block> CUT_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("cut_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CUT_SANDSTONE)));
    public static DeferredBlock<Block> RED_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("red_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.RED_SANDSTONE)));
    public static DeferredBlock<Block> SMOOTH_RED_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("smooth_red_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_RED_SANDSTONE)));
    public static DeferredBlock<Block> CHISELED_RED_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("chiseled_red_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_RED_SANDSTONE)));
    public static DeferredBlock<Block> CUT_RED_SANDSTONE_BARRED_WINDOW = Registration.BLOCKS.register("cut_red_sandstone_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CUT_RED_SANDSTONE)));

    public static DeferredBlock<Block> GRANITE_BARRED_WINDOW = Registration.BLOCKS.register("granite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.GRANITE)));
    public static DeferredBlock<Block> ANDESITE_BARRED_WINDOW = Registration.BLOCKS.register("andesite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static DeferredBlock<Block> DIORITE_BARRED_WINDOW = Registration.BLOCKS.register("diorite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static DeferredBlock<Block> POLISHED_GRANITE_BARRED_WINDOW = Registration.BLOCKS.register("polished_granite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE)));
    public static DeferredBlock<Block> POLISHED_ANDESITE_BARRED_WINDOW = Registration.BLOCKS.register("polished_andesite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static DeferredBlock<Block> POLISHED_DIORITE_BARRED_WINDOW = Registration.BLOCKS.register("polished_diorite_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE)));

    public static final DeferredBlock<Block> BLACKSTONE_BARRED_WINDOW = Registration.BLOCKS.register("blackstone_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final DeferredBlock<Block> POLISHED_BLACKSTONE_BARRED_WINDOW = Registration.BLOCKS.register("polished_blackstone_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final DeferredBlock<Block> POLISHED_BLACKSTONE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("polished_blackstone_bricks_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static final DeferredBlock<Block> DEEPSLATE_BARRED_WINDOW = Registration.BLOCKS.register("deepslate_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final DeferredBlock<Block> DEEPSLATE_BRICKS_BARRED_WINDOW = Registration.BLOCKS.register("deepslate_bricks_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final DeferredBlock<Block> COBBLED_DEEPSLATE_BARRED_WINDOW = Registration.BLOCKS.register("cobbled_deepslate_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final DeferredBlock<Block> POLISHED_DEEPSLATE_BARRED_WINDOW = Registration.BLOCKS.register("polished_deepslate_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final DeferredBlock<Block> DEEPSLATE_TILES_BARRED_WINDOW = Registration.BLOCKS.register("deepslate_tiles_barred_window_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));

    public static DeferredBlock<Block> TERRACOTTA_BARRED_WINDOW = Registration.BLOCKS.register("terracotta_barred_window_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));

    // barred window facade
    public static DeferredBlock<Block> STONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("stone_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static DeferredBlock<Block> SMOOTH_STONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("smooth_stone_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_STONE)));
    public static DeferredBlock<Block> COBBLESTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("cobblestone_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static DeferredBlock<Block> MOSSY_COBBLESTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("mossy_cobblestone_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE)));
    public static DeferredBlock<Block> BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("bricks_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static DeferredBlock<Block> STONE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("stone_bricks_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static DeferredBlock<Block> MOSSY_STONE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("mossy_stone_bricks_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static DeferredBlock<Block> CRACKED_STONE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("cracked_stone_bricks_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CRACKED_STONE_BRICKS)));
    public static DeferredBlock<Block> CHISELED_STONE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("chiseled_stone_bricks_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_STONE_BRICKS)));
    public static DeferredBlock<Block> OBSIDIAN_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("obsidian_barred_window_facade_block",
            () -> new BarredWindowFacadeBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));

    public static DeferredBlock<Block> SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SANDSTONE)));
    public static DeferredBlock<Block> SMOOTH_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("smooth_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_SANDSTONE)));
    public static DeferredBlock<Block> CHISELED_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("chiseled_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE)));
    public static DeferredBlock<Block> CUT_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("cut_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CUT_SANDSTONE)));
    public static DeferredBlock<Block> RED_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("red_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.RED_SANDSTONE)));
    public static DeferredBlock<Block> SMOOTH_RED_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("smooth_red_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_RED_SANDSTONE)));
    public static DeferredBlock<Block> CHISELED_RED_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("chiseled_red_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_RED_SANDSTONE)));
    public static DeferredBlock<Block> CUT_RED_SANDSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("cut_red_sandstone_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.CUT_RED_SANDSTONE)));

    public static DeferredBlock<Block> GRANITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("granite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.GRANITE)));
    public static DeferredBlock<Block> ANDESITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("andesite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static DeferredBlock<Block> DIORITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("diorite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static DeferredBlock<Block> POLISHED_GRANITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_granite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE)));
    public static DeferredBlock<Block> POLISHED_ANDESITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_andesite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static DeferredBlock<Block> POLISHED_DIORITE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_diorite_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE)));

    public static final DeferredBlock<Block> BLACKSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("blackstone_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final DeferredBlock<Block> POLISHED_BLACKSTONE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_blackstone_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final DeferredBlock<Block> POLISHED_BLACKSTONE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_blackstone_bricks_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static final DeferredBlock<Block> DEEPSLATE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("deepslate_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final DeferredBlock<Block> DEEPSLATE_BRICKS_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("deepslate_bricks_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final DeferredBlock<Block> COBBLED_DEEPSLATE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("cobbled_deepslate_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final DeferredBlock<Block> POLISHED_DEEPSLATE_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("polished_deepslate_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final DeferredBlock<Block> DEEPSLATE_TILES_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("deepslate_tiles_barred_window_facade_block",
            () -> new FacadeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));

    public static DeferredBlock<Block> TERRACOTTA_BARRED_WINDOW_FACADE = Registration.BLOCKS.register("terracotta_barred_window_facade_block",
            () -> new BarredWindowBlock(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));


    public static void register() {
        // this method exists simply to ensure that the static objects are registered before
        // the DeferredRegistry is called.
    }
}
