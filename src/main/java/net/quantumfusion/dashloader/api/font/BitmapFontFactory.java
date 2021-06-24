package net.quantumfusion.dashloader.api.font;

import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.DashBitmapFont;
import net.quantumfusion.dashloader.font.DashFont;

public class BitmapFontFactory implements FontFactory {

    @Override
    public DashFont toDash(Font font, DashRegistry registry, Object reserved) {
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
