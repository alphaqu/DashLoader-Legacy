package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.BlankFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashObject;

@DashObject(BlankFont.class)
public class DashBlankFont implements DashFont {


    public DashBlankFont() {
    }

    public DashBlankFont(BlankFont blankFont, DashRegistry registry) {
    }

    @Override
    public BlankFont toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
