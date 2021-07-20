package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.texture.Sprite;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.image.DashSprite;

public class RegistrySpriteData {
	@Serialize(order = 0)
	public final Pointer2ObjectMap<DashSprite> sprites;

	public RegistrySpriteData(@Deserialize("sprites") Pointer2ObjectMap<DashSprite> sprites) {
		this.sprites = sprites;
	}


	public RegistrySpriteData(AbstractRegistryStorage<Sprite, DashSprite> storage) {
		sprites = storage.export();
	}

	public Int2ObjectMap<DashSprite> toUndash() {
		return sprites.convert();
	}
}
