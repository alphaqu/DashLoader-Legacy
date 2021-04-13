package net.quantumfusion.dash.cache.models;


import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashID;


public class DashModelIdentifier implements DashID {
    @Serialize(order = 0)
    public String namespace;
    @Serialize(order = 1)
    public String path;
    @Serialize(order = 2)
    public String variant;


    public DashModelIdentifier(@Deserialize("namespace") String namespace,
                               @Deserialize("path") String path,
                               @Deserialize("variant")  String variant) {
        this.namespace = namespace;
        this.path = path;
        this.variant = variant;
    }

    public DashModelIdentifier(ModelIdentifier identifier) {
        this.namespace = identifier.getNamespace();
        this.path = identifier.getPath();
        this.variant = identifier.getVariant();
    }

    @Override
    public Identifier toUndash() {
        return new ModelIdentifier(new Identifier(namespace, path), variant);
    }
}
