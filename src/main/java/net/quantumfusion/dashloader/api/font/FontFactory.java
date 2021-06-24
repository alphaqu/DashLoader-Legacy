package net.quantumfusion.dashloader.api.font;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;
import net.quantumfusion.dashloader.font.DashFont;

public interface FontFactory extends Factory<Font, DashFont, Object> {

    default FactoryType getFactoryType() {
        return FactoryType.FONT;
    }
}
