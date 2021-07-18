package net.oskarstrom.dashloader.font;

import net.minecraft.client.font.BlankFont;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashObject;

@DashObject(BlankFont.class)
public class DashBlankFont implements DashFont {
    @Override
    public BlankFont toUndash(DashRegistry registry) {
        return new BlankFont();
    }
}
