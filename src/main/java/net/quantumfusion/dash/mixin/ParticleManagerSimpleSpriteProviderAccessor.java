package net.quantumfusion.dash.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ParticleManager.SimpleSpriteProvider.class)
public interface ParticleManagerSimpleSpriteProviderAccessor {

    @Accessor
    List<Sprite> getSprites();

    @Accessor
    void setSprites(List<Sprite> sprites);
}
