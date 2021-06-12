package net.quantumfusion.dashloader.util;

public class ClassLoaderWrapper extends ClassLoader {

    private ClassLoaderWrapper(ClassLoader parent) {
        super(parent);
    }


    public Class<?> defineClass(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }

    public static ClassLoaderWrapper from(ClassLoader loader) {
        return new ClassLoaderWrapper(loader);
    }
}
