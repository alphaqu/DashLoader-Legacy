package net.quantumfusion.dashloader.data;

import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;

public interface DashID extends Dashable<Identifier> {

    Identifier toUndash(DashRegistry registry);
}
