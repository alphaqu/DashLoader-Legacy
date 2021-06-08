package net.quantumfusion.dashloader.util;

import java.io.IOException;

public class ExceptionHelper {
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}
