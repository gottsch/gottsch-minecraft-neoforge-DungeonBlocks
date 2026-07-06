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
package mod.gottsch.forge.dungeonblocks.datagen;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
			if (k.getId().getPath().contains("barred_window")
					|| k.getId().getPath().contains("arrow_slit")
					|| k.getId().getPath().contains("greek_block")
						|| k.getId().getPath().contains("ledge")
					|| k.getId().getPath().contains("corbel")
					|| k.getId().getPath().contains("sill")
					|| k.getId().getPath().contains("fluted_block")
					|| k.getId().getPath().contains("fluted_facade_block")
					|| k.getId().getPath().contains("cornice")
					|| k.getId().getPath().contains("facade_block")
					|| k.getId().getPath().contains("crown_molding")
					|| k.getId().getPath().contains("pillar")
					|| k.getId().getPath().contains("quarter")
			) {
				blockItemParent(v);
			}
		});

		blockItemParent(ModBlocks.MAP.get(ModBlocks.TORCH_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.CANDLE_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.BRAZIER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_HEAVY_GRATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_HEAVY_GRATE));

		blockItemParent(ModBlocks.MAP.get(ModBlocks.COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_VALVE_WHEEL));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_VALVE_WHEEL));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.COPPER_HEAVY_TRAPDOOR), modLoc("block/copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_HEAVY_TRAPDOOR), modLoc("block/exposed_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_HEAVY_TRAPDOOR), modLoc("block/weathered_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_HEAVY_TRAPDOOR), modLoc("block/oxidized_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_HEAVY_TRAPDOOR), modLoc("block/copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_HEAVY_TRAPDOOR), modLoc("block/exposed_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_HEAVY_TRAPDOOR), modLoc("block/weathered_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_HEAVY_TRAPDOOR), modLoc("block/oxidized_copper_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_HEAVY_TRAPDOOR), modLoc("block/dark_iron_heavy_trapdoor_bottom"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_HEAVY_TRAPDOOR), modLoc("block/weathered_copper_heavy_trapdoor_bottom"));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.IRON_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.IRON_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.DARK_IRON_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.EXPOSED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WEATHERED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.OXIDIZED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_EXPOSED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_WEATHERED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_CORNER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_OXIDIZED_COPPER_CORNER_PLATE_BRACKET.getId().getPath()));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.IRON_PLATE_BRACKET), modLoc("block/" + ModBlocks.IRON_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_PLATE_BRACKET), modLoc("block/" + ModBlocks.DARK_IRON_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.EXPOSED_COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WEATHERED_COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.OXIDIZED_COPPER_PLATE_BRACKET.getId().getPath()));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_EXPOSED_COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_WEATHERED_COPPER_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_OXIDIZED_COPPER_PLATE_BRACKET.getId().getPath()));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.IRON_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.IRON_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.DARK_IRON_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.EXPOSED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.WEATHERED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.OXIDIZED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));

		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_EXPOSED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_WEATHERED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_WEATHERED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_ANGLE_PLATE_BRACKET), modLoc("block/" + ModBlocks.WAXED_OXIDIZED_COPPER_ANGLE_PLATE_BRACKET.getId().getPath()));

		blockItemParent(ModBlocks.MAP.get(ModBlocks.WALL_RING));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.TERRACOTTA_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DIRTY_HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DUNGEON_LANTERN));

		basicItem(ModBlocks.MAP.get(ModBlocks.CRIMSON_DUNGEON_DOOR), mcLoc("item/crimson_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.DARK_OAK_DUNGEON_DOOR), mcLoc("item/dark_oak_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.MANGROVE_DUNGEON_DOOR), mcLoc("item/mangrove_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.SPRUCE_DUNGEON_DOOR), mcLoc("item/spruce_door"));

		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LEFT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RIGHT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LARGE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LARGE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LARGE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LARGE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LEFT_LARGE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RIGHT_LARGE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LEFT_LARGE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RIGHT_LARGE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.COBBLESTONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_COBBLESTONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.GRAVEL_BRICK));

		basicItem(ModBlocks.MAP.get(ModBlocks.ROOTS), modLoc("block/roots_head"));
		basicItem(ModBlocks.MAP.get(ModBlocks.ROOTS_BODY), modLoc("block/roots_body"));

		// copper door items: flat icon from the door item texture (waxed reuse the un-waxed texture)
		basicItem(ModBlocks.MAP.get(ModBlocks.COPPER_DOOR), modLoc("item/copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.EXPOSED_COPPER_DOOR), modLoc("item/exposed_copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_DOOR), modLoc("item/weathered_copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.OXIDIZED_COPPER_DOOR), modLoc("item/oxidized_copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.WAXED_COPPER_DOOR), modLoc("item/copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.WAXED_EXPOSED_COPPER_DOOR), modLoc("item/exposed_copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.WAX_WEATHERED_COPPER_DOOR), modLoc("item/weathered_copper_door"));
		basicItem(ModBlocks.MAP.get(ModBlocks.WAXED_OXIDIZED_COPPER_DOOR), modLoc("item/oxidized_copper_door"));

		// copper trapdoor items: parent to the generated bottom model
		copperTrapdoorItem(ModBlocks.COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.EXPOSED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.WEATHERED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.OXIDIZED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.WAXED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
		copperTrapdoorItem(ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

		basicItem(ModItems.LICHEN, modLoc("block/lichen"));
		basicItem(ModItems.MOLD, modLoc("block/mold"));

		basicItem(ModItems.SKELETON, modLoc("item/skeleton"));
	}

	public ItemModelBuilder basicItem(DeferredHolder<Item, ? extends Item> item, ResourceLocation texture) {
		return getBuilder(item.getId().toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", texture);
	}

	/** Trapdoor item model parents to the block's generated "_bottom" model. */
	public ItemModelBuilder copperTrapdoorItem(DeferredHolder<Block, ? extends Block> block) {
		String name = block.getId().getPath();
		return withExistingParent(name, modLoc("block/" + name + "_bottom"));
	}

	public ItemModelBuilder blockItemParent(DeferredHolder<Item, ? extends Item> item) {
		return withExistingParent(item.getId().getPath(), modLoc("block/" + item.getId().getPath()));
	}

	public ItemModelBuilder withExistingParent(DeferredHolder<Item, ? extends Item> item, ResourceLocation parent) {
		return withExistingParent(item.getId().getPath(), parent);
	}
}
