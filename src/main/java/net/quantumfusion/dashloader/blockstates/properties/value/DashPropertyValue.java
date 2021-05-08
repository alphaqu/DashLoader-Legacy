package net.quantumfusion.dashloader.blockstates.properties.value;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashPropertyValue extends Dashable {


    <K> K toUndash(DashRegistry registry);
}
