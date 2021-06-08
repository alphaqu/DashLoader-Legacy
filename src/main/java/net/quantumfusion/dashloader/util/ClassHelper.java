package net.quantumfusion.dashloader.util;

public final class ClassHelper {
    public static Class<?> sneakyForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); //:omegabrain:
        }
    }
}