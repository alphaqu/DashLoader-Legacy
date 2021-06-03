package net.quantumfusion.dashloader.data;

import io.activej.serializer.StringFormat;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeFixedSize;
import io.activej.serializer.annotations.SerializeStringFormat;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.IdentifierAccessor;

public class DashIdentifier implements DashID {
    @Serialize(order = 0)
    @SerializeStringFormat(value = StringFormat.UTF8, path = {0, 0})
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
}
