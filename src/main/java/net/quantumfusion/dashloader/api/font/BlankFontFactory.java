package net.quantumfusion.dashloader.api.font;

import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.font.DashBlankFont;
import net.quantumfusion.dashloader.font.DashFont;

public class BlankFontFactory implements FontFactory {

    @Override
    public DashFont toDash(Font font, DashRegistry registry, Object reserved) {
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
