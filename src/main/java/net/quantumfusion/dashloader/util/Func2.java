package net.quantumfusion.dashloader.util;

@FunctionalInterface
public interface Func2<I, I2, O> {
    public O apply(I one, I2 two);
}
