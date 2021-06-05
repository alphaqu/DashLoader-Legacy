package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.quantumfusion.dashloader.image.DashSprite;

import java.util.Map;

public class RegistrySpriteData {
    @Serialize(order = 0)
    @SerializeNullable(path = {1})
    @SerializeNullable(path = {0})
    public Map<Integer, DashSprite> sprites;

    public RegistrySpriteData(@Deserialize("sprites") Map<Integer, DashSprite> sprites) {
        this.sprites = sprites;
    }
}
