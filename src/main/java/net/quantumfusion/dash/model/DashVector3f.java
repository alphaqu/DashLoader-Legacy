package net.quantumfusion.dash.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.util.math.Vector3f;

public class DashVector3f {

    @Serialize(order = 0)
    public final float x;
    @Serialize(order = 1)
    public final float y;
    @Serialize(order = 2)
    public final float z;


    public DashVector3f(@Deserialize("x") float x, @Deserialize("y") float y, @Deserialize("z") float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DashVector3f(Vector3f vector3f) {
        x = vector3f.getX();
        y = vector3f.getY();
        z = vector3f.getZ();
    }

    public Vector3f toUndash() {
        return new Vector3f(x, y, z);
    }


}
