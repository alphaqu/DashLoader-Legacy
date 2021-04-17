package net.quantumfusion.dashloader.cache;

import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashID extends Dashable {
    Identifier toUndash();
}
