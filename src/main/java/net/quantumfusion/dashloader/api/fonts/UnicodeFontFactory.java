package net.quantumfusion.dashloader.api.fonts;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.UnicodeTextureFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.fonts.DashFont;
import net.quantumfusion.dashloader.font.fonts.DashUnicodeFont;

public class UnicodeFontFactory implements FontFactory {


    @Override
    public <K> DashFont toDash(Font font, DashRegistry registry, K var1) {
        return new DashUnicodeFont((UnicodeTextureFont) font, registry);
    }

    @Override
    public Class<? extends Font> getType() {
        return UnicodeTextureFont.class;
    }

    @Override
    public Class<? extends DashFont> getDashType() {
        return DashUnicodeFont.class;
    }

}