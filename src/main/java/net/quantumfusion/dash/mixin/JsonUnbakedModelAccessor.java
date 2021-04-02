package net.quantumfusion.dash.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelAccessor {

    @Accessor("textureMap")
    Map<String, Either<SpriteIdentifier, String>> getTextureMapD();

    @Accessor("parentId")
    Identifier getParentIdD();

    @Accessor("guiLight")
    JsonUnbakedModel.GuiLight getGuiLightD();

    @Accessor("transformations")
    ModelTransformation getTransformationsD();

    @Accessor("elements")
    List<ModelElement> getElementsD();

    @Accessor("ambientOcclusion")
    boolean getAmbientOcclusionD();

    @Accessor("overrides")
    List<ModelOverride> getOverridesD();

}
