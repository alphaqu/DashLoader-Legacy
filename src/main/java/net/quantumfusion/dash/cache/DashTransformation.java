package net.quantumfusion.dash.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.render.model.json.Transformation;

public class DashTransformation {
    @Serialize(order = 0)
    public final DashVector3f rotation;
    @Serialize(order = 1)
    public final DashVector3f translation;
    @Serialize(order = 2)
    public final DashVector3f scale;

    public DashTransformation(@Deserialize("rotation") DashVector3f rotation, @Deserialize("translation")DashVector3f translation, @Deserialize("scale")DashVector3f scale) {
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
    }

    public DashTransformation(Transformation transformation) {
        rotation = new DashVector3f(transformation.rotation);
        translation = new DashVector3f(transformation.translation);
        scale = new DashVector3f(transformation.scale);
    }

    public Transformation toUndash() {
        return new Transformation(rotation.toUndash(), translation.toUndash(), scale.toUndash());
    }
}
