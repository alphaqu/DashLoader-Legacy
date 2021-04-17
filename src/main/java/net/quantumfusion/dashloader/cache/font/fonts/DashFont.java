package net.quantumfusion.dashloader.cache.font.fonts;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.cache.DashRegistry;

public interface DashFont {
    Font toUndash(DashRegistry registry);
}
