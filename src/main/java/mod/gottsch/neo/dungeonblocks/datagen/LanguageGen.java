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
package mod.gottsch.neo.dungeonblocks.datagen;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.dungeonblocks.core.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
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

//        add(ModBlocks.STONE_BARRED_WINDOW.get(), "Stone Barred Window");
//        add(ModBlocks.SMOOTH_STONE_BARRED_WINDOW.get(), "Smooth Stone Barred Window");
//        add(ModBlocks.COBBLESTONE_BARRED_WINDOW.get(), "Cobblestone Barred Window");
//        add(ModBlocks.MOSSY_COBBLESTONE_BARRED_WINDOW.get(), "Mossy Cobblestone Barred Window");
//        add(ModBlocks.BRICKS_BARRED_WINDOW.get(), "Bricks Barred Window");

        ModBlocks.MAP.forEach((k, v) -> {
//            if (k.getId().getPath().contains("barred_window")) {
                String s = k.getId().getPath().replace("_", " ").replace("block", "");
                s = WordUtils.capitalizeFully(s);
                add(k.get(), s);
//            }
        });
    }
}
