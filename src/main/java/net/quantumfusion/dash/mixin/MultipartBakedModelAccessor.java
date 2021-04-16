package net.quantumfusion.dash.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
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

    @Accessor
    void setComponents(List<Pair<Predicate<BlockState>, BakedModel>> components);

    @Accessor
    void setAmbientOcclusion(boolean ambientOcclusion);

    @Accessor
    void setDepthGui(boolean depthGui);

    @Accessor
    void setSideLit(boolean sideLit);

    @Accessor
    void setSprite(Sprite sprite);

    @Accessor
    void setTransformations(ModelTransformation transformations);

    @Accessor
    void setItemPropertyOverrides(ModelOverrideList itemPropertyOverrides);
}
