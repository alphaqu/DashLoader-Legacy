package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;

public class DashBlankFont implements DashFont {

    @Override
    public Font toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
