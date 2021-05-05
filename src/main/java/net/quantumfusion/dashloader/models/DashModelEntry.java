package net.quantumfusion.dashloader.models;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.quantumfusion.dashloader.common.DashID;
import net.quantumfusion.dashloader.common.DashIdentifier;

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
    public DashModel dashBakedModel;


    public DashModelEntry(@Deserialize("identifier") DashID identifier,
                          @Deserialize("dashBakedModel") DashModel dashBakedModel) {
        this.identifier = identifier;
        this.dashBakedModel = dashBakedModel;
    }
}
