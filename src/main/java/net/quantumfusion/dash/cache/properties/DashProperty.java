package net.quantumfusion.dash.cache.properties;

import net.minecraft.state.property.Property;

public class DashProperty {
    String type;
    String name;

    public DashProperty(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public DashProperty(Property property) {
        type = property.getType().toString();
        name = property.getName();
    }


}
