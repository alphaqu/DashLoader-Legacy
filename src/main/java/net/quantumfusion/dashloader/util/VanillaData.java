package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.image.DashSpriteAtlasTextureData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaData {
    private final List<SpriteAtlasTexture> extraAtlases = new ArrayList<>();
    private final Map<SpriteAtlasTexture, DashSpriteAtlasTextureData> atlasData = new HashMap<>();
    private final Map<BakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>> multipartData = new HashMap<>();

    private SpriteAtlasManager atlasManager;
    private Object2IntMap<BlockState> stateLookup;
    private Map<Identifier, BakedModel> models;
    private Map<Identifier, List<Sprite>> particles;
    private Map<Identifier, List<Font>> fonts;
    private List<String> splashText;


    public VanillaData() {
    }

    public void loadCacheData(SpriteAtlasManager atlasManager,
                              Object2IntMap<BlockState> stateLookup,
                              Map<Identifier, BakedModel> models,
                              Map<Identifier, List<Sprite>> particles,
                              Map<Identifier, List<Font>> fonts,
                              List<String> splashText) {
        this.atlasManager = atlasManager;
        this.stateLookup = stateLookup;
        this.models = models;
        this.particles = particles;
        this.fonts = fonts;
        this.splashText = splashText;
    }

    public void addExtraAtlasAssets(SpriteAtlasTexture atlas) {
        extraAtlases.add(atlas);
    }

    public void addAtlasData(SpriteAtlasTexture atlas, DashSpriteAtlasTextureData data) {
        atlasData.put(atlas, data);
    }

    public void addMultipartModelPredicate(MultipartBakedModel model, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> data) {
        multipartData.put(model, data);
    }

    public void setBakedModelAssets(SpriteAtlasManager atlasManager,
                                    Object2IntMap<BlockState> stateLookup,
                                    Map<Identifier, BakedModel> models) {
        this.atlasManager = atlasManager;
        this.models = models;
        this.stateLookup = stateLookup;
    }

    public void setFontAssets(Map<Identifier, List<Font>> fonts) {
        this.fonts = fonts;
    }

    public void setParticleManagerAssets(Map<Identifier, ParticleManager.SimpleSpriteProvider> particles, SpriteAtlasTexture atlas) {
        this.particles = new HashMap<>();
        particles.forEach((identifier, simpleSpriteProvider) -> this.particles.put(identifier, ((ParticleManagerAccessor.SimpleSpriteProviderAccessor) particles).getSprites()));
        addExtraAtlasAssets(atlas);
    }

    public void setSplashTextAssets(List<String> splashText) {
        this.splashText = splashText;
    }

    public SpriteAtlasManager getAtlasManager() {
        return atlasManager;
    }


    public Object2IntMap<BlockState> getStateLookup() {
        return stateLookup;
    }


    public Map<Identifier, BakedModel> getModels() {
        return models;
    }

    public Map<Identifier, List<Sprite>> getParticles() {
        return particles;
    }

    public Map<Identifier, List<Font>> getFonts() {
        return fonts;
    }

    public List<String> getSplashText() {
        return splashText;
    }

    public DashSpriteAtlasTextureData getAtlasData(SpriteAtlasTexture atlasTexture) {
        return atlasData.get(atlasTexture);
    }

    public Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>> getModelData(BakedModel model) {
        return multipartData.get(model);
    }

    public List<SpriteAtlasTexture> getExtraAtlases() {
        return extraAtlases;
    }

}
