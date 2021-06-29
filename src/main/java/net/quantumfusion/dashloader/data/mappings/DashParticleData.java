package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTexture;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashParticleData implements Dashable {

    @Serialize(order = 0)
    public Pointer2ObjectMap<List<Integer>> particles;

    @Serialize(order = 1)
    public DashSpriteAtlasTexture atlasTexture;

    public DashParticleData(@Deserialize("particles") Pointer2ObjectMap<List<Integer>> particles,
                            @Deserialize("atlasTexture") DashSpriteAtlasTexture atlasTexture) {
        this.particles = particles;
        this.atlasTexture = atlasTexture;
    }

    public DashParticleData(VanillaData data, DashRegistry registry, DashLoader.TaskHandler taskHandler) {
        this.particles = new Pointer2ObjectMap<>();
        final Map<Identifier, List<Sprite>> particles = data.getParticles();
        taskHandler.setSubtasks(particles.size() + 1);
        particles.forEach((identifier, spriteList) -> {
            List<Integer> out = new ArrayList<>();
            spriteList.forEach(sprite -> out.add(registry.createSpritePointer(sprite)));
            this.particles.put(registry.createIdentifierPointer(identifier), out);
            taskHandler.completedSubTask();
        });
        final SpriteAtlasTexture particleAtlas = data.getParticleAtlas();
        atlasTexture = new DashSpriteAtlasTexture(particleAtlas, data.getAtlasData(particleAtlas), registry);
        taskHandler.completedSubTask();

    }


    public Pair<Map<Identifier, List<Sprite>>, SpriteAtlasTexture> toUndash(DashRegistry registry) {
        Map<Identifier, List<Sprite>> out = new HashMap<>();
        particles.forEach((entry) -> {
            List<Sprite> outInner = new ArrayList<>();
            entry.value.forEach(integer1 -> outInner.add(registry.getSprite(integer1)));
            out.put(registry.getIdentifier(entry.key), outInner);
        });
        return Pair.of(out, atlasTexture.toUndash(registry));
    }

}
