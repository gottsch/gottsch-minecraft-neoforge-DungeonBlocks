/*
 * This file is part of  Treasure2.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput output, String locale) {
        super(output, DungeonBlocks.MOD_ID, locale);
    }
    
    /** Blocks whose display name is set explicitly below, and so must not be auto-named. */
    private static final Set<DeferredHolder<Block, Block>> NAMED_BY_HAND = Set.of(
            ModBlocks.ROOTS,
            ModBlocks.ROOTS_BODY,
            ModBlocks.ORC_BANNER,
            ModBlocks.TATTERED_ORC_BANNER,
            ModBlocks.BLOODSTAINED_ORC_BANNER);

    @Override
    protected void addTranslations() {
    	// tabs
        add("itemGroup." + DungeonBlocks.MOD_ID, "DungeonBlocks");
        add("itemGroup." + DungeonBlocks.MOD_ID + ".entities", "DungeonBlocks Entities");

        ModBlocks.MAP.forEach((k, v) -> {
            // these are given custom display names below. LanguageProvider.add() throws on a
            // duplicate key rather than overwriting, so anything named by hand has to be skipped
            // here or datagen dies outright.
            if (NAMED_BY_HAND.contains(k) || ModBlocks.CHEVALS_DE_FRISE.contains(k)) {
                return;
            }
            String s = k.getId().getPath().replace("_block", "").replace("_", " ").trim();
            s = WordUtils.capitalizeFully(s);
            add(k.get(), s);
        });

        // unmapped resources
        // the orc banners' ids stay short (the naming scheme the rest of the mod uses), but
        // "Orc War Banner" is what they actually are
        add(ModBlocks.ORC_BANNER.get(), "Orc War Banner");
        add(ModBlocks.TATTERED_ORC_BANNER.get(), "Tattered Orc War Banner");
        add(ModBlocks.BLOODSTAINED_ORC_BANNER.get(), "Bloodstained Orc War Banner");

        // the auto-name would be "Oak Cheval De Frise"; the word is hyphenated
        ModBlocks.CHEVALS_DE_FRISE.forEach(b -> add(b.get(), WordUtils.capitalizeFully(
                DataGenMaps.woodOf(b.getId().getPath(), "cheval_de_frise").replace("_", " ")) + " Cheval-de-Frise"));

        // death messages for the spikes damage type (data/dungeonblocks/damage_type/spikes.json)
        add("death.attack." + DungeonBlocks.MOD_ID + ".spikes", "%1$s was impaled on spikes");
        add("death.attack." + DungeonBlocks.MOD_ID + ".spikes.player", "%1$s was impaled on spikes whilst fighting %2$s");

        add(ModBlocks.MOLD.get(), "Mold");
        add(ModBlocks.LICHEN.get(), "Lichen");
        add(ModItems.SKELETON.get(), "Skeleton");

        add(ModBlocks.ROOTS.get(), "Roots");
        add(ModBlocks.ROOTS_BODY.get(), "Roots");

        // every clay pot shape shares one display name - the shapes are a visual variation of the
        // same prop, not three things a player needs to tell apart by name.
        ModItems.TOMES.forEach((variant, item) -> add(item.get(), Arrays.stream(variant.id().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "))));
        add("entity." + DungeonBlocks.MOD_ID + ".tome", "Tome");
        ModItems.SCROLLS.forEach((variant, item) -> add(item.get(), Arrays.stream(variant.id().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "))));
        add("entity." + DungeonBlocks.MOD_ID + ".scroll", "Scroll");

        add(ModItems.POT.get(), "Terracotta Pot");
        add(ModItems.SQUAT_CLAY_POT.get(), "Terracotta Pot");
        add(ModItems.THIN_CLAY_POT.get(), "Terracotta Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".pot", "Terracotta Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".squat_clay_pot", "Terracotta Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".thin_clay_pot", "Terracotta Pot");
        add(ModItems.STONE_POT.get(), "Stone Pot");
        add(ModItems.SQUAT_STONE_POT.get(), "Stone Pot");
        add(ModItems.THIN_STONE_POT.get(), "Stone Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".stone_pot", "Stone Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".squat_stone_pot", "Stone Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".thin_stone_pot", "Stone Pot");

        add(ModItems.RED_POT.get(), "Red Pot");
        add(ModItems.SQUAT_RED_POT.get(), "Red Pot");
        add(ModItems.THIN_RED_POT.get(), "Red Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".red_pot", "Red Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".squat_red_pot", "Red Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".thin_red_pot", "Red Pot");

        add(ModItems.BLUE_POT.get(), "Blue Pot");
        add(ModItems.SQUAT_BLUE_POT.get(), "Blue Pot");
        add(ModItems.THIN_BLUE_POT.get(), "Blue Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".blue_pot", "Blue Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".squat_blue_pot", "Blue Pot");
        add("entity." + DungeonBlocks.MOD_ID + ".thin_blue_pot", "Blue Pot");

        // a potion prop carries no effect of its own - what it does is set per placed instance -
        // so the name describes the bottle, not an effect
        add(ModItems.BIG_RED_POTION.get(), "Big Red Potion");
        add("entity." + DungeonBlocks.MOD_ID + ".big_red_potion", "Big Red Potion");
        add(ModItems.RED_FLASK.get(), "Red Flask");
        add("entity." + DungeonBlocks.MOD_ID + ".red_flask", "Red Flask");

        add(ModItems.BIG_YELLOW_POTION.get(), "Big Yellow Potion");
        add("entity." + DungeonBlocks.MOD_ID + ".big_yellow_potion", "Big Yellow Potion");
        add(ModItems.YELLOW_FLASK.get(), "Yellow Flask");
        add("entity." + DungeonBlocks.MOD_ID + ".yellow_flask", "Yellow Flask");

        add(ModItems.BIG_BLUE_POTION.get(), "Big Blue Potion");
        add("entity." + DungeonBlocks.MOD_ID + ".big_blue_potion", "Big Blue Potion");
        add(ModItems.BLUE_FLASK.get(), "Blue Flask");
        add("entity." + DungeonBlocks.MOD_ID + ".blue_flask", "Blue Flask");

        add(ModItems.BIG_GREEN_POTION.get(), "Big Green Potion");
        add("entity." + DungeonBlocks.MOD_ID + ".big_green_potion", "Big Green Potion");
        add(ModItems.GREEN_FLASK.get(), "Green Flask");
        add("entity." + DungeonBlocks.MOD_ID + ".green_flask", "Green Flask");

        add("entity." + DungeonBlocks.MOD_ID + ".pot_shard", "Pot Shard");

    }
}
