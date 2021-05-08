package net.quantumfusion.dashloader.font.fonts;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashFont extends Dashable {
    Font toUndash(DashRegistry registry);
}
