package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashRegistry;

import java.util.function.Function;

public class DashHelper {


    public static <O, I> O nullable(I input, Function<I, O> func) {
        return input == null ? null : func.apply(input);
    }

    public static <O, I> O nullable(I input, DashRegistry registry, Func2<I, DashRegistry, O> func) {
        return input == null ? null : func.apply(input, registry);
    }
}
