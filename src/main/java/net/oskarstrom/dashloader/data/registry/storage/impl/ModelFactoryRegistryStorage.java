package net.oskarstrom.dashloader.data.registry.storage.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.model.BakedModel;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.DashDataType;
import net.oskarstrom.dashloader.data.registry.storage.FactoryRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.model.DashModel;
import net.oskarstrom.dashloader.util.ThreadHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModelFactoryRegistryStorage extends FactoryRegistryStorage<BakedModel, DashModel> {
    @Nullable
    private List<Int2ObjectMap<DashModel>> modelsToDeserialize;

    public ModelFactoryRegistryStorage(Class<?> originalObjectClass, DashRegistry registry, DashDataType type) {
        super(originalObjectClass, registry, type);
    }

    public Int2ObjectMap<DashModel> getRegistryStorage() {
        return registryStorage;
    }

    public List<Int2ObjectMap<DashModel>> getModelsToDeserialize() {
        if (modelsToDeserialize == null) {
            throw new NullPointerException("Models not populated");
        }
        return modelsToDeserialize;
    }

    @Override
    public void toUndash(Logger logger) {
        final short[] currentStage = {0};
        if (modelsToDeserialize == null) {
            throw new NullPointerException("Models not populated");
        }
        modelsToDeserialize.forEach(modelCategory -> {
            logger.info("Loading " + modelCategory.size() + " Models " + "(" + currentStage[0] + ")");
            registryStorageUndashed.putAll(ThreadHelper.execParallel(modelCategory, registry));
            currentStage[0]++;
        });
    }

    @Override
    public int getSize() {
        int models = 0;
        if (modelsToDeserialize == null) {
            throw new NullPointerException("Models not populated");
        }
        for (Int2ObjectMap<DashModel> dashModelInt2ObjectMap : modelsToDeserialize) {
            models += dashModelInt2ObjectMap.size();
        }
        return models;
    }

    public void populateModels(List<Int2ObjectMap<DashModel>> models) {
        modelsToDeserialize = models;
    }


    @Override
    public void populate(Pointer2ObjectMap<DashModel> dashables) {
        throw new UnsupportedOperationException();
    }
}
