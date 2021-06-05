package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.image.DashSprite;

public class RegistrySpriteData {
    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Int2ObjectSortedMap<DashSprite> sprites;

    public RegistrySpriteData(@Deserialize("sprites") Int2ObjectSortedMap<DashSprite> sprites) {
        this.sprites = sprites;
    }
}
