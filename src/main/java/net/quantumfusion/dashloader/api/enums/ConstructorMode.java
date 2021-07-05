package net.quantumfusion.dashloader.api.enums;


import net.quantumfusion.dashloader.api.ExtraVariables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static net.quantumfusion.dashloader.api.DashLoaderAPI.defaultParameters;

public enum ConstructorMode {

    /**
     * public DashObject(OriginalObject originalObject, DashRegistry dashRegistry, ExtraVariables extraVariables)
     */
    FULL((rawClass) -> {
        final List<Class<?>> out = new ArrayList<>();
        out.add(rawClass);
        out.addAll(Arrays.stream(defaultParameters).toList());
        out.add(ExtraVariables.class);
        return out;
    }),
    /**
     * public DashObject(OriginalObject originalObject, DashRegistry dashRegistry)
     */
    DEFAULT_PARAMETERS((rawClass) -> {
        final List<Class<?>> out = new ArrayList<>();
        out.add(rawClass);
        out.addAll(Arrays.stream(defaultParameters).toList());
        return out;
    }),
    /**
     * public DashObject(OriginalObject originalObject)
     */
    OBJECT((rawClass) -> {
        final List<Class<?>> out = new ArrayList<>();
        out.add(rawClass);
        return out;
    }),
    OBJECT_EXTRA((rawClass) -> {
        final List<Class<?>> out = new ArrayList<>();
        out.add(rawClass);
        out.add(ExtraVariables.class);
        return out;
    }),
    /**
     * public DashObject()
     */
    EMPTY((rawClass) -> {
        return new ArrayList<>();
    });

    Function<Class<?>, List<Class<?>>> parameterGetter;

    ConstructorMode(Function<Class<?>, List<Class<?>>> parameterGetter) {
        this.parameterGetter = parameterGetter;
    }

    public Function<Class<?>, List<Class<?>>> getParameterGetter() {
        return parameterGetter;
    }

    public String getExpectedMethod(Class<?> dashClass, Class<?> rawClass) {
        StringBuilder expectedMethod = new StringBuilder();
        expectedMethod.append("public ");
        expectedMethod.append(dashClass.getSimpleName());
        expectedMethod.append('(');
        printClasses(parameterGetter.apply(rawClass), expectedMethod);
        expectedMethod.append(')');
        return expectedMethod.toString();
    }

    private void printClasses(List<Class<?>> classes, StringBuilder stringBuilder) {
        for (Iterator<Class<?>> iterator = classes.iterator(); iterator.hasNext(); ) {
            Class<?> aClass = iterator.next();
            final String simpleName = aClass.getSimpleName();
            stringBuilder.append(simpleName).append(' ').append(Character.toLowerCase(simpleName.charAt(0))).append(simpleName.substring(1));
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
    }
}
