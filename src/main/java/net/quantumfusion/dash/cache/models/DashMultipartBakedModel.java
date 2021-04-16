package net.quantumfusion.dash.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.util.Util;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.DashRegistry;
import net.quantumfusion.dash.mixin.MultipartBakedModelAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashModel, DashBakedModel {

    //identifier baked model
    @Serialize(order = 0)
    public List<Integer> components;


    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeNullable(path = {0})
    @SerializeNullable(path = {1})
    public Map<Integer, byte[]> stateCache;

    MultipartBakedModel toApply;

    public DashMultipartBakedModel(@Deserialize("components") List<Integer> components,
                                   @Deserialize("stateCache") Map<Integer, byte[]> stateCache) {
        this.components = components;
        this.stateCache = stateCache;
    }

    public DashMultipartBakedModel() {
    }

    public DashMultipartBakedModel(MultipartBakedModel model, DashRegistry registry) {
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor) model);
        this.components = new ArrayList<>();
        stateCache = new HashMap<>();
        access.getComponents().forEach(predicateBakedModelPair -> {
            components.add(registry.createModelPointer(predicateBakedModelPair.getRight()));
        });
        access.getStateCache().forEach((blockState, bitSet) -> stateCache.put(registry.createBlockStatePointer(blockState), bitSet.toByteArray()));

    }

    @Override
    public BakedModel toUndash(DashRegistry registry) {

        try {
            MultipartBakedModel model = (MultipartBakedModel) Dash.getUnsafe().allocateInstance(MultipartBakedModel.class);
            Map<BlockState, BitSet> stateCacheOut = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());
            stateCache.forEach((blockstatePointer, bitSet) -> stateCacheOut.put(registry.getBlockstate(blockstatePointer), BitSet.valueOf(bitSet)));
            ((MultipartBakedModelAccessor) model).setStateCache(stateCacheOut);
            toApply = model;
            return model;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void apply(DashRegistry registry) {
        List<Pair<Predicate<BlockState>, BakedModel>> componentsOut = new ArrayList<>();
        components.forEach(integer -> componentsOut.add(Pair.of((blockState -> true), registry.getModel(integer))));
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor) toApply);
        BakedModel bakedModel = (BakedModel) ((Pair) componentsOut.iterator().next()).getRight();
        access.setComponents(componentsOut);
        access.setAmbientOcclusion(bakedModel.useAmbientOcclusion());
        access.setDepthGui(bakedModel.hasDepth());
        access.setSideLit(bakedModel.isSideLit());
        access.setSprite(bakedModel.getSprite());
        access.setTransformations(bakedModel.getTransformation());
        access.setItemPropertyOverrides(bakedModel.getOverrides());
    }

    @Override
    public DashModel toDash(BakedModel model, DashRegistry registry) {
        return new DashMultipartBakedModel((MultipartBakedModel) model, registry);
    }

    @Override
    public ModelStage getStage() {
        return ModelStage.ADVANCED;
    }
}
