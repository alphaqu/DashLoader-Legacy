package net.quantumfusion.dash.cache.font.fonts;

import net.minecraft.client.font.Font;
import net.quantumfusion.dash.cache.DashRegistry;

public interface DashFont {
    Font toUndash(DashRegistry registry);
}
