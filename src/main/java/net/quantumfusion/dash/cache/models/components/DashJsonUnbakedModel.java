package net.quantumfusion.dash.cache.models.components;

import com.mojang.datafixers.util.Either;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.mixin.JsonUnbakedModelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashJsonUnbakedModel {

    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public final Map<String, String> textureMap;


    @Serialize(order = 1)
    @SerializeNullable()
    @Nullable
    public final String parentId;

    @Serialize(order = 2)
    public final List<DashModelElement> elements;


    @Serialize(order = 3)
    @SerializeNullable()
    @Nullable
    public final String guiLight;

    @Serialize(order = 4)
    public final boolean ambientOcclusion;

    @Serialize(order = 5)
    public final List<DashModelOverride> overrides;


    @Serialize(order = 6)
    @SerializeNullable()
    @Nullable
    public final DashModelTransformation transformations;

    @Serialize(order = 7)
    public final DashIdentifier id;

    public DashJsonUnbakedModel(@Deserialize("textureMap") Map<String, String> textureMap,
                                @Deserialize("parentId") @Nullable String parentId,
                                @Deserialize("elements") List<DashModelElement> elements,
                                @Deserialize("guiLight") @Nullable String guiLight,
                                @Deserialize("ambientOcclusion") boolean ambientOcclusion,
                                @Deserialize("overrides") List<DashModelOverride> overrides,
                                @Deserialize("transformations") @Nullable DashModelTransformation transformations,
                                @Deserialize("id") DashIdentifier id
    ) {
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.elements = elements;
        this.guiLight = guiLight;
        this.ambientOcclusion = ambientOcclusion;
        this.overrides = overrides;
        this.transformations = transformations;
        this.id = id;
    }

    public static DashJsonUnbakedModel create(JsonUnbakedModel jsonUnbakedModel, Identifier id) {
        List<DashModelElement> elementsOut = new ArrayList<>();
        JsonUnbakedModelAccessor jsonUnbakedModelMixin = ((JsonUnbakedModelAccessor) jsonUnbakedModel);
        jsonUnbakedModelMixin.getElementsD().forEach(modelElement -> elementsOut.add(new DashModelElement(modelElement)));
        JsonUnbakedModel.GuiLight gui = jsonUnbakedModelMixin.getGuiLightD();
        Map<String, String> textureMapOut = new HashMap<>();
        jsonUnbakedModelMixin.getTextureMapD().forEach((name, sprite) -> {
            if (sprite.left().isPresent()) {
                SpriteIdentifier ido = sprite.left().get();
                textureMapOut.put(name, ido.getTextureId().toString() + ";" + ido.getAtlasId().toString());
            } else if (sprite.right().isPresent()) {
                textureMapOut.put(name, sprite.right().get());
            }
        });
        Identifier parent = jsonUnbakedModelMixin.getParentIdD();
        List<DashModelOverride> overridesOut = new ArrayList<>();
        jsonUnbakedModelMixin.getOverridesD().forEach(modelOverride -> overridesOut.add(new DashModelOverride(modelOverride)));
        return new DashJsonUnbakedModel(
                textureMapOut,
                parent == null ? null : parent.toString(),
                elementsOut,
                gui == null ? null : gui.name(),
                jsonUnbakedModelMixin.getAmbientOcclusionD(),
                overridesOut,
                jsonUnbakedModelMixin.getTransformationsD().equals(ModelTransformation.NONE) ? null : new DashModelTransformation(jsonUnbakedModelMixin.getTransformationsD()),
                new DashIdentifier(id));
    }

    public JsonUnbakedModel toUndash() {
        //elements
        List<ModelElement> unElement = new ArrayList<>();
        elements.forEach(dashModelElement -> unElement.add(dashModelElement.unDash()));
        //texturemap
        Map<String, Either<SpriteIdentifier, String>> unTextureMap = new HashMap<>();
        textureMap.forEach((s, s2) -> {
            if (s2.contains(";")) {
                unTextureMap.put(s, Either.left(new SpriteIdentifier(new Identifier(s2.split(";")[1]), new Identifier(s2.split(";")[0]))));
            } else {
                unTextureMap.put(s, Either.right(s2));
            }
        });
        //overrides
        List<ModelOverride> unOverrides = new ArrayList<>();
        overrides.forEach(dashModelOverride -> unOverrides.add(dashModelOverride.toUndash()));
        //creation
        return new JsonUnbakedModel(
                parentId == null ? null : new Identifier(parentId),
                unElement,
                unTextureMap,
                ambientOcclusion,
                guiLight == null ? null : JsonUnbakedModel.GuiLight.valueOf(guiLight),
                transformations == null ? ModelTransformation.NONE : transformations.toUndash(),
                unOverrides
        );
    }

}
