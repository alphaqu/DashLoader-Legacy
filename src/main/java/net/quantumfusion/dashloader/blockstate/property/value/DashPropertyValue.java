package net.quantumfusion.dashloader.blockstate.property.value;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

public interface DashPropertyValue extends Dashable {
    <T> T toUndash(DashRegistry registry);
}
