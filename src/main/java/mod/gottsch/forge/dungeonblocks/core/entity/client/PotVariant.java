/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * DungeonBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DungeonBlocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.entity.client;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.PotEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The client-side description of one pot shape — everything that differs between the pot variants,
 * declared once so the entity renderer, the layer definitions and the inventory renderer all read
 * from the same place instead of each keeping its own copy of the model/texture/height table.
 *
 * @param entityType    the type this shape is registered as
 * @param layer         the baked layer holding this shape's geometry
 * @param modelFactory  wraps a baked {@link ModelPart} in the matching model class
 * @param texture       entity texture
 * @param modeledHeight total modeled height in blocks (base + neck + lip), used to centre the pot
 *                      in its inventory slot
 * @param modeledWidth  width of the widest part in blocks - always the body box, since neck and lip
 *                      are narrower on every shape. This is the part that meets the floor once the
 *                      pot is on its side, so half of it is the tumble pivot
 * @param scale         uniform render scale applied in the world. 1.0 for the pots, which are
 *                      modeled at the size they are meant to be; the potion prop is modeled at pot
 *                      size and shrunk here instead of being re-modeled. Note that
 *                      {@code modeledHeight} and {@code modeledWidth} stay the <em>unscaled</em>
 *                      Blockbench extents - the world renderer applies the scale itself, and the
 *                      inventory renderer deliberately ignores it so a small prop still fills its
 *                      slot the way every other item does
 *
 * @author Mark Gottschling on Jul 26, 2026
 */
@OnlyIn(Dist.CLIENT)
public record PotVariant(
		Supplier<EntityType<PotEntity>> entityType,
		ModelLayerLocation layer,
		Function<ModelPart, EntityModel<PotEntity>> modelFactory,
		ResourceLocation texture,
		float modeledHeight,
		float modeledWidth,
		float scale) {

	/** A variant rendered at its modeled size, which is every pot. */
	public PotVariant(Supplier<EntityType<PotEntity>> entityType, ModelLayerLocation layer,
			Function<ModelPart, EntityModel<PotEntity>> modelFactory, ResourceLocation texture,
			float modeledHeight, float modeledWidth) {
		this(entityType, layer, modelFactory, texture, modeledHeight, modeledWidth, 1.0F);
	}

	/** Vertical centre of the upright pot, used to centre it in an inventory slot. */
	public double halfHeight() {
		return this.modeledHeight / 2.0D;
	}

	/**
	 * Pivot for the tumble rotation.
	 *
	 * <p>Half the <em>width</em>, not half the height: rotating 90&deg; about a point at height
	 * {@code y} leaves the body's axis at that height, and the body then hangs half its width below.
	 * Pivoting at half-height therefore parks a pot {@code (height - width) / 2} above the floor -
	 * 3px of hover on every shape except the squat pot, which is as wide as it is tall and so was
	 * the only one that ever looked right.
	 */
	public double tumblePivot() {
		return this.modeledWidth / 2.0D;
	}

	public static ResourceLocation entityTexture(String name) {
		return ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "textures/entity/" + name + ".png");
	}
}
