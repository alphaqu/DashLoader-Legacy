package net.quantumfusion.dash.mixin;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(targets = "net.minecraft.client.texture.Sprite$Info")
public interface SpriteInfoAccessor {

	@Accessor("animationData")
	AnimationResourceMetadata getAnimationData();

}
