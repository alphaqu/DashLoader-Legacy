package net.quantumfusion.dashloader.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.ParticleManagerSimpleSpriteProviderAccessor;

import java.util.ArrayList;
import java.util.List;

public class DashSimpleSpriteProvider {
    @Serialize(order = 0)
    public List<Integer> sprites;

    public DashSimpleSpriteProvider(@Deserialize("sprites") List<Integer> sprites) {
        this.sprites = sprites;
    }

    public DashSimpleSpriteProvider(ParticleManager.SimpleSpriteProvider simpleSpriteProvider, DashRegistry registry) {
        sprites = new ArrayList<>();
        ((ParticleManagerSimpleSpriteProviderAccessor) simpleSpriteProvider).getSprites().forEach(sprite -> sprites.add(registry.createSpritePointer(sprite)));
    }

    public ParticleManager.SimpleSpriteProvider toUndash(DashRegistry registry) {
        ParticleManager.SimpleSpriteProvider out = Unsafe.allocateInstance(ParticleManager.SimpleSpriteProvider.class);
        List<Sprite> spritesOut = new ArrayList<>();
        sprites.forEach(integer -> spritesOut.add(registry.getSprite(integer)));
        out.setSprites(spritesOut);
        return out;
    }
}
