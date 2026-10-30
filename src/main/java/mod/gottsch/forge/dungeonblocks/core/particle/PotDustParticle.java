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
package mod.gottsch.forge.dungeonblocks.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * The puff of dust thrown off when a ceramic pot shatters: vanilla {@code POOF} in every respect
 * except size.
 *
 * <p>This is a deliberate copy of vanilla's {@code ExplodeParticle} (which is what {@code POOF}
 * actually is) — grey {@code 0.7..1.0}, <em>negative</em> gravity so the puff drifts gently upward,
 * friction 0.9, the same velocity jitter and the same lifetime curve. The one change is
 * {@link #quadSize}: POOF uses {@code 0.1 * (r*r*6 + 1)} (0.1–0.7, averaging ~0.2, so ~0.4 blocks
 * across and up to 1.4) which is wider than the pots themselves and buries the shard entities.
 * The values here average ~0.2 blocks across instead.
 *
 * <p>Why a custom type rather than just spawning {@code POOF}: size is the only thing that needed
 * changing, and a {@code SimpleParticleType} has no size parameter to pass.
 *
 * <p>The dust read as <em>fragments</em> before this because it used a single hard-edged terracotta
 * sprite. POOF's dissipating look comes from animating through the eight vanilla {@code generic_N}
 * sprites — a scattered cloud shrinking to a dot — which {@code particles/pot_dust.json} now
 * references in the same order vanilla's {@code poof.json} does (generic_7 down to generic_0).
 *
 * @author Mark Gottschling on Jul 25, 2026
 */
public class PotDustParticle extends TextureSheetParticle {

	// POOF's 0.1F/6.0F, scaled to ~45%: 0.04..0.24, averaging ~0.11 -> ~0.22 blocks across.
	// The r*r weighting is vanilla's and keeps most motes small with the occasional bigger one.
	private static final float SIZE_BASE = 0.04F;
	private static final float SIZE_SPREAD = 5.0F;
	private static final float VELOCITY_JITTER = 0.05F;

	private final SpriteSet sprites;

	protected PotDustParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z,
			double xd, double yd, double zd) {
		super(level, x, y, z);
		this.sprites = sprites;

		// note the negative gravity — POOF rises as it fades rather than settling
		this.gravity = -0.1F;
		this.friction = 0.9F;

		// take the server-sent velocity and add POOF's jitter; the 6-arg super would re-randomize
		// and renormalize the velocity instead, discarding what the server sent
		this.xd = xd + (this.random.nextDouble() * 2.0D - 1.0D) * VELOCITY_JITTER;
		this.yd = yd + (this.random.nextDouble() * 2.0D - 1.0D) * VELOCITY_JITTER;
		this.zd = zd + (this.random.nextDouble() * 2.0D - 1.0D) * VELOCITY_JITTER;

		float grey = this.random.nextFloat() * 0.3F + 0.7F;
		this.rCol = grey;
		this.gCol = grey;
		this.bCol = grey;

		this.quadSize = SIZE_BASE
				* (this.random.nextFloat() * this.random.nextFloat() * SIZE_SPREAD + 1.0F);
		this.lifetime = (int) (16.0D / (this.random.nextDouble() * 0.8D + 0.2D)) + 2;
		this.setSpriteFromAge(sprites);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		super.tick();
		// advances through the generic_N frames, which is what makes the puff dissipate
		this.setSpriteFromAge(this.sprites);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
				double x, double y, double z, double xd, double yd, double zd) {
			return new PotDustParticle(level, this.sprites, x, y, z, xd, yd, zd);
		}
	}
}
