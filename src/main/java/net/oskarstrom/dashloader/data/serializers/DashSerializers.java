package net.oskarstrom.dashloader.data.serializers;

import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashMappings;
import net.oskarstrom.dashloader.api.DashLoaderAPI;
import net.oskarstrom.dashloader.data.DashRegistryData;
import net.oskarstrom.dashloader.data.registry.RegistryImageData;
import net.oskarstrom.dashloader.data.registry.RegistryModelData;
import net.oskarstrom.dashloader.util.TimeHelper;
import net.oskarstrom.dashloader.util.enums.DashCachePaths;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DashSerializers {

	public final static List<DashSerializer<?>> SERIALIZERS = new ArrayList<>();
	public final static DashSerializer<DashRegistryData> REGISTRY_SERIALIZER;
	public final static DashSerializer<DashMappings> MAPPING_SERIALIZER;
	public final static DashSerializer<RegistryModelData> MODEL_SERIALIZER;
	public final static DashSerializer<RegistryImageData> IMAGE_SERIALIZER;

	static {
		final DashLoader loader = DashLoader.getInstance();
		REGISTRY_SERIALIZER = addSerializer(new DashSerializer<>(loader, "registry", DashCachePaths.REGISTRY_CACHE.getPath(), (builder) -> {
			final DashLoaderAPI api = loader.getApi();
			api.initAPI();
			api.applyTypes(builder, "fonts");
			api.applyTypes(builder, "predicates");
			api.applyTypes(builder, "properties");
			api.applyTypes(builder, "values");
			api.applyTypes(builder, "data");
			return builder.build(DashRegistryData.class);
		}));
		MODEL_SERIALIZER = addSerializer(new DashSerializer<>(loader, "model", DashCachePaths.REGISTRY_MODEL_CACHE.getPath(), (builder) -> {
			final DashLoaderAPI api = loader.getApi();
			api.initAPI();
			Thread.currentThread().setContextClassLoader(DashLoader.getInstance().getAssignedClassLoader());
			api.applyTypes(builder, "models");
			return builder.build(RegistryModelData.class);
		}));
		IMAGE_SERIALIZER = addSerializer(new DashSerializer<>(loader, "image", DashCachePaths.REGISTRY_IMAGE_CACHE.getPath(), (builder) -> builder.build(RegistryImageData.class)));
		MAPPING_SERIALIZER = addSerializer(new DashSerializer<>(loader, "mapping", DashCachePaths.MAPPINGS_CACHE.getPath(), builder -> builder.build(DashMappings.class)));
	}

	private static <O> DashSerializer<O> addSerializer(DashSerializer<O> serializer) {
		SERIALIZERS.add(serializer);
		return serializer;
	}

	public static void initSerializers() {
		Instant start = Instant.now();
		//create serializer objects
		//initialize the serializers
		if (!createSerializers(false)) {
			createSerializers(true);
		}
		DashLoader.LOGGER.info("[{}ms] Initialized Serializers", TimeHelper.getMs(start));
	}

	private static boolean createSerializers(boolean forceRecache) {
		for (DashSerializer<?> serializer : SERIALIZERS) {
			//if serializer cant load from cache we wanna recreate them;
			if (!serializer.createSerializer(forceRecache)) {
				if (!forceRecache) return false;
			}
		}
		return true;
	}


}
