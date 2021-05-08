package net.quantumfusion.dashloader.blockstates.properties;

import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

public interface DashProperty extends Dashable {
     Property<?> toUndash(DashRegistry registry);

}
