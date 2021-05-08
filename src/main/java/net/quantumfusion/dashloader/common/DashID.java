package net.quantumfusion.dashloader.common;

import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashID extends Dashable {

    Identifier toUndash(DashRegistry registry);
}
