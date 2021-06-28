package net.quantumfusion.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.data.registry.*;

public class DashRegistryData {
    @Serialize(order = 0)
    public final RegistryBlockStateData blockStateRegistryData;
    @Serialize(order = 1)
    public final RegistryFontData fontRegistryData;
    @Serialize(order = 2)
    public final RegistryIdentifierData identifierRegistryData;
    @Serialize(order = 3)
    public final RegistryPropertyData propertyRegistryData;
    @Serialize(order = 4)
    public final RegistryPropertyValueData propertyValueRegistryData;
    @Serialize(order = 5)
    public final RegistrySpriteData spriteRegistryData;
    @Serialize(order = 6)
    public final RegistryPredicateData predicateRegistryData;


    public DashRegistryData(@Deserialize("blockStateRegistryData") RegistryBlockStateData blockStateRegistryData,
                            @Deserialize("fontRegistryData") RegistryFontData fontRegistryData,
                            @Deserialize("identifierRegistryData") RegistryIdentifierData identifierRegistryData,
                            @Deserialize("propertyRegistryData") RegistryPropertyData propertyRegistryData,
                            @Deserialize("propertyValueRegistryData") RegistryPropertyValueData propertyValueRegistryData,
                            @Deserialize("spriteRegistryData") RegistrySpriteData spriteRegistryData,
                            @Deserialize("predicateRegistryData") RegistryPredicateData predicateRegistryData
    ) {
        this.blockStateRegistryData = blockStateRegistryData;
        this.fontRegistryData = fontRegistryData;
        this.identifierRegistryData = identifierRegistryData;
        this.propertyRegistryData = propertyRegistryData;
        this.propertyValueRegistryData = propertyValueRegistryData;
        this.spriteRegistryData = spriteRegistryData;
        this.predicateRegistryData = predicateRegistryData;
    }

}
