package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.util.Util;
import net.quantumfusion.dash.cache.DashModelLoader;
import net.quantumfusion.dash.mixin.MultipartBakedModelAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashModel,DashBakedModel {
    //identifier baked model
    @Serialize(order = 0)
    @SerializeSubclasses(path = {0},extraSubclassesId = "models")
    public final List<DashBakedModel> components;


    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Integer, byte[]> stateCache;

    public DashMultipartBakedModel(@Deserialize("components") List<DashBakedModel> components,
                                   @Deserialize("stateCache")  Map<Integer, byte[]> stateCache) {
        this.components = components;
        this.stateCache = stateCache;
    }

    public DashMultipartBakedModel(MultipartBakedModel model, DashModelLoader loader) {
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor) model);
        this.components = new ArrayList<>();
        stateCache = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());
        access.getComponents().forEach(predicateBakedModelPair -> {
            components.add((DashBakedModel) loader.convertSimpleModel(predicateBakedModelPair.getRight()));
        });
        access.getStateCache().forEach((blockState, bitSet) -> stateCache.put(loader.registry.createBlockStatePointer(blockState), bitSet.toByteArray()));

    }

    @Override
    public BakedModel toUndash(DashModelLoader loader) {
        List<Pair<Predicate<BlockState>, BakedModel>> componentsOut = new ArrayList<>();
        for (DashBakedModel componentRaw : components) {
            DashModel component = (DashModel) componentRaw;
            componentsOut.add(Pair.of((blockState -> true), component.toUndash(loader)));
        }
        MultipartBakedModel model = new MultipartBakedModel(componentsOut);
        Map<BlockState, BitSet> stateCacheOut = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());
        stateCache.forEach((blockstatePointer, bitSet) -> stateCacheOut.put(loader.registry.getBlockstate(blockstatePointer), BitSet.valueOf(bitSet)));
        ((MultipartBakedModelAccessor) model).setStateCache(stateCacheOut);
        return model;
    }
}
