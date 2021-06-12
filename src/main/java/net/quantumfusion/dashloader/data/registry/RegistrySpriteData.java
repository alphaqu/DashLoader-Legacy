package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.image.DashSprite;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.Map;

public class RegistrySpriteData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashSprite> sprites;

    public RegistrySpriteData(@Deserialize("sprites") Pointer2ObjectMap<DashSprite> sprites) {
        this.sprites = sprites;
    }


    public Map<Integer, DashSprite> toUndash() {
        return sprites.convert();
    }
}
