package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashRegistry;

import java.util.function.BiFunction;

public class DashHelper {
    public static <O, I> O nullable(I input, DashRegistry registry, BiFunction<I, DashRegistry, O> func) {
        return input == null ? null : func.apply(input, registry);
    }
}
