package net.quantumfusion.dashloader.cache;

import io.activej.serializer.StringFormat;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeStringFormat;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.mixin.IdentifierAccessor;

public class DashIdentifier implements DashID {
    @Serialize(order = 0)
    @SerializeStringFormat(StringFormat.UTF8)
    public String namespace;

    @Serialize(order = 1)
    @SerializeStringFormat(StringFormat.UTF8)
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

    private static final Class<Identifier> identifierClass = Identifier.class;

    @Override
    public Identifier toUndash() {
        final Identifier identifier = Unsafe.allocateInstance(identifierClass);
        final IdentifierAccessor access = ((IdentifierAccessor)identifier);
        access.setNamespace(namespace);
        access.setPath(path);
        return identifier;
    }
}
