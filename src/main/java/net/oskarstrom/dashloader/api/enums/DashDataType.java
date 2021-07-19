package net.oskarstrom.dashloader.api.enums;

import net.oskarstrom.dashloader.api.DashDataClass;
import net.oskarstrom.dashloader.blockstate.property.DashProperty;
import net.oskarstrom.dashloader.blockstate.property.value.DashPropertyValue;
import net.oskarstrom.dashloader.font.DashFont;
import net.oskarstrom.dashloader.model.DashModel;
import net.oskarstrom.dashloader.model.predicates.DashPredicate;

public enum DashDataType {
    MODEL("Model", "models", DashModel.class, true),
    PROPERTY("Property", "properties", DashProperty.class, true),
    PROPERTY_VALUE("Property Value", "values", DashPropertyValue.class, true),
    PREDICATE("Predicate", "predicates", DashPredicate.class, true),
    FONT("Font", "fonts", DashFont.class, true),
    DATA("Data", "data", DashDataClass.class, false),
    DEFAULT("something went wrong", "omegakek", null, true);

    public String name;
    //serializers
    public String internalName;
    public Class<?> factoryInterface;
    public boolean requiresTargetObject;


    DashDataType(String type, String internalName, Class<?> factoryInterface, boolean requiresTargetObject) {
        this.name = type;
        this.internalName = internalName;
        this.factoryInterface = factoryInterface;
        this.requiresTargetObject = requiresTargetObject;
    }

    @Override
    public String toString() {
        return internalName;
    }
}
