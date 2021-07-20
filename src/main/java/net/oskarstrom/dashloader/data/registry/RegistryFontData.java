package net.oskarstrom.dashloader.data.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.font.Font;
import net.oskarstrom.dashloader.data.registry.storage.AbstractRegistryStorage;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import net.oskarstrom.dashloader.font.DashFont;

public class RegistryFontData {
	@Serialize(order = 0)
	@SerializeSubclasses(path = {0}, extraSubclassesId = "fonts")
	public final Pointer2ObjectMap<DashFont> fonts;

	public RegistryFontData(@Deserialize("fonts") Pointer2ObjectMap<DashFont> fonts) {
		this.fonts = fonts;
	}

	public RegistryFontData(AbstractRegistryStorage<Font, DashFont> storage) {
		fonts = storage.export();
	}

	public Int2ObjectMap<DashFont> toUndash() {
		return fonts.convert();
	}

}
