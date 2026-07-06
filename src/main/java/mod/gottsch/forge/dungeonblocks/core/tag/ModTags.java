package mod.gottsch.forge.dungeonblocks.core.tag;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * @author by Mark Gottschling on 10/16/2025
 */
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> CORBELS = mod(DungeonBlocks.MOD_ID, "corbels");
        public static final TagKey<Block> LEDGES = mod(DungeonBlocks.MOD_ID, "ledges");


        public static TagKey<Block> mod(String domain, String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(domain, path));
        }
    }
}
