package net.quantumfusion.dashloader.api.fonts;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.font.fonts.DashFont;

public interface FontFactory extends Factory<Font, DashFont> {

    default FactoryType getFactoryType() {
        return FactoryType.FONT;
    }
}
