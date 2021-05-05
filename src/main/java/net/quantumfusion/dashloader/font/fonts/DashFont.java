package net.quantumfusion.dashloader.font.fonts;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;

public interface DashFont {
    Font toUndash(DashRegistry registry);
}
