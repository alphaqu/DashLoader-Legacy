package net.quantumfusion.dash.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public interface MultipartBakedModelAccessor {

    @Accessor
    List<Pair<Predicate<BlockState>, BakedModel>> getComponents();

    @Accessor
    Map<BlockState, BitSet> getStateCache();


    @Accessor
    void setStateCache(Map<BlockState, BitSet> stateBitSetMap);
}
