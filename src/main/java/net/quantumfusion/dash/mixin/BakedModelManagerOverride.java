package net.quantumfusion.dash.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.models.DashModelLoader;
import net.quantumfusion.dash.cache.atlas.DashSpriteAtlasManager;
import net.quantumfusion.dash.cache.blockstates.DashBlockState;
import net.quantumfusion.dash.cache.models.DashBakedModel;
import net.quantumfusion.dash.cache.DashIdentifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Mixin(BakedModelManager.class)
public class BakedModelManagerOverride {


    @Shadow @Final private BlockColors colorMap;

    @Shadow private int mipmap;

    @Shadow @Nullable private SpriteAtlasManager atlasManager;

    private static DashModelLoader loader;
    private static Map<Identifier, BakedModel> modelsCachetemp;


    @Shadow @Final private TextureManager textureManager;

    @Shadow private Map<Identifier, BakedModel> models;

    @Shadow private Object2IntMap<BlockState> stateLookup;

    @Shadow private BakedModel missingModel;

    @Shadow @Final private BlockModels blockModelCache;

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/render/model/ModelLoader;",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<ModelLoader> cir) {
        profiler.startTick();
        ModelLoader modelLoader = null;
        if(loader == null){
            modelLoader = new ModelLoader(resourceManager, this.colorMap, profiler, this.mipmap);
        }
        profiler.endTick();
        cir.setReturnValue(modelLoader);

    }

    @Inject(method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        profiler.startTick();
        profiler.push("upload");

        if(modelLoader != null){
            System.out.println("Creating model cache");
            //serialization
            this.atlasManager = modelLoader.upload(this.textureManager, profiler);
            this.models = modelLoader.getBakedModelMap();
            this.stateLookup = modelLoader.getStateLookup();
            loader = new DashModelLoader(atlasManager,stateLookup,models);
        } else {
            loader.load();
            atlasManager = loader.atlasManagerOut;
            stateLookup = loader.stateLookupOut;
            models = loader.modelsOut;
        }
        stateLookup = new Object2IntOpenHashMap<>();

        this.missingModel = this.models.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockModelCache.reload();
        profiler.pop();
        profiler.endTick();
        ci.cancel();
    }

}
