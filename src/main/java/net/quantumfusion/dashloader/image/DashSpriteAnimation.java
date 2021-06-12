package net.quantumfusion.dashloader.image;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.SpriteAnimationAccessor;
import net.quantumfusion.dashloader.util.DashHelper;

import java.util.ArrayList;
import java.util.List;

public class DashSpriteAnimation {
    @Serialize(order = 0)
    public DashSpriteAnimationFrame[] frames;
    @Serialize(order = 1)
    public int frameCount;
    @Serialize(order = 2)
    @SerializeNullable
    public DashSpriteInterpolation interpolation;

    public DashSpriteAnimation(@Deserialize("frames") DashSpriteAnimationFrame[] frames,
                               @Deserialize("frameCount") int frameCount,
                               @Deserialize("interpolation") DashSpriteInterpolation interpolation) {
        this.frames = frames;
        this.frameCount = frameCount;
        this.interpolation = interpolation;
    }


    public DashSpriteAnimation(Sprite.Animation animation, DashRegistry registry) {
        SpriteAnimationAccessor access = ((SpriteAnimationAccessor) animation);
        frames = new DashSpriteAnimationFrame[access.getFrames().size()];
        List<Sprite.AnimationFrame> accessFrames = access.getFrames();
        for (int i = 0, accessFramesSize = accessFrames.size(); i < accessFramesSize; i++) {
            frames[i] = new DashSpriteAnimationFrame(accessFrames.get(i));
        }
        frameCount = access.getFrameCount();
        interpolation = DashHelper.nullable(access.getInterpolation(), registry, DashSpriteInterpolation::new);
    }


    public Sprite.Animation toUndash(Sprite owner, DashRegistry registry) {
        List<Sprite.AnimationFrame> out = new ArrayList<>(frames.length);
        for (DashSpriteAnimationFrame frame : frames) {
            out.add(frame.toUndash(registry));
        }
        return SpriteAnimationAccessor.init(owner, out, frameCount, interpolation == null ? null : interpolation.toUndash(owner, registry));
    }
}
