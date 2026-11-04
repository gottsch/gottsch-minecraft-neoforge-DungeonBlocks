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

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * A bone-white urn: a pot in every way, except that it lets out a cloud of ash when it breaks. The
 * vanilla ash particles drift down on their own, so the cloud settles rather than just vanishing.
 *
 * @author Mark Gottschling on 11/01/2026
 */
public class CineraryUrnEntity extends PotEntity {

	private static final int ASH_COUNT = 30;
	private static final int WHITE_ASH_COUNT = 20;

	public CineraryUrnEntity(EntityType<? extends PotEntity> type, Level level) {
		super(type, level, PotMaterial.BONE);
	}

	@Override
	protected void spawnBreakParticles(ServerLevel level) {
		super.spawnBreakParticles(level);
		double y = this.getY() + this.getBbHeight() * 0.5D;
		level.sendParticles(ParticleTypes.ASH, this.getX(), y, this.getZ(), ASH_COUNT, 0.3D, 0.3D, 0.3D, 0.0D);
		level.sendParticles(ParticleTypes.WHITE_ASH, this.getX(), y, this.getZ(), WHITE_ASH_COUNT, 0.3D, 0.3D, 0.3D, 0.0D);
	}
}
