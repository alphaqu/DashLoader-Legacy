package net.quantumfusion.dashloader.blockstate.property.value;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;

public interface DashPropertyValue extends Dashable {
    <K> K toUndash(DashRegistry registry);
}
