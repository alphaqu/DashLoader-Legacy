package net.quantumfusion.dash.mixin;

import com.google.common.base.Charsets;
import io.activej.serializer.stream.StreamOutput;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.misc.DashParticleTextureData;
import net.quantumfusion.dash.util.StringHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@Shadow @Final private Map<Identifier, Object> spriteAwareFactories;

	@Inject(method = "loadTextureList(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;Ljava/util/Map;)V",at = @At(value = "HEAD"),cancellable = true)
	private void loadTextureListFast(ResourceManager resourceManager, Identifier id, Map<Identifier, List<Identifier>> result, CallbackInfo ci) {
		Identifier identifier = new Identifier(id.getNamespace(), "particles/" + id.getPath() + ".json");
		try {
			HashMap<Identifier, List<Identifier>> particleCache = Dash.particleCache;
			List<Identifier> list;
			if (particleCache.containsKey(id)) {
				list = particleCache.get(id);
			} else {
				Resource resource = resourceManager.getResource(identifier);
				Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
				ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
				list = particleTextureData.getTextureList();
				String dashId = StringHelper.idToFile(id);
				StreamOutput output = StreamOutput.create(Files.newOutputStream(Dash.config.resolve("dash/particles/" + dashId + ".activej"), StandardOpenOption.CREATE, StandardOpenOption.WRITE));
				output.serialize(Dash.dashParticleTextureDataSerializer, new DashParticleTextureData(particleTextureData, id));
				output.flush();
				reader.close();
				resource.close();
				System.out.println("Created particle: " + id);
			}
			boolean bl = this.spriteAwareFactories.containsKey(id);
			if (list == null) {
				if (bl) {
					throw new IllegalStateException("Missing texture list for particle " + id);
				}
			} else {
				if (!bl) {
					throw new IllegalStateException("Redundant texture list for particle " + id);
				}

				result.put(id, list.stream().map((identifierx) -> new Identifier(identifierx.getNamespace(), "particle/" + identifierx.getPath())).collect(Collectors.toList()));
			}
		} catch (IOException var39) {
			throw new IllegalStateException("Failed to load description for particle " + id, var39);
		}
	}
}
