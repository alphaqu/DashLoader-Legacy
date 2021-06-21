package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.VanillaData;
import net.quantumfusion.dashloader.util.TaskHandler;
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

    public DashParticleData(VanillaData data, DashRegistry registry, TaskHandler taskHandler) {
        this.particles = new Pointer2ObjectMap<>();
        final Map<Identifier, List<Sprite>> particles = data.getParticles();
        taskHandler.setSubtasks(particles.size());
        particles.forEach((identifier, spriteList) -> {
            List<Integer> out = new ArrayList<>();
            spriteList.forEach(sprite -> out.add(registry.createSpritePointer(sprite)));
            this.particles.put(registry.createIdentifierPointer(identifier), out);
            taskHandler.completedSubTask();
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
