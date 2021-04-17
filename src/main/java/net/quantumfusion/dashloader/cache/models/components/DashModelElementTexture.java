package net.quantumfusion.dashloader.cache.models.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.json.ModelElementTexture;

public class DashModelElementTexture {

    @Serialize(order = 0)
    public final float[] uvs;
    @Serialize(order = 1)
    public final int rotation;

    public DashModelElementTexture(@Deserialize("uvs") float[] uvs,
                                   @Deserialize("rotation") int rotation) {
        this.uvs = uvs;
        this.rotation = rotation;
    }

    public DashModelElementTexture(ModelElementTexture modelElementTexture) {
        uvs = modelElementTexture.uvs;
        rotation = modelElementTexture.rotation;
    }

    public ModelElementTexture toUndash() {
        return new ModelElementTexture(uvs, rotation);
    }
}
