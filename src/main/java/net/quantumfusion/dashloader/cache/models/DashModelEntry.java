package net.quantumfusion.dashloader.cache.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.cache.DashID;
import net.quantumfusion.dashloader.cache.DashIdentifier;

public class DashModelEntry {

    @Serialize(order = 0)
    @SerializeNullable()
    @SerializeSubclasses(value = {
            DashIdentifier.class,
            DashModelIdentifier.class
    })
    public DashID identifier;

    @Serialize(order = 1)
    @SerializeNullable()
    @SerializeSubclasses(extraSubclassesId = "models")
    public DashBakedModel dashBakedModel;


    public DashModelEntry(@Deserialize("identifier") DashID identifier,
                          @Deserialize("dashBakedModel") DashBakedModel dashBakedModel) {
        this.identifier = identifier;
        this.dashBakedModel = dashBakedModel;
    }
}
