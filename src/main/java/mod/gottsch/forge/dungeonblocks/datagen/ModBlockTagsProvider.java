package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.CorbelBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.LedgeBlocks;
import mod.gottsch.forge.dungeonblocks.core.tag.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * 
 * @author Mark Gottschling on Oct 16, 2025
 *
 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
                                ExistingFileHelper existingFileHelper) {
    	super(output, lookupProvider, DungeonBlocks.MOD_ID, existingFileHelper);
	}

	@Override
    protected void addTags(Provider provider) {
    	// corbels
    	tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.ACACIA_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.ANDESITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.COBBLESTONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.BIRCH_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.BLACKSTONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.CHERRY_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.COBBLED_DEEPSLATE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.DARK_OAK_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.DEEPSLATE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.DEEPSLATE_BRICKS_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.DEEPSLATE_TILES_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.DIORITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.GRANITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.JUNGLE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.MANGROVE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.MOSSY_COBBLESTONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.MOSSY_STONE_BRICKS_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.OAK_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_ANDESITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_BLACKSTONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_BLACKSTONE_BRICKS_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_DEEPSLATE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_DIORITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.POLISHED_GRANITE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.SMOOTH_STONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.SPRUCE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STONE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STONE_BRICKS_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_ACACIA_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_BIRCH_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_CHERRY_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_DARK_OAK_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_JUNGLE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_MANGROVE_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_OAK_CORBEL.get());
		tag(ModTags.Blocks.CORBELS).add(CorbelBlocks.STRIPPED_SPRUCE_CORBEL.get());

		// ledges
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.ANDESITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.BLACKSTONE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.BRICKS_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.COBBLESTONE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.COBBLED_DEEPSLATE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.DEEPSLATE_BRICKS_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.DEEPSLATE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.DIORITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.GRANITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.LIGHT_GRAY_CONCRETE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.MOSSY_COBBLESTONE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.MOSSY_STONE_BRICKS_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_ANDESITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_BLACKSTONE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_BLACKSTONE_BRICKS_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_DEEPSLATE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_DIORITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.POLISHED_GRANITE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.SMOOTH_STONE_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.STONE_BRICKS_LEDGE.get());
		tag(ModTags.Blocks.LEDGES).add(LedgeBlocks.STONE_LEDGE.get());
	}

}
