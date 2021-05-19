package net.quantumfusion.dashloader.util;

public final class ClassHelper {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>) aClass;
    }
}