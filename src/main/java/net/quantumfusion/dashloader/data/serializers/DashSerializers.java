package net.quantumfusion.dashloader.data.serializers;

import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashMappings;
import net.quantumfusion.dashloader.api.DashLoaderAPI;
import net.quantumfusion.dashloader.data.DashRegistryData;
import net.quantumfusion.dashloader.data.registry.RegistryImageData;
import net.quantumfusion.dashloader.data.registry.RegistryModelData;
import net.quantumfusion.dashloader.util.TimeHelper;

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
        REGISTRY_SERIALIZER = addSerializer(new DashSerializer<>(loader, "registry", (builder) -> {
            final DashLoaderAPI api = loader.getApi();
            api.initAPI();
            return builder
                    .withSubclasses("fonts", api.fontTypes)
                    .withSubclasses("predicates", api.predicateTypes)
                    .withSubclasses("properties", api.propertyTypes)
                    .withSubclasses("values", api.propertyValueTypes)
                    .build(DashRegistryData.class);
        }));
        MODEL_SERIALIZER = addSerializer(new DashSerializer<>(loader, "model", (builder) -> {
            final DashLoaderAPI api = loader.getApi();
            api.initAPI();
            return builder
                    .withSubclasses("models", api.modelTypes)
                    .build(RegistryModelData.class);
        }));
        IMAGE_SERIALIZER = addSerializer(new DashSerializer<>(loader, "image", (builder) -> builder.build(RegistryImageData.class)));
        MAPPING_SERIALIZER = addSerializer(new DashSerializer<>(loader, "mapping", builder -> builder.build(DashMappings.class)));
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
            clearSerializers();
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

    public static void clearSerializers() {
        for (DashSerializer<?> serializer : SERIALIZERS) {
            serializer.markCacheAsNull();
        }
    }


}
