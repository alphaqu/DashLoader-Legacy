package net.quantumfusion.dash.cache.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.util.Util;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;
import net.quantumfusion.dash.mixin.MultipartBakedModelAccessor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashBakedModel {
    //identifier baked model
    private final List<DashBakedModel> components;


    Map<DashBlockState, BitSet> stateCache = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());


    public DashMultipartBakedModel(MultipartBakedModel model, DashModelLoader loader) {
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor)model);
        this.components = new ArrayList<>();
        access.getComponents().forEach(predicateBakedModelPair -> {
            components.add(loader.convertSimpleModel(predicateBakedModelPair.getRight()));
        });
        access.getStateCache().forEach((blockState, bitSet) -> stateCache.put(new DashBlockState(blockState),bitSet));

    }

    @Override
    public BakedModel toUndash(DashModelLoader loader) {
        List<Pair<Predicate<BlockState>, BakedModel>> componentsOut = new ArrayList<>();
        components.forEach(model -> componentsOut.add(Pair.of((blockState -> true), model.toUndash(loader))));
        MultipartBakedModel model = new MultipartBakedModel(componentsOut);
        Map<BlockState, BitSet> stateCacheOut = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());
        stateCache.forEach((dashBlockState, bitSet) -> stateCacheOut.put(dashBlockState.toUndash(), bitSet));
        ((MultipartBakedModelAccessor)model).setStateCache(stateCacheOut);
        return model;
    }
}
