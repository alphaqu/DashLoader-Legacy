package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(SpriteAtlasTexture.Data.class)
public interface SpriteDataAccessor {

	@Accessor("spriteIds")
	Set<Identifier> getSpriteIds();
	@Accessor("width")
	int getWidth();
	@Accessor("height")
	int getHeight();
	@Accessor("maxLevel")
	int getMaxLevel();
	@Accessor("sprites")
	List<Sprite> getSprites();
}
