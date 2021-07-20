package net.oskarstrom.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.VanillaData;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.image.DashSpriteAtlasTexture;
import net.oskarstrom.dashloader.util.ThreadHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashParticleData implements Dashable {

	@Serialize(order = 0)
	public final Pointer2ObjectMap<List<Integer>> particles;

	@Serialize(order = 1)
	public final DashSpriteAtlasTexture atlasTexture;

	public DashParticleData(@Deserialize("particles") Pointer2ObjectMap<List<Integer>> particles,
							@Deserialize("atlasTexture") DashSpriteAtlasTexture atlasTexture) {
		this.particles = particles;
		this.atlasTexture = atlasTexture;
	}

	public DashParticleData(VanillaData data, DashRegistry registry, DashLoader.TaskHandler taskHandler) {
		this.particles = new Pointer2ObjectMap<>();
		final Map<Identifier, List<Sprite>> particles = data.getParticles();
		taskHandler.setSubtasks(particles.size() + 1);
		ThreadHelper.execForEach(particles, (identifier, spriteList) -> {
			List<Integer> out = new ArrayList<>();
			spriteList.forEach(sprite -> out.add(registry.sprites.register(sprite)));
			this.particles.put(registry.identifiers.register(identifier), out);
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
			entry.value.forEach(integer1 -> outInner.add(registry.sprites.getObject(integer1)));
			out.put(registry.identifiers.getObject(entry.key), outInner);
		});
		return Pair.of(out, atlasTexture.toUndash(registry));
	}

}
