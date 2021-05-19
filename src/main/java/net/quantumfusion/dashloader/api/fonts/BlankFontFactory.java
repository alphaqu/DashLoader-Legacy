package net.quantumfusion.dashloader.api.fonts;

import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.fonts.DashBlankFont;
import net.quantumfusion.dashloader.font.fonts.DashFont;

public class BlankFontFactory implements FontFactory {

    @Override
    public <K> DashFont toDash(Font font, DashRegistry registry, K var1) {
        return new DashBlankFont();
    }

    @Override
    public Class<? extends Font> getType() {
        return BlankFont.class;
    }

    @Override
    public Class<? extends DashFont> getDashType() {
        return DashBlankFont.class;
    }

}
