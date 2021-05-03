package net.quantumfusion.dashloader.cache.font.fonts;

import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.cache.DashRegistry;

public class DashBlankFont implements DashFont {

    @Override
    public Font toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
