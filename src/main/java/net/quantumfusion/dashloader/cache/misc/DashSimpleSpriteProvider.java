package net.quantumfusion.dashloader.cache.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.mixin.ParticleManagerSimpleSpriteProviderAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashSimpleSpriteProvider {
    @Serialize(order = 0)
    public List<Integer> sprites;

    public DashSimpleSpriteProvider(@Deserialize("sprites")  List<Integer> sprites) {
        this.sprites = sprites;
    }

    public DashSimpleSpriteProvider(ParticleManager.SimpleSpriteProvider simpleSpriteProvider,DashRegistry registry) {
        sprites = new ArrayList<>();
        ((ParticleManagerSimpleSpriteProviderAccessor)simpleSpriteProvider).getSprites().forEach(sprite -> sprites.add(registry.createSpritePointer(sprite)));
    }

    public ParticleManager.SimpleSpriteProvider toUndash(DashRegistry registry) {
        ParticleManager.SimpleSpriteProvider out = null;
        try {
            out = (ParticleManager.SimpleSpriteProvider) DashLoader.getInstance().getUnsafe().allocateInstance(ParticleManager.SimpleSpriteProvider.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        List<Sprite> spritesOut = new ArrayList<>();
        sprites.forEach(integer -> spritesOut.add(registry.getSprite(integer)));
        out.setSprites(spritesOut);
        return out;
    }
}
