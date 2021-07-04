package net.quantumfusion.dashloader.api;


import net.quantumfusion.dashloader.Dashable;

public interface Factory<F> extends Dashable<F> {

    default FactoryType getFactoryType() {
        return FactoryType.DEFAULT;
    }

    ;
}
