package net.quantumfusion.dash.cache.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dash.Dash;
import net.quantumfusion.dash.cache.DashCache;
import net.quantumfusion.dash.mixin.ParticleManagerSimpleSpriteProviderAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashSimpleSpriteProvider {
    @Serialize(order = 0)
    public List<Integer> sprites;

    public DashSimpleSpriteProvider(@Deserialize("sprites")  List<Integer> sprites) {
        this.sprites = sprites;
    }

    public DashSimpleSpriteProvider(ParticleManager.SimpleSpriteProvider simpleSpriteProvider) {
        sprites = new ArrayList<>();
        ((ParticleManagerSimpleSpriteProviderAccessor)simpleSpriteProvider).getSprites().forEach(sprite -> sprites.add(Dash.loader.registry.createSpritePointer(sprite)));
    }

    public ParticleManager.SimpleSpriteProvider toUndash(DashCache loader) {
        ParticleManager.SimpleSpriteProvider out = null;
        try {
            out = (ParticleManager.SimpleSpriteProvider) Dash.getUnsafe().allocateInstance(ParticleManager.SimpleSpriteProvider.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        List<Sprite> spritesOut = new ArrayList<>();
        sprites.forEach(integer -> spritesOut.add(loader.registry.getSprite(integer)));
        out.setSprites(spritesOut);
        return out;
    }
}
