package mod.gottsch.neo.dungeonblocks.datagen;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.dungeonblocks.core.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DungeonBlocks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//        this.tag(ModTags.Blocks.METAL_DETECTOR_VALUABLES)
//                .add(ModBlocks.SAPPHIRE_ORE.get()).addTag(Tags.Blocks.ORES);

        DataGenMaps maps = new DataGenMaps();

        Registration.BLOCKS.getEntries().stream()
                .filter(b -> {
                    for(String n : maps.stone_blocks) {
                        if (b.getId().getPath().contains(n)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(b -> {
                    String name = b.getId().getPath();

                    if (maps.wood_names.contains(name)) {
//                        this.tag(BlockTags.MINEABLE_WITH_AXE)
//                                .add(b.get());
//                        this.tag(BlockTags.NEEDS_IRON_TOOL)
//                                .add(b.get());
                    } else {
                        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
                        if (name.contains("obsidian")) {
                            this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(b.get());
                        } else {
                            this.tag(BlockTags.NEEDS_STONE_TOOL).add(b.get());
                        }
                    }
                });
    }
}