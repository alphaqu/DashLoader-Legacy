package net.quantumfusion.dashloader.util;

import java.util.List;

public final class ClassHelper {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>) aClass;
    }


    public static Class<?> forName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String printClasses(Class<?>[] classes) {
        StringBuilder builder = new StringBuilder();
        for (Class<?> aClass : classes) builder.append(aClass.getSimpleName());
        return builder.toString();
    }

    public static String printClasses(List<Class<?>> classes) {
        StringBuilder builder = new StringBuilder();
        for (Class<?> aClass : classes) builder.append(aClass.getSimpleName());
        return builder.toString();
    }
}