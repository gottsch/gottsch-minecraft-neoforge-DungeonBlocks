/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.dungeonblocks.datagen;

import mod.gottsch.neo.dungeonblocks.DungeonBlocks;
import mod.gottsch.neo.dungeonblocks.core.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 
 * @author Mark Gottschling on Oct 26, 2023
 *
 */
public class ItemModelsProvider extends ItemModelProvider {

	public ItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, DungeonBlocks.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		/*
		 * block items
		 */
		ModBlocks.MAP.forEach((k, v) -> {
			if (k.getId().getPath().contains("barred_window") ||
					k.getId().getPath().contains("greek_block") || k.getId().getPath().contains("keystone") ||
			k.getId().getPath().contains("ledge") || k.getId().getPath().contains("corbel")) {
				blockItemParent(v);
			}
		});
		blockItemParent(ModBlocks.MAP.get(ModBlocks.TORCH_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.CANDLE_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.BRAZIER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_GRATE));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_GRATE_TRAPDOOR), modLoc("block/dark_iron_grate_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_GRATE_TRAPDOOR), modLoc("block/weathered_copper_grate_trapdoor_bottom"));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WALL_RING));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.PLATE_BRACKET_BLOCK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.TERRACOTTA_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DIRTY_HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DUNGEON_LANTERN));

		basicItem(ModBlocks.MAP.get(ModBlocks.CRIMSON_DUNGEON_DOOR), mcLoc("item/crimson_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.DARK_OAK_DUNGEON_DOOR), mcLoc("item/dark_oak_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.MANGROVE_DUNGEON_DOOR), mcLoc("item/mangrove_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.SPRUCE_DUNGEON_DOOR), mcLoc("item/spruce_door"));

	}

	public ItemModelBuilder basicItem(DeferredHolder<Item, ? extends Item> item, ResourceLocation texture) {
		return getBuilder(item.getId().toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", texture);
	}

	public ItemModelBuilder blockItemParent(DeferredHolder<Item, ? extends Item> item) {
		return withExistingParent(item.getId().getPath(), modLoc("block/" + item.getId().getPath()));
	}

	public ItemModelBuilder withExistingParent(DeferredHolder<Item, ? extends Item> item, ResourceLocation parent) {
		return withExistingParent(item.getId().getPath(), parent);
	}
}
