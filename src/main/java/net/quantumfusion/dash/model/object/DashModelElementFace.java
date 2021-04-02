package net.quantumfusion.dash.model.object;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.quantumfusion.dash.model.DashDirection;
import org.jetbrains.annotations.Nullable;

public class DashModelElementFace {


    @Serialize(order = 0)
    @SerializeNullable
    public final DashDirection cullFace;
    @Serialize(order = 1)
    public final int tintIndex;
    @Serialize(order = 2)
    public final String textureId;
    @Serialize(order = 3)
    public final DashModelElementTexture textureData;

    public DashModelElementFace(@Deserialize("cullFace") @Nullable DashDirection cullFace,
                                @Deserialize("tintIndex")  int tintIndex,
                                @Deserialize("textureId") String textureId,
                                @Deserialize("textureData") DashModelElementTexture textureData
    ) {
        this.cullFace = cullFace;
        this.tintIndex = tintIndex;
        this.textureId = textureId;
        this.textureData = textureData;
    }

    public DashModelElementFace(ModelElementFace modelElementFace) {
        if (modelElementFace.cullFace != null) {
            cullFace = new DashDirection(modelElementFace.cullFace);
        } else {
            cullFace = null;
        }
        tintIndex = modelElementFace.tintIndex;
        textureData = new DashModelElementTexture(modelElementFace.textureData);
        textureId = modelElementFace.textureId;
    }

    public ModelElementFace toUndash() {
        return new ModelElementFace(cullFace == null ? null : cullFace.toUndash(), tintIndex, textureId, textureData.toUndash());
    }
}
