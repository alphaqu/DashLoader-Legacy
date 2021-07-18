package net.oskarstrom.dashloader.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeFixedSize;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.mixin.accessor.IdentifierAccessor;
import net.oskarstrom.dashloader.model.DashModelIdentifier;

public class DashIdentifier implements DashID {
    @Serialize(order = 0)
    @SerializeFixedSize(2)
    public String[] strings;

    public DashIdentifier(@Deserialize("strings") String[] strings) {
        this.strings = strings;
    }

    public DashIdentifier(Identifier identifier) {
        strings = new String[2];
        strings[0] = identifier.getNamespace();
        strings[1] = identifier.getPath();
    }

    @Override
    public Identifier toUndash(DashRegistry registry) {
        return IdentifierAccessor.init(strings);
    }

    public static DashID createIdentifier(Identifier identifier) {
        if (identifier instanceof ModelIdentifier) {
            return new DashModelIdentifier((ModelIdentifier) identifier);
        } else {
            return new DashIdentifier(identifier);
        }
    }
}
