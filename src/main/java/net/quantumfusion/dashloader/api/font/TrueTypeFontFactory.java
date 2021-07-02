package net.quantumfusion.dashloader.api.font;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.TrueTypeFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.font.DashTrueTypeFont;

public class TrueTypeFontFactory implements FontFactory {
    @Override
    public DashFont toDash(Font font, DashRegistry registry, Object var1) {
        return new DashTrueTypeFont((TrueTypeFont) font);
    }

    @Override
    public Class<? extends Font> getType() {
        return TrueTypeFont.class;
    }

    @Override
    public Class<? extends DashFont> getDashType() {
        return DashTrueTypeFont.class;
    }

}
