package net.quantumfusion.dashloader.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.Font;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.atlas.DashExtraAtlasData;
import net.quantumfusion.dashloader.cache.atlas.DashSpriteAtlasData;
import net.quantumfusion.dashloader.cache.blockstates.DashBlockStateData;
import net.quantumfusion.dashloader.cache.font.DashFontManagerData;
import net.quantumfusion.dashloader.cache.misc.DashParticleData;
import net.quantumfusion.dashloader.cache.models.DashModelData;
import net.quantumfusion.dashloader.misc.DashSplashTextData;
import net.quantumfusion.dashloader.mixin.SpriteAtlasManagerAccessor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MappingData {
    private static final Logger LOGGER = LogManager.getLogger();
    public SpriteAtlasManager atlasManagerOut;
    public Object2IntMap<BlockState> stateLookupOut;
    public Map<Identifier, BakedModel> modelsOut;
    public Map<Identifier, List<Sprite>> particlesOut;
    public Map<Identifier, List<Font>> fontsOut;
    public List<String> splashTextOut;

    @Serialize(order = 0)
    public DashModelData modelData;

    @Serialize(order = 1)
    public DashSpriteAtlasData spriteAtlasData;

    @Serialize(order = 2)
    public DashBlockStateData blockStateData;

    @Serialize(order = 3)
    public DashParticleData particleData;

    @Serialize(order = 4)
    public DashExtraAtlasData extraAtlasData;

    @Serialize(order = 5)
    public DashFontManagerData fontManagerData;

    @Serialize(order = 6)
    public DashSplashTextData splashTextData;

    public MappingData() {
    }

    @SuppressWarnings("unused")//active j uses this so stop complaining intelij.
    public MappingData(@Deserialize("modelData") DashModelData modelData,
                       @Deserialize("spriteAtlasData") DashSpriteAtlasData spriteAtlasData,
                       @Deserialize("blockStateData") DashBlockStateData blockStateData,
                       @Deserialize("particleData") DashParticleData particleData,
                       @Deserialize("extraAtlasData") DashExtraAtlasData extraAtlasData,
                       @Deserialize("fontManagerData") DashFontManagerData fontManagerData,
                       @Deserialize("splashTextData") DashSplashTextData splashTextData
    ) {
        this.modelData = modelData;
        this.spriteAtlasData = spriteAtlasData;
        this.blockStateData = blockStateData;
        this.particleData = particleData;
        this.extraAtlasData = extraAtlasData;
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

    public void setExtraAtlasData(DashExtraAtlasData extraAtlasData) {
        this.extraAtlasData = extraAtlasData;
    }

    public void setFontManagerData(DashFontManagerData fontManagerData) {
        this.fontManagerData = fontManagerData;
    }

    public void setSplashTextData(DashSplashTextData splashTextData) {
        this.splashTextData = splashTextData;
    }

    public List<SpriteAtlasTexture> toUndash(DashRegistry registry) {
        List<SpriteAtlasTexture> atlasesToRegister = new ArrayList<>();
        List<Runnable> tasks = new ArrayList<>();
        tasks.add(() -> splashTextOut = splashTextData.toUndash());

        tasks.add(() -> {
            atlasManagerOut = spriteAtlasData.toUndash(registry);
            atlasesToRegister.addAll((((SpriteAtlasManagerAccessor) atlasManagerOut).getAtlases().values()));
        });

        tasks.add(() -> stateLookupOut = blockStateData.toUndash(registry));

        tasks.add(() -> {
            Pair<Map<Identifier, List<Sprite>>, SpriteAtlasTexture> outParticle = particleData.toUndash(registry);
            particlesOut = outParticle.getLeft();
            atlasesToRegister.add(outParticle.getValue());
        });

        tasks.add(() -> modelsOut = modelData.toUndash(registry));

        tasks.add(() -> atlasesToRegister.addAll(extraAtlasData.toUndash(registry)));

        tasks.add(() -> fontsOut = fontManagerData.toUndash(registry));

        tasks.parallelStream().forEach(Runnable::run);
        modelData = null;
        spriteAtlasData = null;
        blockStateData = null;
        particleData = null;
        extraAtlasData = null;
        fontManagerData = null;
        splashTextData = null;
        return atlasesToRegister;
    }


}
