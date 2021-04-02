package net.quantumfusion.dash.sprite.info;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteInfoAccessor;


public class DashInfo {
    @Serialize(order = 0)
    public final DashIdentifier id;
    @Serialize(order = 1)
    public final int width;
    @Serialize(order = 2)
    public final int height;
    @Serialize(order = 3)
    public final DashAnimationResourceMetadata animationData;

    public DashInfo(@Deserialize("id")DashIdentifier id,
                    @Deserialize("width")   int width,
                    @Deserialize("height")   int height,
                    @Deserialize("animationData")   DashAnimationResourceMetadata animationData) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.animationData = animationData;
    }

    public DashInfo(Sprite.Info info) {
        id = new DashIdentifier(info.getId());
        width = info.getWidth();
        height = info.getHeight();
        animationData = new DashAnimationResourceMetadata(((SpriteInfoAccessor)(Object)info).getAnimationData());
    }

    public Sprite.Info toUndash() {
        return new Sprite.Info(id.toUndash(),width,height,animationData.toUndash());
    }
}
