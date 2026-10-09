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
import mod.gottsch.forge.dungeonblocks.core.block.CopperFamily;
import mod.gottsch.forge.dungeonblocks.core.block.ModBlocks;
import mod.gottsch.forge.dungeonblocks.core.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

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
		// every decorative block shows its block model
		ModBlocks.DECOR.forEach(d -> blockItemParent(ModBlocks.MAP.get(d.block())));
		for (DeferredHolder<Block, Block> b : List.of(ModBlocks.STONE_GREEK_BLOCK, ModBlocks.ANDESITE_GREEK_BLOCK,
				ModBlocks.POLISHED_BASALT_GREEK_BLOCK,
				ModBlocks.SQUARE_STONE_BRICK_FACADE_BLOCK, ModBlocks.SQUARE_MUD_BRICK_FACADE_BLOCK,
				ModBlocks.SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_FACADE_BLOCK,
				ModBlocks.SQUARE_STONE_BRICK_QUARTER_FACADE_BLOCK, ModBlocks.SQUARE_MUD_BRICK_QUARTER_FACADE_BLOCK,
				ModBlocks.SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK, ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_QUARTER_FACADE_BLOCK)) {
			blockItemParent(ModBlocks.MAP.get(b));
		}

		blockItemParent(ModBlocks.MAP.get(ModBlocks.TORCH_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_CHISELED_STONE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_POLISHED_BASALT));
		// flat vanilla cobweb icon, not the 3D block render blockItemParent would give - the thin
		// cutout planes look wrong/invisible from the fixed GUI isometric angle
		basicItem(ModBlocks.MAP.get(ModBlocks.ANGLE_COBWEB_1), mcLoc("block/cobweb"));
		basicItem(ModBlocks.MAP.get(ModBlocks.ANGLE_COBWEB_2), mcLoc("block/cobweb"));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.CANDLE_SCONCE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.BRAZIER));
		ModBlocks.DARK_IRON_GRATES.forEach((age, b) -> blockItemParent(ModBlocks.MAP.get(b)));
		ModBlocks.COPPER_GRATES.forEach((age, b) -> blockItemParent(ModBlocks.MAP.get(b)));
		ModBlocks.COPPER_HEAVY_GRATES.forEach((age, b) -> blockItemParent(ModBlocks.MAP.get(b)));
		ModBlocks.COPPER_VALVE_WHEELS.forEach((age, b) -> blockItemParent(ModBlocks.MAP.get(b)));

		// heavy trapdoors: the closed bottom model (waxed reuse the un-waxed one)
		ModBlocks.COPPER_HEAVY_TRAPDOORS.forEach((age, b) -> withExistingParent(ModBlocks.MAP.get(b),
				modLoc("block/" + CopperFamily.id(age, "copper_heavy_trapdoor") + "_bottom")));
		ModBlocks.DARK_IRON_HEAVY_TRAPDOORS.forEach((age, b) -> withExistingParent(ModBlocks.MAP.get(b),
				modLoc("block/" + age.id("dark_iron_heavy_trapdoor") + "_bottom")));

		// plate brackets: each block's own model
		for (DeferredHolder<Block, Block> b : List.of(ModBlocks.IRON_CORNER_PLATE_BRACKET, ModBlocks.DARK_IRON_CORNER_PLATE_BRACKET,
				ModBlocks.IRON_PLATE_BRACKET, ModBlocks.DARK_IRON_PLATE_BRACKET,
				ModBlocks.IRON_ANGLE_PLATE_BRACKET, ModBlocks.DARK_IRON_ANGLE_PLATE_BRACKET)) {
			ownBlockModel(b);
		}
		for (CopperFamily family : List.of(ModBlocks.COPPER_CORNER_PLATE_BRACKETS, ModBlocks.COPPER_PLATE_BRACKETS,
				ModBlocks.COPPER_ANGLE_PLATE_BRACKETS)) {
			family.forEach((age, b) -> ownBlockModel(b));
		}

		blockItemParent(ModBlocks.MAP.get(ModBlocks.WALL_RING));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.WEATHERED_COPPER_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.TERRACOTTA_SEWER));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DIRTY_HAY_PATCH));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DUNGEON_LANTERN));

		// dungeon doors show their vanilla wood's door icon
		ModBlocks.DUNGEON_DOORS.forEach(door -> basicItem(ModBlocks.MAP.get(door.block()), mcLoc("item/" + door.wood() + "_door")));

		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_STONE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_STONE_BRICK_SLAB));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_STONE_BRICK_SLAB));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_MUD_BRICK));
		// the facade / quarter facade item models are covered by the MAP sweep above
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_STONE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_MUD_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LEFT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RIGHT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LEFT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RIGHT_LARGE_STONE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LEFT_LARGE_MUD_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RIGHT_LARGE_MUD_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_MUD_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LEFT_LARGE_MUD_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RIGHT_LARGE_MUD_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_DEEPSLATE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.SQUARE_DEEPSLATE_BRICK_SLAB));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_SQUARE_DEEPSLATE_BRICK_SLAB));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.LEFT_LARGE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RIGHT_LARGE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_LEFT_LARGE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RIGHT_LARGE_DEEPSLATE_BRICK));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.CHISELED_DEEPSLATE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_CHISELED_DEEPSLATE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_DEEPSLATE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_DEEPSLATE_TILES));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_COBBLED_DEEPSLATE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_TUFF));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_DEEPSLATE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.POLISHED_ANDESITE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.POLISHED_ANDESITE_BRICK_STAIRS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_POLISHED_ANDESITE_BRICKS));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_POLISHED_ANDESITE_BRICK_STAIRS));
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
		blockItemParent(ModBlocks.MAP.get(ModBlocks.RUBBLE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.MOSSY_RUBBLE));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.GRAVEL_BRICK));

		basicItem(ModBlocks.MAP.get(ModBlocks.ROOTS), modLoc("block/roots_head"));
		basicItem(ModBlocks.MAP.get(ModBlocks.ROOTS_BODY), modLoc("block/roots_body"));

		// copper door items: flat icon from the door item texture (waxed reuse the un-waxed texture)
		ModBlocks.COPPER_DOORS.forEach((age, b) -> basicItem(ModBlocks.MAP.get(b), modLoc("item/" + CopperFamily.id(age, "copper_door"))));
		basicItem(ModBlocks.MAP.get(ModBlocks.IRON_BARS_DOOR), modLoc("item/iron_bars_door"));
		ModBlocks.DARK_IRON_BARS_DOORS.forEach((age, b) -> basicItem(ModBlocks.MAP.get(b), modLoc("item/" + age.id("dark_iron_bars_door"))));
		// bars show their flat texture in the inventory, as vanilla iron bars do
		ModBlocks.DARK_IRON_BARS.forEach((age, b) -> basicItem(ModBlocks.MAP.get(b), modLoc("block/" + age.id("dark_iron_bars"))));
		// drawn flat, as vanilla's ladder is: see tools/gen_prop_item_icons.py
		basicItem(ModBlocks.MAP.get(ModBlocks.DARK_IRON_LADDER), modLoc("item/dark_iron_ladder"));
		ModBlocks.SHARPENED_LOGS.forEach(b -> blockItemParent(ModBlocks.MAP.get(b)));
		ModBlocks.CAPSTONES.keySet().forEach(b -> blockItemParent(ModBlocks.MAP.get(b)));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.IRON_SPIKES));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.DARK_IRON_SPIKES));
		ModBlocks.CHEVALS_DE_FRISE.forEach(b -> blockItemParent(ModBlocks.MAP.get(b)));
		ModBlocks.WALKWAY_BRACKETS.forEach(b -> blockItemParent(ModBlocks.MAP.get(b)));
		// the portcullis item shows a bottom cell, tips and all - the piece that says "portcullis"
		withExistingParent(ModBlocks.MAP.get(ModBlocks.PORTCULLIS), modLoc("block/portcullis_bottom"));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.PORTCULLIS_WINCH));
		// multi-block props show the part that says what they are: the effigy's head end
		withExistingParent(ModBlocks.MAP.get(ModBlocks.STONE_SARCOPHAGUS), modLoc("block/stone_sarcophagus_head_closed"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.DEEPSLATE_SARCOPHAGUS), modLoc("block/deepslate_sarcophagus_head_closed"));
		// the iron maiden whole, shut, as the gibbet
		withExistingParent(ModBlocks.MAP.get(ModBlocks.IRON_MAIDEN), modLoc("block/iron_maiden_item"));
		// no one block of the gibbet reads as a gibbet, so the item is the whole cage at a third size
		withExistingParent(ModBlocks.MAP.get(ModBlocks.GIBBET), modLoc("block/gibbet_item"));
		// the pillory and the rack whole, as the gibbet: shut, and slack
		for (DeferredHolder<Block, Block> block : List.of(ModBlocks.PILLORY, ModBlocks.OCCUPIED_PILLORY,
				ModBlocks.TORTURE_RACK, ModBlocks.OCCUPIED_TORTURE_RACK)) {
			withExistingParent(ModBlocks.MAP.get(block), modLoc("block/" + block.getId().getPath() + "_item"));
		}
		// the item shows a full rack, though a placed one starts empty
		withExistingParent(ModBlocks.MAP.get(ModBlocks.FIREWOOD_RACK), modLoc("block/firewood_rack_4"));
		// an empty rack reads as a bare frame at icon size, and an item has no renderer to hang
		// weapons in it, so the item's model has a sword and an axe racked
		withExistingParent(ModBlocks.MAP.get(ModBlocks.WEAPON_RACK), modLoc("block/weapon_rack_item"));
		// the whole coffin, closed, at half size: half a coffin does not say "coffin"
		ModBlocks.COFFINS.forEach(b -> withExistingParent(ModBlocks.MAP.get(b), modLoc("block/" + b.getId().getPath() + "_item")));
		// a niche shows the skull and bones every niche had before there was a choice of remains
		ModBlocks.CATACOMB_NICHES.keySet().forEach(b ->
				withExistingParent(ModBlocks.MAP.get(b), modLoc("block/" + b.getId().getPath() + "_skull_and_bones")));
		// the pike's top half, the skull on its pole, is the part that says what it is
		withExistingParent(ModBlocks.MAP.get(ModBlocks.SKULL_PIKE), modLoc("block/skull_pike_upper"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.ZOMBIE_HEAD_PIKE), modLoc("block/zombie_head_pike_upper"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.BLOODY_STEVE_HEAD_PIKE), modLoc("block/bloody_steve_head_pike_upper"));
		// a full heap, though a placed pile starts small
		withExistingParent(ModBlocks.MAP.get(ModBlocks.BONE_PILE), modLoc("block/bone_pile_4"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.CHANDELIER), modLoc("block/chandelier"));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.PERCHED_GARGOYLE));
		// the lever sconce shows as the torch sconce it pretends to be
		withExistingParent(ModBlocks.MAP.get(ModBlocks.LEVER_SCONCE), modLoc("block/torch_sconce_block"));
		// a hidden door shows a flat door icon in its wall's stone, as vanilla's doors show theirs
		// (tools/gen_prop_item_icons.py): its lower half drawn as a block read as a slab of wall
		ModBlocks.HIDDEN_DOORS.keySet().forEach(b ->
				basicItem(ModBlocks.MAP.get(b), modLoc("item/" + b.getId().getPath())));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.PEDESTAL));
		// a tapestry shows its whole scene, shrunk flat (tools/gen_tapestry_textures.py)
		ModBlocks.TAPESTRIES.forEach(b -> basicItem(ModBlocks.MAP.get(b), modLoc("item/" + b.getId().getPath())));
		ModBlocks.CRUMBLING_FLOORS.keySet().forEach(b -> blockItemParent(ModBlocks.MAP.get(b)));
		// the full scatter, though a placed one starts at a few chips
		withExistingParent(ModBlocks.MAP.get(ModBlocks.RUBBLE_SCATTER), modLoc("block/rubble_scatter_4"));
		blockItemParent(ModBlocks.MAP.get(ModBlocks.GARGOYLE_BUST));
		// the whole statue, shrunk to fit: its top half alone is a pair of wings
		withExistingParent(ModBlocks.MAP.get(ModBlocks.GARGOYLE_STATUE), modLoc("block/gargoyle_statue_item"));
		// the chain fixtures show the model they hang as - bar the meat hook, a thin line of iron
		// at icon size, which has a flat icon (tools/gen_prop_item_icons.py) as vanilla's chain does
		withExistingParent(ModBlocks.MAP.get(ModBlocks.MANACLES), modLoc("block/chain_fixture_manacles"));
		basicItem(ModBlocks.MAP.get(ModBlocks.MEAT_HOOK), modLoc("item/meat_hook"));
		withExistingParent(ModBlocks.MAP.get(ModBlocks.CENSER), modLoc("block/chain_fixture_censer"));
		// vanilla's cauldron icon with the default green brew baked in (tools/gen_bubbling_cauldron_item.py)
		basicItem(ModBlocks.MAP.get(ModBlocks.BUBBLING_CAULDRON), modLoc("item/bubbling_cauldron"));

		// copper trapdoor items: parent to the generated bottom model
		ModBlocks.COPPER_TRAPDOORS.forEach((age, b) -> copperTrapdoorItem(b));

		basicItem(ModItems.LICHEN, modLoc("block/lichen"));
		basicItem(ModItems.MOLD, modLoc("block/mold"));

		basicItem(ModItems.SKELETON, modLoc("item/skeleton"));

		// the chain block is drawn by a BlockEntityRenderer and has no baked model to inherit from,
		// so its item uses vanilla's chain sprite
		basicItem(ModBlocks.MAP.get(ModBlocks.SWINGING_CHAIN), mcLoc("item/chain"));

		// same story for the banners: nothing baked to inherit, so each one's flat block sprite -
		// which is also its break particle - is the icon
		ModBlocks.BANNERS.forEach(banner ->
				basicItem(ModBlocks.MAP.get(banner), modLoc("block/" + banner.getId().getPath())));

		slabTableItem(ModBlocks.STONE_SLAB_TABLE);
		slabTableItem(ModBlocks.STONE_BRICKS_SLAB_TABLE);
		slabTableItem(ModBlocks.MOSSY_STONE_BRICKS_SLAB_TABLE);
		slabTableItem(ModBlocks.SMOOTH_STONE_SLAB_TABLE);
		slabTableItem(ModBlocks.SMOOTH_SANDSTONE_SLAB_TABLE);

		// pot props render as real 3D geometry in the inventory, not as sprites — see PotItemRenderer
		potItem(ModItems.POT);
		potItem(ModItems.SQUAT_CLAY_POT);
		potItem(ModItems.THIN_CLAY_POT);
		potItem(ModItems.STONE_POT);
		potItem(ModItems.SQUAT_STONE_POT);
		potItem(ModItems.THIN_STONE_POT);
		potItem(ModItems.RED_POT);
		potItem(ModItems.SQUAT_RED_POT);
		potItem(ModItems.THIN_RED_POT);
		potItem(ModItems.BLUE_POT);
		potItem(ModItems.SQUAT_BLUE_POT);
		potItem(ModItems.THIN_BLUE_POT);
		potItem(ModItems.BIG_RED_POTION);
		potItem(ModItems.RED_FLASK);
		potItem(ModItems.BIG_YELLOW_POTION);
		potItem(ModItems.YELLOW_FLASK);
		potItem(ModItems.BIG_BLUE_POTION);
		potItem(ModItems.BLUE_FLASK);
		potItem(ModItems.BIG_GREEN_POTION);
		potItem(ModItems.GREEN_FLASK);
		// a tome's item is the icon its cover was taken from, as it is
		ModItems.TOMES.values().forEach(tome -> basicItem(tome.get()));
		// and a scroll's, likewise
		ModItems.SCROLLS.values().forEach(scroll -> basicItem(scroll.get()));
	}

	public ItemModelBuilder basicItem(DeferredHolder<Item, ? extends Item> item, ResourceLocation texture) {
		return getBuilder(item.getId().toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", texture);
	}

	/**
	 * Item model for an entity-backed prop that should render as 3D geometry rather than a flat
	 * sprite.
	 *
	 * <p>{@code builtin/entity} bakes to a vanilla {@code BuiltInModel}, whose
	 * {@code isCustomRenderer()} is true — that is what routes rendering to the item's
	 * {@code IClientItemExtensions} renderer. It carries no geometry and no texture of its own, so the
	 * {@code display} block below (copied from vanilla {@code block/block.json}) is what positions the
	 * prop per context, giving it block-identical placement in the GUI, in hand, on the ground and in
	 * item frames.
	 */
	public ItemModelBuilder potItem(DeferredHolder<Item, Item> item) {
		ItemModelBuilder builder = getBuilder(item.getId().toString())
				.parent(new ModelFile.UncheckedModelFile("builtin/entity"));
		builder.transforms()
				.transform(ItemDisplayContext.GUI)
						.rotation(30, 225, 0).scale(0.625F).end()
				.transform(ItemDisplayContext.GROUND)
						.translation(0, 3, 0).scale(0.25F).end()
				.transform(ItemDisplayContext.FIXED)
						.scale(0.5F).end()
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
						.rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
						.rotation(0, 45, 0).scale(0.4F).end()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
						.rotation(0, 225, 0).scale(0.4F).end()
				.end();
		return builder;
	}

	/**
	 * The slab table has no model under its own name — only "_foot" and "_head" halves — so the item
	 * shows the foot, which is the half that lands where the player clicks.
	 */
	public ItemModelBuilder slabTableItem(DeferredHolder<Block, Block> block) {
		String name = block.getId().getPath();
		return withExistingParent(name, modLoc("block/" + name + "_foot"));
	}

	/** An item parented to its block's own model, block/<id>. */
	private void ownBlockModel(DeferredHolder<Block, Block> block) {
		withExistingParent(ModBlocks.MAP.get(block), modLoc("block/" + block.getId().getPath()));
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
