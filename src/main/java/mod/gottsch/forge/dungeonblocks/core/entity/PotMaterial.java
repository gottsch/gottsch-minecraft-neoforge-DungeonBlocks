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
package mod.gottsch.forge.dungeonblocks.core.entity;

/**
 * What a pot is made of, and therefore what its debris looks like when it shatters.
 *
 * <p>Shape and material are deliberately separate axes: the three pot shapes each exist in every
 * material, so a new colour is one constant here plus its textures rather than a new model class.
 * The material travels from the {@link PotEntity} to the {@link PotShardEntity} it throws, which is
 * the only reason it needs to exist at runtime at all — a pot's own texture is already fixed by its
 * {@code EntityType}.
 *
 * <p>Ordinals are persisted (shard NBT) and synced, so <b>append new constants at the end</b>.
 *
 * @author Mark Gottschling on Aug 09, 2026
 */
public enum PotMaterial {
	TERRACOTTA("pot_shard"),
	STONE("stone_pot_shard"),
	RED("red_pot_shard"),
	BLUE("blue_pot_shard"),
	GLASS("glass_pot_shard");

	private final String shardTexture;

	private PotMaterial(String shardTexture) {
		this.shardTexture = shardTexture;
	}

	/** File name (no folder, no extension) of this material's shard texture under textures/entity. */
	public String getShardTexture() {
		return this.shardTexture;
	}

	/** Tolerant of out-of-range ordinals so an unknown saved value degrades to terracotta. */
	public static PotMaterial byOrdinal(int ordinal) {
		PotMaterial[] values = values();
		return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : TERRACOTTA;
	}
}
