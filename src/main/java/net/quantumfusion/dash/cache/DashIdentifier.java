package net.quantumfusion.dash.cache;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class DashIdentifier {
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

    public Identifier toUndash() {
        return new Identifier(namespace, path);
    }
}
