package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ThreadHelper {


    public static void exec(Runnable... runnables) {
        DashLoader.THREADPOOL.invokeAll(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
    }

    public static <V, D extends Dashable> Map<Long, V> execParallel(Map<Long, D> dashables, DashRegistry registry) {
        final Map<Long, V> answerMap = new HashMap<>();
        final Collection<Map.Entry<Long, V>> invoke = DashLoader.THREADPOOL.invoke(new UndashTask<>(new ArrayList<>(dashables.entrySet()), 100, registry));
        invoke.forEach((answer) -> answerMap.put(answer.getKey(), answer.getValue()));
        return answerMap;
    }

    public static void blockUntilDone(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
