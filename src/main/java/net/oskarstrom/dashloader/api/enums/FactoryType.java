package net.oskarstrom.dashloader.api.enums;

import net.oskarstrom.dashloader.blockstate.property.DashProperty;
import net.oskarstrom.dashloader.blockstate.property.value.DashPropertyValue;
import net.oskarstrom.dashloader.font.DashFont;
import net.oskarstrom.dashloader.model.DashModel;
import net.oskarstrom.dashloader.model.predicates.DashPredicate;

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
