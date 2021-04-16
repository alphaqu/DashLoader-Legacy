package net.quantumfusion.dash.cache;

import net.minecraft.util.Identifier;
import net.quantumfusion.dash.util.Dashable;

public interface DashID extends Dashable {
    Identifier toUndash();
}
