
package mod.gottsch.forge.dungeonblocks.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * basically a copy of vanilla SuspendedParticle
 */
public class BlackSporeParticle extends TextureSheetParticle {

   public BlackSporeParticle(ClientLevel level, SpriteSet spriteSet, double x, double y, double z) {
        super(level, x, y - 0.125D, z);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(spriteSet);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    public BlackSporeParticle(ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y - 0.125D, z, xd, yd, zd);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(spriteSet);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            BlackSporeParticle particle = new BlackSporeParticle(level, this.sprite, x, y, z, 0.0D, (double)-0.8F, 0.0D) {
                public Optional<ParticleGroup> getParticleGroup() {
                    return Optional.of(ParticleGroup.SPORE_BLOSSOM);
                }
            };
            particle.lifetime = Mth.randomBetweenInclusive(level.random, 500, 1000);
            particle.gravity = 0.01F;
            particle.setColor(0F, 0F, 0F);
            return particle;
        }
    }
}
