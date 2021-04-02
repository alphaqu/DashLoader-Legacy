package net.quantumfusion.dash.mixin;

import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ParticleTextureData.class)
public interface ParticleTextureDataAccessor {

    @Invoker("<init>")
    static ParticleTextureData newParticleTextureData(@Nullable List<Identifier> textureList){
        throw new AssertionError();
    }
}
