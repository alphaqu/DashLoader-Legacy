package net.quantumfusion.dash.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ModelOverrideList.class)
public interface ModelOverideListAccessor {


    @Accessor("overrides")
    List<ModelOverride> getOverrides();

    @Accessor("overrides")
    void setOverrides(List<ModelOverride> overrides);

    @Accessor("models")
    List<BakedModel> getModels();

    @Accessor("models")
    void setModels(List<BakedModel> models);

    @Invoker("<init>")
    static ModelOverrideList newModelOverrideList() {
        throw new AssertionError();
    }
}
