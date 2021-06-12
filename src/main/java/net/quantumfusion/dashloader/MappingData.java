package net.quantumfusion.dashloader;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.data.DashMappingData;
import net.quantumfusion.dashloader.data.mappings.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MappingData {
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;
    public Map<Identifier, List<Sprite>> particlesOut;
    public Map<Identifier, List<Font>> fontsOut;
    public List<String> splashTextOut;

    public DashModelData modelData;
    public DashSpriteAtlasData spriteAtlasData;
    public DashBlockStateData blockStateData;
    public DashParticleData particleData;
    public DashFontManagerData fontManagerData;
    public DashSplashTextData splashTextData;

    public MappingData() {
    }

    public void loadData(DashMappingData data) {
        this.modelData = data.modelMappings;
        this.spriteAtlasData = data.spriteAtlasMappings;
        this.blockStateData = data.blockStateMappings;
        this.particleData = data.particleMappings;
        this.fontManagerData = data.fontMappings;
        this.splashTextData = data.splashTextMappings;
    }

    public DashMappingData createData() {
        return new DashMappingData(blockStateData, fontManagerData, modelData, particleData, splashTextData, spriteAtlasData);
    }

    public List<SpriteAtlasTexture> toUndash(DashRegistry registry) {

        final Pair<SpriteAtlasManager, List<SpriteAtlasTexture>> spriteData = spriteAtlasData.toUndash(registry);
        this.atlasManagerOut = spriteData.getKey();
        List<SpriteAtlasTexture> atlasesToRegister = new ArrayList<>(spriteData.getValue());


        particlesOut = particleData.toUndash(registry);

        splashTextOut = splashTextData.toUndash();
        stateLookupOut = blockStateData.toUndash(registry);
        modelsOut = modelData.toUndash(registry);
        fontsOut = fontManagerData.toUndash(registry);

        modelData = null;
        spriteAtlasData = null;
        blockStateData = null;
        particleData = null;
        fontManagerData = null;
        splashTextData = null;
        return atlasesToRegister;
    }

    public void setAtlasManagerOut(SpriteAtlasManager atlasManagerOut) {
        this.atlasManagerOut = atlasManagerOut;
    }

    public void setStateLookupOut(Object2IntMap<BlockState> stateLookupOut) {
        this.stateLookupOut = stateLookupOut;
    }

    public void setModelsOut(Map<Identifier, BakedModel> modelsOut) {
        this.modelsOut = modelsOut;
    }

    public void setParticlesOut(Map<Identifier, List<Sprite>> particlesOut) {
        this.particlesOut = particlesOut;
    }

    public void setFontsOut(Map<Identifier, List<Font>> fontsOut) {
        this.fontsOut = fontsOut;
    }

    public void setSplashTextOut(List<String> splashTextOut) {
        this.splashTextOut = splashTextOut;
    }

    public void setModelData(DashModelData modelData) {
        this.modelData = modelData;
    }

    public void setSpriteAtlasData(DashSpriteAtlasData spriteAtlasData) {
        this.spriteAtlasData = spriteAtlasData;
    }

    public void setBlockStateData(DashBlockStateData blockStateData) {
        this.blockStateData = blockStateData;
    }

    public void setParticleData(DashParticleData particleData) {
        this.particleData = particleData;
    }

    public void setFontManagerData(DashFontManagerData fontManagerData) {
        this.fontManagerData = fontManagerData;
    }

    public void setSplashTextData(DashSplashTextData splashTextData) {
        this.splashTextData = splashTextData;
    }
}
