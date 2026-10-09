package mod.gottsch.forge.dungeonblocks.core.particle;

import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.setup.Registration;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * @author by Mark Gottschling on 10/9/2025
 */
@EventBusSubscriber(modid = DungeonBlocks.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLACK_SPORE_PARTICLE = Registration.PARTICLES.register("black_spore", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POT_DUST_PARTICLE = Registration.PARTICLES.register("pot_dust", () -> new SimpleParticleType(false));

    public static void register(IEventBus bus) {
        Registration.registerParticles(bus);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BLACK_SPORE_PARTICLE.get(), BlackSporeParticle.Provider::new);
        event.registerSpriteSet(POT_DUST_PARTICLE.get(), PotDustParticle.Provider::new);
    }
}
