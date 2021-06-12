package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.data.mappings.*;
import net.quantumfusion.dashloader.data.registry.*;

public class DashCacheData {
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
    @Serialize(order = 6)
    public final RegistryBlockStateData blockStateRegistryData;
    @Serialize(order = 7)
    public final RegistryFontData fontRegistryData;
    @Serialize(order = 8)
    public final RegistryIdentifierData identifierRegistryData;
    @Serialize(order = 9)
    public final RegistryImageData imageRegistryData;
    @Serialize(order = 10)
    public final RegistryModelData modelRegistryData;
    @Serialize(order = 11)
    public final RegistryPropertyData propertyRegistryData;
    @Serialize(order = 12)
    public final RegistryPropertyValueData propertyValueRegistryData;
    @Serialize(order = 13)
    public final RegistrySpriteData spriteRegistryData;
    @Serialize(order = 14)
    public final RegistryPredicateData predicateRegistryData;


    public DashCacheData(@Deserialize("blockStateMappings") DashBlockStateData blockStateMappings,
                         @Deserialize("fontMappings") DashFontManagerData fontMappings,
                         @Deserialize("modelMappings") DashModelData modelMappings,
                         @Deserialize("particleMappings") DashParticleData particleMappings,
                         @Deserialize("splashTextMappings") DashSplashTextData splashTextMappings,
                         @Deserialize("spriteAtlasMappings") DashSpriteAtlasData spriteAtlasMappings,
                         @Deserialize("blockStateRegistryData") RegistryBlockStateData blockStateRegistryData,
                         @Deserialize("fontRegistryData") RegistryFontData fontRegistryData,
                         @Deserialize("identifierRegistryData") RegistryIdentifierData identifierRegistryData,
                         @Deserialize("imageRegistryData") RegistryImageData imageRegistryData,
                         @Deserialize("modelRegistryData") RegistryModelData modelRegistryData,
                         @Deserialize("propertyRegistryData") RegistryPropertyData propertyRegistryData,
                         @Deserialize("propertyValueRegistryData") RegistryPropertyValueData propertyValueRegistryData,
                         @Deserialize("spriteRegistryData") RegistrySpriteData spriteRegistryData,
                         @Deserialize("predicateRegistryData") RegistryPredicateData predicateRegistryData
    ) {
        this.blockStateMappings = blockStateMappings;
        this.fontMappings = fontMappings;
        this.modelMappings = modelMappings;
        this.particleMappings = particleMappings;
        this.splashTextMappings = splashTextMappings;
        this.spriteAtlasMappings = spriteAtlasMappings;
        this.blockStateRegistryData = blockStateRegistryData;
        this.fontRegistryData = fontRegistryData;
        this.identifierRegistryData = identifierRegistryData;
        this.imageRegistryData = imageRegistryData;
        this.modelRegistryData = modelRegistryData;
        this.propertyRegistryData = propertyRegistryData;
        this.propertyValueRegistryData = propertyValueRegistryData;
        this.spriteRegistryData = spriteRegistryData;
        this.predicateRegistryData = predicateRegistryData;
    }


}
