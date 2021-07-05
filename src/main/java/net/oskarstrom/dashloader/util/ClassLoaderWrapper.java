package net.oskarstrom.dashloader.util;

import java.security.SecureClassLoader;

public class ClassLoaderWrapper extends SecureClassLoader {

    public ClassLoaderWrapper(SecureClassLoader parent) {
        super(parent);
    }

    public Class<?> defineCustomClass(String name, byte[] b) throws ClassFormatError {
        return super.defineClass(name, b, 0, b.length);
    }


}
