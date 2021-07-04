package net.quantumfusion.dashloader.font;

import net.minecraft.client.font.Font;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.api.Factory;
import net.quantumfusion.dashloader.api.FactoryType;

public interface DashFont extends Factory<Font> {
    Font toUndash(DashRegistry registry);

    @Override
    default FactoryType getFactoryType() {
        return FactoryType.FONT;
    }
}

