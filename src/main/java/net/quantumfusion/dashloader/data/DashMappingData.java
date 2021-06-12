package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.data.mappings.*;

public class DashMappingData {
    @Serialize(order = 0)
    public final DashBlockStateData blockStateMappings;
    @Serialize(order = 1)
    public final DashFontManagerData fontMappings;
    @Serialize(order = 2)
    public final DashModelData modelMappings;
    @Serialize(order = 3)
    public final DashParticleData particleMappings;
    @Serialize(order = 4)
    public final DashSplashTextData splashTextMappings;
    @Serialize(order = 5)
    public final DashSpriteAtlasData spriteAtlasMappings;


    public DashMappingData(@Deserialize("blockStateMappings") DashBlockStateData blockStateMappings,
                           @Deserialize("fontMappings") DashFontManagerData fontMappings,
                           @Deserialize("modelMappings") DashModelData modelMappings,
                           @Deserialize("particleMappings") DashParticleData particleMappings,
                           @Deserialize("splashTextMappings") DashSplashTextData splashTextMappings,
                           @Deserialize("spriteAtlasMappings") DashSpriteAtlasData spriteAtlasMappings) {
        this.blockStateMappings = blockStateMappings;
        this.fontMappings = fontMappings;
        this.modelMappings = modelMappings;
        this.particleMappings = particleMappings;
        this.splashTextMappings = splashTextMappings;
        this.spriteAtlasMappings = spriteAtlasMappings;
    }

}
