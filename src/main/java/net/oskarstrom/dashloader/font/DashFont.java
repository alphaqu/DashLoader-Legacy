package net.oskarstrom.dashloader.font;

import net.minecraft.client.font.Font;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.Factory;

public interface DashFont extends Factory<Font> {
    Font toUndash(DashRegistry registry);

}

