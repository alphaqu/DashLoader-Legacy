package net.quantumfusion.dash.model.atlas;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.quantumfusion.dash.common.DashIdentifier;
import net.quantumfusion.dash.mixin.SpriteAtlasManagerAccessor;
import net.quantumfusion.dash.sprite.util.DashImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DashSpriteAtlasManager {
	public final ArrayList<DashSpriteAtlasTexture> atlases;

	public DashSpriteAtlasManager(ArrayList<DashSpriteAtlasTexture> atlases) {
		this.atlases = atlases;
	}

	public DashSpriteAtlasManager(SpriteAtlasManager spriteAtlasManager) {
		atlases = new ArrayList<>();
		((SpriteAtlasManagerAccessor)spriteAtlasManager).getAtlases().forEach((identifier, spriteAtlasTexture) -> atlases.add(new DashSpriteAtlasTexture(spriteAtlasTexture)));
	}

	public SpriteAtlasManager toUndash() {
		ArrayList<SpriteAtlasTexture> out = new ArrayList<>();
		atlases.forEach(spriteAtlasTexture -> out.add(spriteAtlasTexture.toUndash()));
		return new SpriteAtlasManager(out);
	}
}
