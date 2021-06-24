package net.quantumfusion.dashloader.api.font;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.UnicodeTextureFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.font.DashUnicodeFont;

public class UnicodeFontFactory implements FontFactory {


    @Override
    public DashFont toDash(Font font, DashRegistry registry, Object reserved) {
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