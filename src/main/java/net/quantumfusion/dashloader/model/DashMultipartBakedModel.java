package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.util.Util;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.ExtraVariables;
import net.quantumfusion.dashloader.api.annotation.DashConstructor;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.api.enums.ConstructorMode;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.data.serialization.Pointer2PointerMap;
import net.quantumfusion.dashloader.mixin.accessor.MultipartBakedModelAccessor;
import net.quantumfusion.dashloader.util.UnsafeHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@DashObject(MultipartBakedModel.class)
public class DashMultipartBakedModel implements DashModel {

    //identifier baked model
    @Serialize(order = 0)
    public final Pointer2PointerMap components;

    @Serialize(order = 1)
    public final Pointer2ObjectMap<byte[]> stateCache;

    MultipartBakedModel toApply;

    public DashMultipartBakedModel(@Deserialize("components") Pointer2PointerMap components,
                                   @Deserialize("stateCache") Pointer2ObjectMap<byte[]> stateCache) {
        this.components = components;
        this.stateCache = stateCache;
    }

    @DashConstructor(ConstructorMode.FULL)
    public DashMultipartBakedModel(MultipartBakedModel model, DashRegistry registry, ExtraVariables extraVariables) {
        final Object extraVariable1 = extraVariables.getExtraVariable1();
        final Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> selectors = ((Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>) extraVariable1);
        MultipartBakedModelAccessor access = ((MultipartBakedModelAccessor) model);
        List<Pair<Predicate<BlockState>, BakedModel>> accessComponents = access.getComponents();
        final int size = accessComponents.size();
        this.components = new Pointer2PointerMap(size);
        for (int i = 0; i < size; i++) {
            final BakedModel right = accessComponents.get(i).getRight();
            components.put(registry.createPredicatePointer(selectors.getKey().get(i), selectors.getValue()), registry.createModelPointer(right));
        }


        final Map<BlockState, BitSet> stateCache = access.getStateCache();
        this.stateCache = new Pointer2ObjectMap<>(stateCache.size());
        stateCache.forEach((blockState, bitSet) -> this.stateCache.put(registry.createBlockStatePointer(blockState), bitSet.toByteArray()));

    }

    private static final Class<MultipartBakedModel> cls = MultipartBakedModel.class;

    @Override
    public MultipartBakedModel toUndash(DashRegistry registry) {
        MultipartBakedModel model = UnsafeHelper.allocateInstance(cls);
        Map<BlockState, BitSet> stateCacheOut = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());
        stateCache.forEach((entry) -> stateCacheOut.put(registry.getBlockstate(entry.key), BitSet.valueOf(entry.value)));
        ((MultipartBakedModelAccessor) model).setStateCache(stateCacheOut);
        toApply = model;
        return model;
    }

    @Override
    public void apply(DashRegistry registry) {
        List<Pair<Predicate<BlockState>, BakedModel>> componentsOut = new ArrayList<>();
        components.forEach((entry) -> componentsOut.add(Pair.of(registry.getPredicate(entry.key), registry.getModel(entry.value))));
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
