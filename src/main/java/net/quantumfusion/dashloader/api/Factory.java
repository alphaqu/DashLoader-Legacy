package net.quantumfusion.dashloader.api;

import net.quantumfusion.dashloader.DashRegistry;

public interface Factory<T, D> {

    <K> D toDash(T font, DashRegistry registry, K var1);

    Class<? extends T> getType();

    Class<? extends D> getDashType();

    default FactoryType getFactoryType() {
        return FactoryType.DEFAULT;
    }

    ;
}
