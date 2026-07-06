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
import org.apache.commons.lang3.text.WordUtils;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput output, String locale) {
        super(output, DungeonBlocks.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
    	// tabs
        add("itemGroup." + DungeonBlocks.MOD_ID, "DungeonBlocks");

        ModBlocks.MAP.forEach((k, v) -> {
            // these are given custom display names below
            if (k == ModBlocks.ROOTS || k == ModBlocks.ROOTS_BODY) {
                return;
            }
            String s = k.getId().getPath().replace("_block", "").replace("_", " ").trim();
            s = WordUtils.capitalizeFully(s);
            add(k.get(), s);
        });

        // unmapped resources
        add(ModBlocks.MOLD.get(), "Mold");
        add(ModBlocks.LICHEN.get(), "Lichen");
        add(ModItems.SKELETON.get(), "Skeleton");

        add(ModBlocks.ROOTS.get(), "Roots");
        add(ModBlocks.ROOTS_BODY.get(), "Roots");

    }
}
