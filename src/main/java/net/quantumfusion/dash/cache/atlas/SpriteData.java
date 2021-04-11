package net.quantumfusion.dash.cache.atlas;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.cache.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteDataAccessor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SpriteData {
    Set<DashIdentifier> spriteIds;
    final int width;
    final int height;
    final int maxLevel;
    List<DashSprite> sprites;

    public SpriteData(Set<DashIdentifier> spriteIds, int width, int height, int maxLevel, List<DashSprite> sprites) {
        this.spriteIds = spriteIds;
        this.width = width;
        this.height = height;
        this.maxLevel = maxLevel;
        this.sprites = sprites;
    }

    public SpriteData(SpriteAtlasTexture.Data data) {
        SpriteDataAccessor dataAccessor = ((SpriteDataAccessor) data);
        spriteIds = new HashSet<>();
        sprites = new LinkedList<>();
        dataAccessor.getSpriteIds().forEach(identifier -> spriteIds.add(new DashIdentifier(identifier)));
        width = dataAccessor.getWidth();
        height = dataAccessor.getHeight();
        maxLevel = dataAccessor.getMaxLevel();


    }
}
