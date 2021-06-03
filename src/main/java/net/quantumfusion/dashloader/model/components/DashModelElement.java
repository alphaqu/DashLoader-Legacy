package net.quantumfusion.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashModelElement {
    @Serialize(order = 0)
    public final DashVec3f from;
    @Serialize(order = 1)
    public final DashVec3f to;

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

    public DashModelElement(@Deserialize("from") DashVec3f from,
                            @Deserialize("to") DashVec3f to,
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
        from = new DashVec3f(modelElement.from);
        to = new DashVec3f(modelElement.to);
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

    public ModelElement toUndash(DashRegistry registry) {
        Map<Direction, ModelElementFace> faceOut = new HashMap<>();
        faces.forEach((s, dashModelElementFace) -> faceOut.put(Direction.byName(s), dashModelElementFace.toUndash(registry)));
        return new ModelElement(from.toUndash(), to.toUndash(), faceOut, rotation == null ? null : rotation.toUndash(), shade);
    }
}
