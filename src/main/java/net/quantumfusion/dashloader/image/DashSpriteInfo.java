package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.SpriteInfoAccessor;


public class DashSpriteInfo {
    @Serialize(order = 0)
    public final Integer id;
    @Serialize(order = 1)
    public final int width;
    @Serialize(order = 2)
    public final int height;
    @Serialize(order = 3)
    public final DashAnimationResourceMetadata animationData;

    public DashSpriteInfo(@Deserialize("id") Integer id,
                          @Deserialize("width") int width,
                          @Deserialize("height") int height,
                          @Deserialize("animationData") DashAnimationResourceMetadata animationData) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.animationData = animationData;
    }

    public DashSpriteInfo(Sprite.Info info, DashRegistry registry) {
        id = registry.createIdentifierPointer(info.getId());
        width = info.getWidth();
        height = info.getHeight();
        animationData = new DashAnimationResourceMetadata(((SpriteInfoAccessor) (Object) info).getAnimationData());
    }

    public Sprite.Info toUndash(DashRegistry registry) {
        return new Sprite.Info(registry.getIdentifier(id), width, height, animationData.toUndash());
    }
}
