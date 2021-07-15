package net.oskarstrom.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshImpl;
import net.oskarstrom.dashloader.mixin.accessor.MeshImplAccessor;

public class DashMesh {
    @Serialize(order = 0)
    public final int[] data;

    @SuppressWarnings("unused")
    public DashMesh(@Deserialize("data") int[] data) {
        this.data = data;
    }

    public DashMesh(Mesh mesh) {
        data = ((MeshImpl) mesh).data();
    }

    public Mesh toUndash() {
        return MeshImplAccessor.create(data);
    }
}