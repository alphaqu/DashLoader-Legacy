package net.oskarstrom.dashloader.font;

import net.minecraft.client.font.BlankFont;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;

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
