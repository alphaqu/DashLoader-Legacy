package net.quantumfusion.dashloader.api.enums;

import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

public enum FactoryType {
    MODEL("Model", DashModel.class),
    PROPERTY("Property", DashProperty.class),
    PROPERTY_VALUE("Property Value", DashPropertyValue.class),
    PREDICATE("Predicate", DashPredicate.class),
    FONT("Font", DashFont.class),
    DEFAULT("something went wrong", null);

    public String name;
    public Class<?> factoryInterface;


    FactoryType(String type, Class<?> factoryInterface) {
        this.name = type;
        this.factoryInterface = factoryInterface;
    }
}
