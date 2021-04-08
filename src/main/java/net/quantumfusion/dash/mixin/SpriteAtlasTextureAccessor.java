package net.quantumfusion.dash.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(SpriteAtlasTexture.class)
public interface SpriteAtlasTextureAccessor {

	@Accessor("sprites")
	Map<Identifier, Sprite> getSprites();

	@Accessor("animatedSprites")
	List<Sprite> getAnimatedSprites();

	@Accessor("spritesToLoad")
	Set<Identifier> getSpritesToLoad();

	@Accessor("id")
	Identifier getId();

	@Accessor("maxTextureSize")
	int getMaxTextureSize();


	@Accessor("sprites")
	void setSprites(Map<Identifier, Sprite> sprites);

	@Accessor("animatedSprites")
	void setAnimatedSprites(List<Sprite> animatedSprites);

	@Accessor("spritesToLoad")
	void setSpritesToLoad(Set<Identifier> spritesToLoad);

	@Accessor("id")
	void setId(Identifier id);

	@Accessor("maxTextureSize")
	void setMaxTextureSize(int maxTextureSize);

}
