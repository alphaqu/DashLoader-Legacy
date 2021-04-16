package net.quantumfusion.dash.cache.font.fonts;

import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dash.cache.DashRegistry;

public class DashBlankFont implements DashFont{

    public DashBlankFont() {
    }

    @Override
    public Font toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
