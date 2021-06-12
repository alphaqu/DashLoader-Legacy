package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.ParticleManagerSimpleSpriteProviderAccessor;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashParticleData {

    @Serialize(order = 0)
    public Pointer2ObjectMap<List<Integer>> particles;

    public DashParticleData(@Deserialize("particles") Pointer2ObjectMap<List<Integer>> particles) {
        this.particles = particles;
    }

    public DashParticleData(Map<Identifier, ParticleManager.SimpleSpriteProvider> particles, DashRegistry registry) {
        this.particles = new Pointer2ObjectMap<>();
        particles.forEach((identifier, simpleSpriteProvider) -> {
            List<Integer> out = new ArrayList<>();
            ((ParticleManagerSimpleSpriteProviderAccessor) simpleSpriteProvider).getSprites().forEach(sprite -> out.add(registry.createSpritePointer(sprite)));
            this.particles.put(registry.createIdentifierPointer(identifier), out);
        });
    }


    public Map<Identifier, List<Sprite>> toUndash(DashRegistry registry) {
        Map<Identifier, List<Sprite>> out = new HashMap<>();
        particles.forEach((entry) -> {
            List<Sprite> outInner = new ArrayList<>();
            entry.value.forEach(integer1 -> outInner.add(registry.getSprite(integer1)));
            out.put(registry.getIdentifier(entry.key), outInner);
        });
        return out;
    }

}
