package net.quantumfusion.dashloader.api;

import net.minecraft.state.StateManager;
import net.quantumfusion.dashloader.blockstate.property.DashProperty;
import net.quantumfusion.dashloader.blockstate.property.value.DashPropertyValue;
import net.quantumfusion.dashloader.font.DashFont;
import net.quantumfusion.dashloader.model.DashModel;
import net.quantumfusion.dashloader.model.ModelVariables;
import net.quantumfusion.dashloader.model.predicates.DashPredicate;

public enum FactoryType {
    MODEL("Model", new Class[]{ModelVariables.class}, DashModel.class),
    PROPERTY("Property", new Class[]{Integer.class}, DashProperty.class),
    PROPERTY_VALUE("Property Value", new Class[]{Integer.class}, DashPropertyValue.class),
    PREDICATE("Predicate", new Class[]{StateManager.class}, DashPredicate.class),
    FONT("Font", DashFont.class),
    DEFAULT("something went wrong", null);

    public String name;
    public Class<?>[] extraParameters;
    public Class<?> factoryInterface;

    FactoryType(String type, Class<?>[] extraParameters, Class<?> factoryInterface) {
        this.name = type;
        this.extraParameters = extraParameters;
        this.factoryInterface = factoryInterface;
    }

    FactoryType(String type, Class<?> factoryInterface) {
        this.name = type;
        this.extraParameters = new Class[]{};
        this.factoryInterface = factoryInterface;
    }
}
