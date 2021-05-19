package net.quantumfusion.dashloader.api.fonts;

import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.fonts.DashBitmapFont;
import net.quantumfusion.dashloader.font.fonts.DashFont;

public class BitmapFontFactory implements FontFactory {

    @Override
    public <K> DashFont toDash(Font font, DashRegistry registry, K var1) {
        return new DashBitmapFont((BitmapFont) font, registry);
    }

    @Override
    public Class<? extends Font> getType() {
        return BitmapFont.class;
    }

    @Override
    public Class<? extends DashFont> getDashType() {
        return DashBitmapFont.class;
    }

}
