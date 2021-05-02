package net.quantumfusion.dashloader.cache.models;


import io.activej.serializer.StringFormat;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeStringFormat;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.cache.DashID;
import net.quantumfusion.dashloader.mixin.IdentifierAccessor;
import net.quantumfusion.dashloader.mixin.ModelIdentifierAccessor;


public class DashModelIdentifier implements DashID {
    @Serialize(order = 0)
    @SerializeStringFormat(StringFormat.UTF8)
    public String namespace;

    @Serialize(order = 1)
    @SerializeStringFormat(StringFormat.UTF8)
    public String path;

    @Serialize(order = 2)
    @SerializeStringFormat(StringFormat.UTF8)
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

    private static final Class<ModelIdentifier> cls = ModelIdentifier.class;

    @Override
    public Identifier toUndash() {
        ModelIdentifier identifier = Unsafe.allocateInstance(cls);
        ((ModelIdentifierAccessor)identifier).setVariant(variant);
        final IdentifierAccessor identifier1 = (IdentifierAccessor) identifier;
        identifier1.setNamespace(namespace);
        identifier1.setPath(path);
        return identifier;
    }
}
