package net.quantumfusion.dashloader.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.util.DashCacheState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.client.render.block.BlockModels.getModelId;

@Mixin(BakedModelManager.class)
public class BakedModelManagerOverride {

    @Shadow
    @Final
    private BlockColors colorMap;
    @Shadow
    private int mipmapLevels;
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

    @Inject(method = "prepare",
            at = @At(value = "HEAD"), cancellable = true)
    private void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<ModelLoader> cir) {
        profiler.startTick();
        ModelLoader modelLoader;
        if (DashLoader.getInstance().state != DashCacheState.LOADED) {
            modelLoader = new ModelLoader(resourceManager, this.colorMap, profiler, this.mipmapLevels);
        } else {
            //hipidy hopedy this is now dashes property
            modelLoader = null;
        }
        profiler.endTick();
        cir.setReturnValue(modelLoader);

    }

    @Inject(method = "apply",
            at = @At(value = "HEAD"), cancellable = true)
    private void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        profiler.startTick();
        profiler.push("upload");
        DashLoader loader = DashLoader.getInstance();
        if (loader.state != DashCacheState.LOADED) {
            DashLoader.LOGGER.info("DashLoader not loaded, Initializing minecraft ModelLoader to create assets for caching.");
            //serialization
            this.atlasManager = modelLoader.upload(this.textureManager, profiler);
            this.models = modelLoader.getBakedModelMap();
            this.stateLookup = modelLoader.getStateLookup();
            loader.addBakedModelAssets(atlasManager, stateLookup, models);
            this.missingModel = this.models.get(ModelLoader.MISSING_ID);
            this.blockModelCache.reload();

        } else {
            //cache go brr
            DashLoader.LOGGER.info("Starting apply stage.");
            loader.applyDashCache(textureManager);
            this.atlasManager = loader.getAtlasManagerOut();
            this.models = loader.getModelsOut();
            this.stateLookup = loader.getStateLookupOut();
            this.missingModel = this.models.get(ModelLoader.MISSING_ID);
            Map<BlockState, BakedModel> modelsOut = new ConcurrentHashMap<>();
            Registry.BLOCK.stream().parallel().forEach(block ->
                    block.getStateManager().getStates().parallelStream().forEach((blockState) ->
                            modelsOut.put(blockState, ((BakedModelManager) (Object) this).getModel(getModelId(blockState)))));
            ((BlockModelsAccessor) blockModelCache).setModels(modelsOut);
        }
        profiler.swap("cache");
        profiler.pop();
        profiler.endTick();
        ci.cancel();
    }

}
