package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.image.DashSprite;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

public class RegistrySpriteData {
    @Serialize(order = 0)
    public Pointer2ObjectMap<DashSprite> sprites;

    public RegistrySpriteData(@Deserialize("sprites") Pointer2ObjectMap<DashSprite> sprites) {
        this.sprites = sprites;
    }


    public Int2ObjectMap<DashSprite> toUndash() {
        return sprites.convert();
    }
}
