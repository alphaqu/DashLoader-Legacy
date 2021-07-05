package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;

public interface DashFont extends Factory<Font> {
    Font toUndash(DashRegistry registry);

}

