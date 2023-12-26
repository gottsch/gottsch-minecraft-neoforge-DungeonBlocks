package mod.gottsch.neo.dungeonblocks.datagen;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
@Mod.EventBusSubscriber(modid = DungeonBlocks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(packOutput));
            generator.addProvider(true, ModLootTableProvider.create(packOutput));
            ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
            generator.addProvider(true, new ItemModelsProvider(packOutput, event.getExistingFileHelper()));
            generator.addProvider(true, new LanguageGen(packOutput, "en_us"));
        }
//        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));


//        generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));
    }
}