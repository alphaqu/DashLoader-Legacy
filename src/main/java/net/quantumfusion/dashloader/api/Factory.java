package net.quantumfusion.dashloader.api;

import net.quantumfusion.dashloader.DashRegistry;

public interface Factory<T, D, E> {

    D toDash(T font, DashRegistry registry, E var1);

    Class<? extends T> getType();

    Class<? extends D> getDashType();

    default FactoryType getFactoryType() {
        return FactoryType.DEFAULT;
    }

    ;
}
