package net.quantumfusion.dash.mixin;

import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.annotation.Native;

@Mixin(Sprite.class)
public interface SpriteAccessor {

    @Accessor("info")
    Sprite.Info getInfo();

    @Accessor("images")
    NativeImage[] getImages();

    @Invoker("<init>")
    static Sprite newSprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage){
        throw new AssertionError();
    }
}
