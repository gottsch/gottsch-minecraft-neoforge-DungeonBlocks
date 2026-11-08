package mod.gottsch.forge.dungeonblocks.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A wisp of pale soul-fire mist, given off by the soul vent: it rises slowly, wanders, swells and
 * fades in and out over a few seconds, so a few at a time read as a thin glowing haze rather than
 * smoke.
 *
 * <p>The sprite is one of vanilla's larger soft {@code generic_N} puffs, picked once and held, since
 * running through the frames (as POOF does) shrinks it to a dot. It is drawn translucent with its
 * alpha on a sine over its life, never above {@link #MAX_ALPHA}, and lit as a full block light, so
 * it glows like the fire it came from in a dark crypt.
 *
 * <p>Sizes stay well above the ~0.06 quadSize below which a particle is sub-pixel.
 *
 * @author Mark Gottschling on 11/03/2026
 */
public class SoulMistParticle extends TextureSheetParticle {
    private static final float MAX_ALPHA = 0.35F;
    private static final float GROWTH = 1.012F;
    private static final double WANDER = 0.0015D;

    protected SoulMistParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z,
                               double xd, double yd, double zd) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.gravity = 0.0F;
        this.friction = 0.98F;
        this.hasPhysics = false;

        // soul fire's cyan, a little paler or deeper per wisp
        float tint = this.random.nextFloat() * 0.15F;
        this.rCol = 0.50F + tint;
        this.gCol = 0.88F + tint * 0.5F;
        this.bCol = 0.95F;
        this.alpha = 0.0F;

        this.quadSize = 0.12F + this.random.nextFloat() * 0.12F;
        this.lifetime = 60 + this.random.nextInt(50);
        this.pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed) {
            return;
        }
        this.xd += (this.random.nextDouble() * 2.0D - 1.0D) * WANDER;
        this.zd += (this.random.nextDouble() * 2.0D - 1.0D) * WANDER;
        this.quadSize *= GROWTH;
        this.alpha = MAX_ALPHA * Mth.sin((float) Math.PI * this.age / this.lifetime);
    }

    /** Full block light, the sky light where it is. */
    @Override
    protected int getLightColor(float partialTick) {
        int sky = super.getLightColor(partialTick) >> 16 & 0xFF;
        return 0xF0 | sky << 16;
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
            return new SoulMistParticle(level, this.sprites, x, y, z, xd, yd, zd);
        }
    }
}
