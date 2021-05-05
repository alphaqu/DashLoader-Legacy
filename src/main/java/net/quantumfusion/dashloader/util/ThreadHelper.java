package net.quantumfusion.dashloader.util;

import java.util.Arrays;

public class ThreadHelper {

    public static void exec(Runnable... runnables) {
        Arrays.stream(runnables).parallel().forEach(Runnable::run);
    }

}
