package net.quantumfusion.dash.cache.atlas;

import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.quantumfusion.dash.cache.DashID;
import net.quantumfusion.dash.cache.DashIdentifier;

public class DashSpriteIdentifier  {
    public final DashID atlas;
    public final DashID texture;

    public DashSpriteIdentifier(DashID atlas, DashID texture) {
        this.atlas = atlas;
        this.texture = texture;
    }

    public DashSpriteIdentifier(SpriteIdentifier spriteIdentifier) {
        atlas = new DashIdentifier(spriteIdentifier.getAtlasId());
        texture = new DashIdentifier(spriteIdentifier.getTextureId());
    }

    public SpriteIdentifier toUndash() {
        return new SpriteIdentifier(atlas.toUndash(),texture.toUndash());
    }
}
