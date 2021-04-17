package net.quantumfusion.dashloader.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.util.Identifier;

public class DashIdentifier implements DashID {
    @Serialize(order = 0)
    public String namespace;
    @Serialize(order = 1)
    public String path;

    public DashIdentifier(@Deserialize("namespace") String namespace,
                          @Deserialize("path") String path) {
        this.namespace = namespace;
        this.path = path;

    }

    public DashIdentifier(Identifier identifier) {
        this.namespace = identifier.getNamespace();
        this.path = identifier.getPath();
    }

    @Override
    public Identifier toUndash() {
        return new Identifier(namespace, path);
    }
}
