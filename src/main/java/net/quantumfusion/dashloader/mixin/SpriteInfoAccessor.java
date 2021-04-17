package net.quantumfusion.dashloader.mixin;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.texture.Sprite$Info")
public interface SpriteInfoAccessor {

    @Accessor("animationData")
    AnimationResourceMetadata getAnimationData();

}
