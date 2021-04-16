package net.quantumfusion.dash.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.cache.DashCacheState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.minecraft.client.render.block.BlockModels.getModelId;

@Mixin(BakedModelManager.class)
public class BakedModelManagerOverride {

    @Shadow
    @Final
    private BlockColors colorMap;
    @Shadow
    private int mipmap;
    @Shadow
    @Nullable
    private SpriteAtlasManager atlasManager;
    @Shadow
    @Final
    private TextureManager textureManager;

    @Shadow
    private Map<Identifier, BakedModel> models;

    @Shadow
    private Object2IntMap<BlockState> stateLookup;

    @Shadow
    private BakedModel missingModel;

    @Shadow
    @Final
    private BlockModels blockModelCache;

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/render/model/ModelLoader;",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<ModelLoader> cir) {
        profiler.startTick();
        ModelLoader modelLoader;
        if (Dash.loader.state != DashCacheState.LOADED) {
            modelLoader = new ModelLoader(resourceManager, this.colorMap, profiler, this.mipmap);
        } else {
            //hipidy hopedy this is now dashes property
            modelLoader = null;
        }
        profiler.endTick();
        cir.setReturnValue(modelLoader);

    }

    @Inject(method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        profiler.startTick();
        profiler.push("upload");
        DashCache loader = Dash.loader;
        if (Dash.loader.state != DashCacheState.LOADED) {
            //serialization
            this.atlasManager = modelLoader.upload(this.textureManager, profiler);
            this.models = modelLoader.getBakedModelMap();
            this.stateLookup = modelLoader.getStateLookup();
            Dash.loader.addBakedModelAssets(atlasManager, stateLookup, models);

        } else {
            //cache go brr
            loader.load(textureManager);
            this.atlasManager = loader.atlasManagerOut;
            this.models = loader.modelsOut;
            this.stateLookup = loader.stateLookupOut;
        }
        this.missingModel = this.models.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockModelCache.reload();
        profiler.pop();
        profiler.endTick();
        ci.cancel();
    }

}
