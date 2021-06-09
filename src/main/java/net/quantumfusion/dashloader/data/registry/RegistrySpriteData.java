package net.quantumfusion.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.image.DashSprite;
import net.quantumfusion.dashloader.util.Pntr2ObjectMap;


public class RegistrySpriteData {
    @Serialize(order = 0)
    public Pntr2ObjectMap<DashSprite> sprites;

    public RegistrySpriteData(@Deserialize("sprites") Int2ObjectMap<DashSprite> sprites) {
        this.sprites = new Pntr2ObjectMap<>(sprites);
    }

    public Int2ObjectMap<DashSprite> toUndash() {
        return sprites.convert();
    }
}
