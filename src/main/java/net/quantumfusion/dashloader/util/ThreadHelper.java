package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ThreadHelper {


    public static final Function REBOUND = func -> func;
    public static void exec(Runnable... runnables) {
        final List<Future<Object>> futures = DashLoader.THREADPOOL.invokeAll(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <V, D extends Dashable> Map<Integer, V> execParallel(Map<Integer, D> dashables, DashRegistry registry) {
        final Map<Integer, V> answerMap = new HashMap<>((int) Math.ceil(dashables.size() / 0.75));
        final Collection<Map.Entry<Integer, V>> invoke = DashLoader.THREADPOOL.invoke(new UndashTask<>(new ArrayList<>(dashables.entrySet()), 100, registry));
        invoke.forEach((answer) -> answerMap.put(answer.getKey(), answer.getValue()));
        return answerMap;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepUntilTrue(Supplier<Boolean> supplier) {
        while (!supplier.get()) {
            sleep(10);
        }
    }

    public static void sleepUntilFalse(Supplier<Boolean> supplier) {
        while (supplier.get()) {
            sleep(10);
        }
    }

    public static void sleepUntilTrue(Supplier<Boolean> supplier, long millis) {
        while (!supplier.get()) {
            sleep(millis);
        }
    }

    public static void sleepUntilFalse(Supplier<Boolean> supplier, long millis) {
        while (supplier.get()) {
            sleep(millis);
        }
    }
}
