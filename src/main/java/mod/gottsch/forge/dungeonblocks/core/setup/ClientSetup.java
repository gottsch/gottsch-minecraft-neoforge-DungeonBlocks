package mod.gottsch.forge.dungeonblocks.core.setup;

import mod.gottsch.forge.dungeonblocks.core.item.PotItem;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.BubblingCauldronBlock;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.blockentity.ModBlockEntityTypes;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.DungeonBannerModel;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.DungeonBannerRenderer;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.PedestalRenderer;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.SarcophagusRenderer;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.SwingingChainRenderer;
import mod.gottsch.forge.dungeonblocks.core.blockentity.client.WeaponRackRenderer;
import mod.gottsch.forge.dungeonblocks.core.entity.ModEntityTypes;
import mod.gottsch.forge.dungeonblocks.core.entity.client.BigRedPotionModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotItemRenderer;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotRenderer;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotShardModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotShardRenderer;
import mod.gottsch.forge.dungeonblocks.core.entity.client.PotVariant;
import mod.gottsch.forge.dungeonblocks.core.entity.client.RedFlaskModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.SquatClayPotModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.ThinClayPotModel;
import mod.gottsch.forge.dungeonblocks.core.entity.TomeVariant;
import mod.gottsch.forge.dungeonblocks.core.entity.client.ScrollModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.ScrollRenderer;
import mod.gottsch.forge.dungeonblocks.core.entity.client.TomeModel;
import mod.gottsch.forge.dungeonblocks.core.entity.client.TomeRenderer;
import mod.gottsch.forge.dungeonblocks.core.state.properties.ChainFixture;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = DungeonBlocks.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    /**
     * The pot shapes, declared once. Layer definitions, the in-world entity renderers and the 3D
     * inventory renderer all read this list, so a new pot shape is one entry here plus its model
     * class — nothing else on the client side needs touching.
     *
     * <p>The float is the total modeled height in blocks (base + neck + lip): 14px, 10px and 12px
     * respectively.
     */
    // heights and widths are the modeled extents in blocks: 14x8, 10x10 and 12x6 pixels.
    // The stone set reuses the terracotta set's geometry outright — a material is purely a
    // reskin, so it is three more entries here against the same three layers, no new model class.
    private static final List<PotVariant> POT_VARIANTS = List.of(
            new PotVariant(ModEntityTypes.POT, PotModel.LAYER_LOCATION, PotModel::new,
                    PotVariant.entityTexture("pot"), 0.875F, 0.5F),
            new PotVariant(ModEntityTypes.SQUAT_CLAY_POT, SquatClayPotModel.LAYER_LOCATION, SquatClayPotModel::new,
                    PotVariant.entityTexture("squat_clay_pot"), 0.625F, 0.625F),
            new PotVariant(ModEntityTypes.THIN_CLAY_POT, ThinClayPotModel.LAYER_LOCATION, ThinClayPotModel::new,
                    PotVariant.entityTexture("thin_clay_pot"), 0.75F, 0.375F),
            new PotVariant(ModEntityTypes.STONE_POT, PotModel.LAYER_LOCATION, PotModel::new,
                    PotVariant.entityTexture("stone_pot"), 0.875F, 0.5F),
            new PotVariant(ModEntityTypes.SQUAT_STONE_POT, SquatClayPotModel.LAYER_LOCATION, SquatClayPotModel::new,
                    PotVariant.entityTexture("squat_stone_pot"), 0.625F, 0.625F),
            new PotVariant(ModEntityTypes.THIN_STONE_POT, ThinClayPotModel.LAYER_LOCATION, ThinClayPotModel::new,
                    PotVariant.entityTexture("thin_stone_pot"), 0.75F, 0.375F),
            new PotVariant(ModEntityTypes.RED_POT, PotModel.LAYER_LOCATION, PotModel::new,
                    PotVariant.entityTexture("red_pot"), 0.875F, 0.5F),
            new PotVariant(ModEntityTypes.SQUAT_RED_POT, SquatClayPotModel.LAYER_LOCATION, SquatClayPotModel::new,
                    PotVariant.entityTexture("squat_red_pot"), 0.625F, 0.625F),
            new PotVariant(ModEntityTypes.THIN_RED_POT, ThinClayPotModel.LAYER_LOCATION, ThinClayPotModel::new,
                    PotVariant.entityTexture("thin_red_pot"), 0.75F, 0.375F),
            new PotVariant(ModEntityTypes.BLUE_POT, PotModel.LAYER_LOCATION, PotModel::new,
                    PotVariant.entityTexture("blue_pot"), 0.875F, 0.5F),
            new PotVariant(ModEntityTypes.SQUAT_BLUE_POT, SquatClayPotModel.LAYER_LOCATION, SquatClayPotModel::new,
                    PotVariant.entityTexture("squat_blue_pot"), 0.625F, 0.625F),
            new PotVariant(ModEntityTypes.THIN_BLUE_POT, ThinClayPotModel.LAYER_LOCATION, ThinClayPotModel::new,
                    PotVariant.entityTexture("thin_blue_pot"), 0.75F, 0.375F),
            // modeled at tall-pot size and halved at render time rather than re-modeled
            new PotVariant(ModEntityTypes.BIG_RED_POTION, BigRedPotionModel.LAYER_LOCATION, BigRedPotionModel::new,
                    PotVariant.entityTexture("big_red_potion"), 0.875F, 0.5F, 0.5F),
            new PotVariant(ModEntityTypes.RED_FLASK, RedFlaskModel.LAYER_LOCATION, RedFlaskModel::new,
                    PotVariant.entityTexture("red_flask"), 0.8125F, 0.4375F, 0.5F),
            // same geometry as the red potion set, reskinned - see ModEntityTypes for the colour list
            new PotVariant(ModEntityTypes.BIG_YELLOW_POTION, BigRedPotionModel.LAYER_LOCATION, BigRedPotionModel::new,
                    PotVariant.entityTexture("big_yellow_potion"), 0.875F, 0.5F, 0.5F),
            new PotVariant(ModEntityTypes.YELLOW_FLASK, RedFlaskModel.LAYER_LOCATION, RedFlaskModel::new,
                    PotVariant.entityTexture("yellow_flask"), 0.8125F, 0.4375F, 0.5F),
            new PotVariant(ModEntityTypes.BIG_BLUE_POTION, BigRedPotionModel.LAYER_LOCATION, BigRedPotionModel::new,
                    PotVariant.entityTexture("big_blue_potion"), 0.875F, 0.5F, 0.5F),
            new PotVariant(ModEntityTypes.BLUE_FLASK, RedFlaskModel.LAYER_LOCATION, RedFlaskModel::new,
                    PotVariant.entityTexture("blue_flask"), 0.8125F, 0.4375F, 0.5F),
            new PotVariant(ModEntityTypes.BIG_GREEN_POTION, BigRedPotionModel.LAYER_LOCATION, BigRedPotionModel::new,
                    PotVariant.entityTexture("big_green_potion"), 0.875F, 0.5F, 0.5F),
            new PotVariant(ModEntityTypes.GREEN_FLASK, RedFlaskModel.LAYER_LOCATION, RedFlaskModel::new,
                    PotVariant.entityTexture("green_flask"), 0.8125F, 0.4375F, 0.5F));

    /**
     * Register the {@link IBlockColor} handlers.
     *
     * @param event The event
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, reader, pos, color) -> {
                    return (reader != null && pos != null) ? BiomeColors.getAverageWaterColor(reader, pos)  : 0;
                },
                ModBlocks.WEATHERED_COPPER_SEWER.get(),
                ModBlocks.TERRACOTTA_SEWER.get());
        // the bubbling cauldron's brew (tint 0) takes its colour from the blockstate
        event.register(
                (state, reader, pos, tint) -> tint == 0 ? state.getValue(BubblingCauldronBlock.COLOR).getColor() : -1,
                ModBlocks.BUBBLING_CAULDRON.get());
    }

    // No item colour handler for the bubbling cauldron: its item is a flat sprite with the green
    // brew painted in (tools/gen_bubbling_cauldron_item.py), and a tint on layer0 would dye it all.

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (DungeonBannerModel.Shape shape : DungeonBannerModel.Shape.values()) {
            event.registerLayerDefinition(shape.layer, () -> DungeonBannerModel.createBodyLayer(shape));
        }
        event.registerLayerDefinition(PotModel.LAYER_LOCATION, PotModel::createBodyLayer);
        event.registerLayerDefinition(SquatClayPotModel.LAYER_LOCATION, SquatClayPotModel::createBodyLayer);
        event.registerLayerDefinition(ThinClayPotModel.LAYER_LOCATION, ThinClayPotModel::createBodyLayer);
        event.registerLayerDefinition(BigRedPotionModel.LAYER_LOCATION, BigRedPotionModel::createBodyLayer);
        event.registerLayerDefinition(RedFlaskModel.LAYER_LOCATION, RedFlaskModel::createBodyLayer);
        event.registerLayerDefinition(ScrollModel.LAYER_LOCATION, ScrollModel::createBodyLayer);
        for (TomeVariant.Shape shape : TomeVariant.Shape.values()) {
            event.registerLayerDefinition(TomeModel.layer(shape), () -> TomeModel.createBodyLayer(shape));
        }
        // hand the shape table to the inventory renderer now that the layers exist
        PotItemRenderer.setVariants(POT_VARIANTS);
        for (int i = 0; i < PotShardModel.LAYERS.length; i++) {
            int variant = i;
            event.registerLayerDefinition(PotShardModel.LAYERS[variant], () -> PotShardModel.createBodyLayer(variant));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // PotRenderer is shape-agnostic: each variant supplies its model, texture and tumble pivot.
        POT_VARIANTS.forEach(variant ->
                event.registerEntityRenderer(variant.entityType().get(),
                        context -> new PotRenderer(context,
                                variant.modelFactory().apply(context.bakeLayer(variant.layer())),
                                variant.texture(), variant.tumblePivot(), variant.scale())));
        event.registerEntityRenderer(ModEntityTypes.POT_SHARD.get(), PotShardRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.TOME.get(), TomeRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SCROLL.get(), ScrollRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SWINGING_CHAIN.get(), SwingingChainRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.DUNGEON_BANNER.get(), DungeonBannerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SARCOPHAGUS.get(), SarcophagusRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.WEAPON_RACK.get(), WeaponRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PEDESTAL.get(), PedestalRenderer::new);
    }

    /**
     * Models the renderers look up by their own location, which have to be registered to be baked
     * under it: the sarcophagus and coffin lid-only models, which no blockstate references and
     * SarcophagusRenderer always draws, and the chain fixtures' models, which SwingingChainRenderer
     * draws in a chain's bottom link. The fixture blocks' blockstates use those models too, but a
     * blockstate bakes its models under the block's own locations, not these.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        List<DeferredHolder<Block, Block>> tombs = new ArrayList<>(List.of(ModBlocks.STONE_SARCOPHAGUS, ModBlocks.DEEPSLATE_SARCOPHAGUS));
        tombs.addAll(ModBlocks.COFFINS);
        for (DeferredHolder<Block, Block> block : tombs) {
            for (BedPart part : BedPart.values()) {
                event.register(SarcophagusRenderer.lidModel(block.getId(), part));
            }
        }
        for (ChainFixture fixture : ChainFixture.values()) {
            for (boolean lit : new boolean[] {false, true}) {
                String model = fixture.model(lit);
                if (model != null) {
                    event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/" + model)));
                }
            }
        }
    }

    /**
     * The pot items render as 3D geometry rather than a flat sprite (see {@link PotItemRenderer}).
     * Forge 1.20.1 did this in Item#initializeClient; NeoForge 21.1 registers it here.
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        Item[] pots = Registration.ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(item -> item instanceof PotItem)
                .toArray(Item[]::new);
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return PotItemRenderer.getInstance();
            }
        }, pots);
    }
}
