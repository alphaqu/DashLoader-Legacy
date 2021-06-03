package net.quantumfusion.dashloader;

import io.activej.serializer.annotations.Deserialize;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.atlas.DashSpriteAtlasData;
import net.quantumfusion.dashloader.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.font.DashFontManagerData;
import net.quantumfusion.dashloader.misc.DashParticleData;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.models.DashModelData;
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

    @SuppressWarnings("unused")//active j uses this so stop complaining intelij.
    public MappingData(@Deserialize("modelData") DashModelData modelData,
                       @Deserialize("spriteAtlasData") DashSpriteAtlasData spriteAtlasData,
                       @Deserialize("blockStateData") DashBlockStateData blockStateData,
                       @Deserialize("particleData") DashParticleData particleData,
                       @Deserialize("fontManagerData") DashFontManagerData fontManagerData,
                       @Deserialize("splashTextData") DashSplashTextData splashTextData
    ) {
        this.modelData = modelData;
        this.spriteAtlasData = spriteAtlasData;
        this.blockStateData = blockStateData;
        this.particleData = particleData;
        this.fontManagerData = fontManagerData;
        this.splashTextData = splashTextData;
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

    public List<SpriteAtlasTexture> toUndash(DashRegistry registry) {
        List<SpriteAtlasTexture> atlasesToRegister = new ArrayList<>();

        final Pair<SpriteAtlasManager, List<SpriteAtlasTexture>> spriteData = spriteAtlasData.toUndash(registry);
        this.atlasManagerOut = spriteData.getKey();
        atlasesToRegister.addAll(spriteData.getValue());


        Pair<Map<Identifier, List<Sprite>>, SpriteAtlasTexture> outParticle = particleData.toUndash(registry);
        particlesOut = outParticle.getLeft();
        atlasesToRegister.add(outParticle.getValue());

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


}
