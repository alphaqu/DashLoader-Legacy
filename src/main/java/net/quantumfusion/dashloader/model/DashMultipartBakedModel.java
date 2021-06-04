package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.util.Util;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.MultipartBakedModelAccessor;
import net.quantumfusion.dashloader.util.PairMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashModel {

    //identifier baked model
    @Serialize(order = 0)
    public PairMap<Integer, Integer> components;

    @Serialize(order = 1)
    public PairMap<Integer, byte[]> stateCache;

    MultipartBakedModel toApply;

    public DashMultipartBakedModel(@Deserialize("components") PairMap<Integer, Integer> components,
                                   @Deserialize("stateCache") PairMap<Integer, byte[]> stateCache) {
        this.components = components;
        this.stateCache = stateCache;
    }

    public DashMultipartBakedModel(MultipartBakedModel model, DashRegistry registry, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> selectors) {
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor) model);
        List<Pair<Predicate<BlockState>, BakedModel>> accessComponents = access.getComponents();
        final int size = accessComponents.size();
        this.components = new PairMap<>(size);
        for (int i = 0; i < size; i++) {
            final BakedModel right = accessComponents.get(i).getRight();
            components.put(registry.createPredicatePointer(selectors.getKey().get(i), selectors.getValue()), registry.createModelPointer(right));
        }
        final Map<BlockState, BitSet> stateCache = access.getStateCache();
        this.stateCache = new PairMap<>(stateCache.size());
        stateCache.forEach((blockState, bitSet) -> this.stateCache.put(registry.createBlockStatePointer(blockState), bitSet.toByteArray()));

    }

    private static final Class<MultipartBakedModel> cls = MultipartBakedModel.class;

    @Override
    public BakedModel toUndash(DashRegistry registry) {
        MultipartBakedModel model = Unsafe.allocateInstance(cls);
        Map<BlockState, BitSet> stateCacheOut = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());
        stateCache.forEach((blockstatePointer, bitSet) -> stateCacheOut.put(registry.getBlockstate(blockstatePointer), BitSet.valueOf(bitSet)));
        ((MultipartBakedModelAccessor) model).setStateCache(stateCacheOut);
        toApply = model;
        return model;
    }

    @Override
    public void apply(DashRegistry registry) {
        List<Pair<Predicate<BlockState>, BakedModel>> componentsOut = new ArrayList<>();
        components.forEach((dashPredicate, integer) -> componentsOut.add(Pair.of(registry.getPredicate(dashPredicate), registry.getModel(integer))));
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
    public int getStage() {
        return 2;
    }
}
