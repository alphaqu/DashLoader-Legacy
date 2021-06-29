package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;

public interface DashFont extends Dashable {
    Font toUndash(DashRegistry registry);
}
