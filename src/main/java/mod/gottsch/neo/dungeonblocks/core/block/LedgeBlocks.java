package mod.gottsch.neo.dungeonblocks.core.block;

import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on Jan 12, 2020
 */
public class LedgeBlocks {
    public static RegistryObject<Block> STONE_LEDGE = Registration.BLOCKS.register("stone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> SMOOTH_STONE_LEDGE = Registration.BLOCKS.register("smooth_stone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> COBBLESTONE_LEDGE = Registration.BLOCKS.register("cobblestone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> MOSSY_COBBLESTONE_LEDGE = Registration.BLOCKS.register("mossy_cobblestone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> BRICKS_LEDGE = Registration.BLOCKS.register("bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> STONE_BRICKS_LEDGE = Registration.BLOCKS.register("stone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> MOSSY_STONE_BRICKS_LEDGE = Registration.BLOCKS.register("mossy_stone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static RegistryObject<Block> LIGHT_GRAY_CONCRETE_LEDGE = Registration.BLOCKS.register("light_gray_concrete_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));


    public static RegistryObject<Block> GRANITE_LEDGE = Registration.BLOCKS.register("granite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.GRANITE)));
    public static RegistryObject<Block> ANDESITE_LEDGE = Registration.BLOCKS.register("andesite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));
    public static RegistryObject<Block> DIORITE_LEDGE = Registration.BLOCKS.register("diorite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static RegistryObject<Block> POLISHED_GRANITE_LEDGE = Registration.BLOCKS.register("polished_granite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE)));
    public static RegistryObject<Block> POLISHED_ANDESITE_LEDGE = Registration.BLOCKS.register("polished_andesite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static RegistryObject<Block> POLISHED_DIORITE_LEDGE = Registration.BLOCKS.register("polished_diorite_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE)));

    public static RegistryObject<Block> BLACKSTONE_LEDGE = Registration.BLOCKS.register("blackstone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_LEDGE = Registration.BLOCKS.register("polished_blackstone_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static RegistryObject<Block> POLISHED_BLACKSTONE_BRICKS_LEDGE = Registration.BLOCKS.register("polished_blackstone_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

    public static RegistryObject<Block> DEEPSLATE_LEDGE = Registration.BLOCKS.register("deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_BRICKS_LEDGE = Registration.BLOCKS.register("deepslate_bricks_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> COBBLED_DEEPSLATE_LEDGE = Registration.BLOCKS.register("cobbled_deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> POLISHED_DEEPSLATE_LEDGE = Registration.BLOCKS.register("polished_deepslate_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static RegistryObject<Block> DEEPSLATE_TILES_LEDGE = Registration.BLOCKS.register("deepslate_tiles_ledge_block",
            () -> new LedgeBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));

    public static void register() {}
}
