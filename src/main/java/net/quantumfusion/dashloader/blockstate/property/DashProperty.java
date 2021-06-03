package net.quantumfusion.dashloader.blockstate.property;

import net.minecraft.state.property.Property;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

public interface DashProperty extends Dashable {
     Property<?> toUndash(DashRegistry registry);

}
