package net.quantumfusion.dash.model.object;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dash.model.DashVector3f;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashModelElement {
    @Serialize(order = 0)
    public final DashVector3f from;
    @Serialize(order = 1)
    public final DashVector3f to;

    @Serialize(order = 2)
    @SerializeNullable()
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public final Map<String, DashModelElementFace> faces;

    @Nullable
    @Serialize(order = 3)
    @SerializeNullable()
    public final DashModelRotation rotation;

    @Serialize(order = 4)
    public final boolean shade;

    public DashModelElement(@Deserialize("from") DashVector3f from,
                            @Deserialize("to") DashVector3f to,
                            @Deserialize("faces") Map<String, DashModelElementFace> faces,
                            @Deserialize("rotation") @Nullable DashModelRotation rotation,
                            @Deserialize("shade") boolean shade) {
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.rotation = rotation;
        this.shade = shade;
    }

    public DashModelElement(ModelElement modelElement) {
        from = new DashVector3f(modelElement.from);
        to = new DashVector3f(modelElement.to);
        HashMap<String, DashModelElementFace> face = new HashMap<>();
        modelElement.faces.forEach((direction, modelElementFace) -> face.put(direction.name(), new DashModelElementFace(modelElementFace)));
        faces = face;
        if (modelElement.rotation != null) {
            rotation = new DashModelRotation(modelElement.rotation);
        } else {
            rotation = null;
        }
        shade = modelElement.shade;
    }

    public ModelElement unDash() {
        Map<Direction, ModelElementFace> faceOut = new HashMap<>();
        faces.forEach((s, dashModelElementFace) -> faceOut.put(Direction.byName(s), dashModelElementFace.toUndash()));
        return new ModelElement(from.toUndash(), to.toUndash(), faceOut, rotation == null ? null : rotation.toUndash(), shade);
    }
}
