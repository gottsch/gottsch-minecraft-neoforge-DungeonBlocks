package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.DecorType;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModMaterials;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import mod.gottsch.forge.dungeonblocks.core.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DungeonBlocks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // corbels and ledges, from the decorative block table
        ModBlocks.DECOR.stream().filter(d -> d.type() == DecorType.CORBEL)
                .forEach(d -> tag(ModTags.Blocks.CORBELS).add(d.block().get()));
        ModBlocks.DECOR.stream().filter(d -> d.type() == DecorType.LEDGE)
                .forEach(d -> tag(ModTags.Blocks.LEDGES).add(d.block().get()));
        // The mod's stone, in one pass in registry order (which is the tag files' entry order):
        // the decorative blocks, the stone blocks registered with ModBlocks.stone(...), and the
        // stone pieces kept in lists of their own. Tool tiers are vanilla's: stone, bricks,
        // sandstone, deepslate and the rest take any pickaxe, so they get no tier tag; obsidian
        // needs diamond. A decorative block made of wood (the wood corbels) is an axe's.
        // (Comments below that say a block "matches nothing in stone_blocks" date from when this
        // pass matched id substrings instead.)
        Map<Block, ModMaterials.Material> decor = ModBlocks.DECOR.stream()
                .collect(Collectors.toMap(d -> d.block().get(), DecorType.DecorBlock::material));
        Set<Block> stone = Stream.of(ModBlocks.STONE_BLOCKS, ModBlocks.CAPSTONES.keySet(), ModBlocks.HIDDEN_DOORS.keySet(),
                        ModBlocks.CATACOMB_NICHES.keySet(), ModBlocks.CRUMBLING_FLOORS.keySet())
                .flatMap(Collection::stream).map(b -> (Block) b.get()).collect(Collectors.toSet());
        Registration.BLOCKS.getEntries().forEach(b -> {
            ModMaterials.Material material = decor.get(b.get());
            if (material != null && ModMaterials.WOOD.contains(material)) {
                this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get());
            } else if (material != null || stone.contains(b.get())) {
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
                if (material != null && material.base() == Blocks.OBSIDIAN) {
                    this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(b.get());
                }
            }
        });
        // pickaxe, no tier, as vanilla's chain
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SWINGING_CHAIN.get());

        // The skeleton matches nothing in stone_blocks, so the loop above skips it - but it still
        // inherits requiresCorrectToolForDrops from Properties.ofFullCopy(STONE), and a block that
        // requires the correct tool while belonging to no tool tag can never be mined for drops by
        // anything. Tagged explicitly rather than by adding "skeleton" to stone_blocks, which would
        // also pull it into the stone family's model and recipe generation.
        // No tier tag: vanilla bone block is mineable with any pickaxe.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SKELETON.get());

        // Angle cobwebs are decorative webbing, so a sword should cut them the way it cuts a vanilla
        // cobweb. Vanilla gets there by hardcoding Blocks.COBWEB in SwordItem, which a modded block
        // cannot reach; SWORD_EFFICIENT is the tag equivalent and gives swords 1.5F. Combined with
        // the 0.4 hardness set in ModBlocks that lands on 8 ticks, the same as vanilla cobweb.
        // Note vanilla COBWEB itself is NOT in this tag - it does not need to be, given the hardcoding.
        this.tag(BlockTags.SWORD_EFFICIENT).add(ModBlocks.ANGLE_COBWEB_1.get(), ModBlocks.ANGLE_COBWEB_2.get());

        // mineable/needs-tool come from the stone_blocks sweep ("square"); the slab tag does not
        this.tag(BlockTags.SLABS).add(ModBlocks.SQUARE_STONE_BRICK_SLAB.get(), ModBlocks.MOSSY_SQUARE_STONE_BRICK_SLAB.get(),
                ModBlocks.SQUARE_DEEPSLATE_BRICK_SLAB.get(), ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_SLAB.get());

        // same reason as the skeleton above: "rubble" matches nothing in stone_blocks, so
        // the loop skips it, but it copies requiresCorrectToolForDrops from cobblestone.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.RUBBLE.get(), ModBlocks.MOSSY_RUBBLE.get());

        // Same reason again: of the three mossy deepslate full blocks, only "mossy_deepslate_bricks"
        // matches stone_blocks (on "brick"). "tiles" and "cobbled" match nothing there, and both
        // copy requiresCorrectToolForDrops from their base stone. Their eleven decorative types are
        // fine - those match on "facade", "pillar", "sill" and so on.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MOSSY_DEEPSLATE_TILES.get(), ModBlocks.MOSSY_COBBLED_DEEPSLATE.get());

        // "mossy_tuff" matches nothing in stone_blocks either, and copies requiresCorrectToolForDrops
        // from vanilla tuff. Any pickaxe, as vanilla tuff.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MOSSY_TUFF.get());

        // Every copper block belonged to no tool tag. Most of them - grates, heavy grates, valve
        // wheels, trapdoors, heavy trapdoors, plate brackets - copy requiresCorrectToolForDrops from
        // COPPER_GRATE, so they could never be mined for a drop. Swept by id rather than listed, so a
        // new copper block is covered automatically; the tier tag follows the block's own property
        // rather than being assumed, so the doors and sewer block (which drop to anything) only
        // gain pickaxe speed. Stone tier matches vanilla copper.
        Registration.BLOCKS.getEntries().stream()
                .filter(b -> b.getId().getPath().contains("copper"))
                .forEach(b -> {
                    this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
                    if (b.get().defaultBlockState().requiresCorrectToolForDrops()) {
                        this.tag(BlockTags.NEEDS_STONE_TOOL).add(b.get());
                    }
                });

        // "basalt" matches nothing in stone_blocks, and the block copies requiresCorrectToolForDrops
        // from vanilla polished basalt. Any pickaxe, as vanilla polished basalt.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MOSSY_POLISHED_BASALT.get());

        // The iron bars door matches nothing in stone_blocks ("barred_window" is not "bars") and
        // copies requiresCorrectToolForDrops from the iron door. Pickaxe with no tier tag, exactly
        // as vanilla tags the iron door. DOORS as vanilla does too - not WOODEN_DOORS, which is
        // what villagers path through.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.IRON_BARS_DOOR.get());
        this.tag(BlockTags.DOORS).add(ModBlocks.IRON_BARS_DOOR.get());
        // the dark iron bars and their doors: the same, and pickaxe-only like vanilla iron bars
        ModBlocks.DARK_IRON_BARS.forEach((age, b) -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get()));
        ModBlocks.DARK_IRON_BARS_DOORS.forEach((age, b) -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get()));
        // a ladder climbs only by this tag, not by being a LadderBlock
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.DARK_IRON_LADDER.get());
        this.tag(BlockTags.CLIMBABLE).add(ModBlocks.DARK_IRON_LADDER.get());
        ModBlocks.DARK_IRON_BARS_DOORS.forEach((age, b) -> this.tag(BlockTags.DOORS).add(b.get()));

        // Sharpened logs are wood: axe, no tier, like vanilla logs. Their ids match nothing in
        // stone_blocks, and must stay out of that sweep, which would tag them for a pickaxe.
        ModBlocks.SHARPENED_LOGS.forEach(b -> this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get()));
        ModBlocks.CHEVALS_DE_FRISE.forEach(b -> this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get()));
        ModBlocks.WALKWAY_BRACKETS.forEach(b -> this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get()));


        // Spikes require the correct tool (as iron bars do) and match nothing in stone_blocks.
        // Pickaxe, no tier, like vanilla iron bars.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.IRON_SPIKES.get(), ModBlocks.DARK_IRON_SPIKES.get());
        // Dungeon furniture matches nothing in stone_blocks either. The sarcophagi copy stone bricks
        // (any pickaxe, like the mod's other stone); the iron pieces are pickaxe-only like iron bars.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.STONE_SARCOPHAGUS.get(), ModBlocks.DEEPSLATE_SARCOPHAGUS.get(),
                ModBlocks.IRON_MAIDEN.get(), ModBlocks.GIBBET.get());

        // The pillory and the rack are dark oak, and match nothing in stone_blocks: an axe, no tier
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.PILLORY.get(), ModBlocks.OCCUPIED_PILLORY.get(),
                ModBlocks.TORTURE_RACK.get(), ModBlocks.OCCUPIED_TORTURE_RACK.get());

        // Same for the portcullis and its winch
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PORTCULLIS.get(), ModBlocks.PORTCULLIS_WINCH.get());

        // The racks match nothing in stone_blocks either. Neither requires a tool to drop; these
        // only make the right one faster - an axe for the firewood, a pickaxe for the iron rack.
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.FIREWOOD_RACK.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.WEAPON_RACK.get());

        // Coffins and the skull pike are wood: axe, no tier, and neither needs a tool to drop.
        ModBlocks.COFFINS.forEach(b -> this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get()));
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.SKULL_PIKE.get(), ModBlocks.ZOMBIE_HEAD_PIKE.get(),
                ModBlocks.BLOODY_STEVE_HEAD_PIKE.get());
        // The gargoyles copy stone, requiresCorrectToolForDrops included, and match nothing in
        // stone_blocks: any pickaxe, as vanilla stone.
        for (DeferredHolder<Block, Block> b : List.of(ModBlocks.PERCHED_GARGOYLE, ModBlocks.GARGOYLE_BUST, ModBlocks.GARGOYLE_STATUE)) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
        }
        // The lever sconce is the torch sconce's twin: pickaxe, no tier. The hidden doors are doors
        // (DOORS, as vanilla tags the iron door); the stone pass above makes them pickaxe. The
        // pedestal copies polished andesite, requiresCorrectToolForDrops included: any pickaxe.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.LEVER_SCONCE.get(), ModBlocks.PEDESTAL.get());
        ModBlocks.HIDDEN_DOORS.keySet().forEach(b -> this.tag(BlockTags.DOORS).add(b.get()));
        // Tapestries are cloth, and drop to anything: an axe only takes them down faster, as vanilla's
        // banners are tagged.
        ModBlocks.TAPESTRIES.forEach(b -> this.tag(BlockTags.MINEABLE_WITH_AXE).add(b.get()));
        // The rubble scatter is loose stone: pickaxe, no tier, and it drops to anything.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.RUBBLE_SCATTER.get());
        // The bone pile, the chandelier and the chain fixtures drop to anything; a pickaxe only
        // takes them down faster.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.BONE_PILE.get(), ModBlocks.CHANDELIER.get(),
                ModBlocks.MANACLES.get(), ModBlocks.MEAT_HOOK.get(), ModBlocks.CENSER.get());
        // "bubbling_cauldron" matches nothing in stone_blocks, and it copies vanilla cauldron's
        // requiresCorrectToolForDrops: untagged it could never drop. Pickaxe, no tier, as vanilla's.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.BUBBLING_CAULDRON.get());

        // "brazier" and "lantern" match nothing in stone_blocks, so neither block was ever tagged.
        // The brazier drops to anything, but a pickaxe mined it no faster than a bare hand. The
        // dungeon lantern copies vanilla lantern's requiresCorrectToolForDrops, so with no tool tag
        // it could never be mined for a drop at all. Pickaxe, no tier: vanilla's lantern exactly.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.BRAZIER.get(), ModBlocks.DUNGEON_LANTERN.get());
        // The sconces and the wall ring had the brazier's gap: in no tool tag, so no faster to mine
        // with a pickaxe than by hand. None requires a tool to drop, so no tier.
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CANDLE_SCONCE.get(), ModBlocks.TORCH_SCONCE.get(),
                ModBlocks.WALL_RING.get());

        // Dark iron grates and heavy trapdoors, plain and rusted, belonged to no tool tag, so a
        // pickaxe mined them no faster than a bare hand. Pickaxe only, no tier tag: they do not
        // require the correct tool for drops and never have, so they still drop to anything.
        ModBlocks.DARK_IRON_GRATES.forEach((age, b) -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get()));
        ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.forEach((age, b) -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get()));

        // must stay last: it checks the tags built above
        checkCorrectToolBlocksAreMineable();
    }

    /**
     * Fails datagen if any mod block requires the correct tool for drops but is in no mineable/* tag.
     * Such a block can never be mined for a drop by anything, and the substring sweeps above have
     * shipped that bug repeatedly (the skeleton, rubble, copper, the lantern...). Only tags built
     * by this provider are seen, which covers everything: no mod block is put in a mineable tag
     * anywhere else.
     */
    private void checkCorrectToolBlocksAreMineable() {
        Set<ResourceLocation> mineable = new HashSet<>();
        for (TagKey<Block> tag : List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE,
                BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)) {
            collectElements(tag.location(), mineable, new HashSet<>());
        }

        List<String> untagged = Registration.BLOCKS.getEntries().stream()
                .filter(b -> b.get().defaultBlockState().requiresCorrectToolForDrops())
                .map(DeferredHolder::getId)
                .filter(id -> !mineable.contains(id))
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
        if (!untagged.isEmpty()) {
            throw new IllegalStateException(untagged.size() + " block(s) require the correct tool for drops"
                    + " but are in no mineable/* tag, so they can never drop: " + String.join(", ", untagged));
        }
    }

    /** Adds the element ids of a tag built here to {@code out}, following nested tag references. */
    private void collectElements(ResourceLocation tagId, Set<ResourceLocation> out, Set<ResourceLocation> visited) {
        if (!visited.add(tagId) || !builders.containsKey(tagId)) {
            return;
        }
        for (TagEntry entry : builders.get(tagId).build()) {
            if (entry.isTag()) {
                collectElements(entry.getId(), out, visited);
            } else {
                out.add(entry.getId());
            }
        }
    }
}