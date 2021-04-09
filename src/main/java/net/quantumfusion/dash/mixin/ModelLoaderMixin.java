package net.quantumfusion.dash.mixin;

import io.activej.serializer.stream.StreamOutput;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.models.DashJsonUnbakedModel;
import net.quantumfusion.dash.util.StringHelper;
import org.apache.commons.compress.utils.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static net.minecraft.client.render.model.ModelLoader.BLOCK_ENTITY_MARKER;


@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow
    @Final
    private ResourceManager resourceManager;

    @Shadow
    @Final
    private static Map<String, String> BUILTIN_MODEL_DEFINITIONS;


    @Shadow
    @Final
    public static JsonUnbakedModel GENERATION_MARKER;

    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> unbakedModels;


    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> modelsToBake;



    @Shadow @Final public static SpriteIdentifier SHIELD_BASE;

    @Shadow public abstract UnbakedModel getOrLoadModel(Identifier id);

    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void loadModelOverride(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) throws IOException {
        if(Dash.modelCache){
            cir.setReturnValue(dash(BUILTIN_MODEL_DEFINITIONS, id, resourceManager));
        }

    }


    private static JsonUnbakedModel dash(Map<String, String> BUILTIN_MODEL_DEFINITIONS, Identifier id, ResourceManager resourceManager) throws IOException {
        if (Dash.jsonModelMap.containsKey(id)) {
            return Dash.jsonModelMap.get(id);
        } else {
            Reader reader = null;
            Resource resource = null;

            JsonUnbakedModel jsonUnbakedModel;
            try {
                String string = id.getPath();
                if (!"builtin/generated".equals(string)) {
                    if ("builtin/entity".equals(string)) {
                        jsonUnbakedModel = BLOCK_ENTITY_MARKER;
                        return jsonUnbakedModel;
                    }

                    if (string.startsWith("builtin/")) {
                        String string2 = string.substring("builtin/".length());
                        String string3 = BUILTIN_MODEL_DEFINITIONS.get(string2);
                        if (string3 == null) {
                            throw new FileNotFoundException(id.toString());
                        }

                        reader = new StringReader(string3);
                    } else {
                        resource = resourceManager.getResource(new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
                        reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    }

                    JsonUnbakedModel model = JsonUnbakedModel.deserialize(reader);
                    try {
                        String dashId = StringHelper.idToFile(id);
                        StreamOutput output = StreamOutput.create(Files.newOutputStream(Dash.config.resolve("dash/models/" + dashId + ".activej"), StandardOpenOption.CREATE, StandardOpenOption.WRITE));
                        output.serialize(Dash.jsonModelSerializer, DashJsonUnbakedModel.create(model, id));
                        output.flush();
                        System.out.println("Created model: " + dashId);
                    } catch (Exception e) {
                        System.out.println("eroor");
                    }
                    jsonUnbakedModel = model;
                    jsonUnbakedModel.id = id.toString();
                    return jsonUnbakedModel;
                }

                jsonUnbakedModel = GENERATION_MARKER;
            } finally {
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);
            }

            return jsonUnbakedModel;
        }
    }


}
