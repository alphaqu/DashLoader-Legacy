package net.quantumfusion.dashloader.api;

public enum FactoryType {
    MODEL("Model"),
    PROPERTY("Property"),
    PREDICATE("Predicate"),
    FONT("Font"),
    DEFAULT("something went wrong");

    public String name;

    FactoryType(String type) {
        this.name = type;
    }
}
