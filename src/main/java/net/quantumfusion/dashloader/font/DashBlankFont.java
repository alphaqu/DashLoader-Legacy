package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.BlankFont;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.annotation.DashConstructor;
import net.quantumfusion.dashloader.api.annotation.DashObject;
import net.quantumfusion.dashloader.api.enums.ConstructorMode;

@DashObject(BlankFont.class)
public class DashBlankFont implements DashFont {


    @DashConstructor(ConstructorMode.EMPTY)
    public DashBlankFont() {
    }

    @Override
    public BlankFont toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
