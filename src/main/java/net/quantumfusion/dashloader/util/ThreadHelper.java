package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.util.serialization.Pointer2ObjectMap;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ThreadHelper {


    public static void exec(Runnable... runnables) {
        exec(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
    }

    public static <T> void exec(List<Callable<T>> callable) {
        final List<Future<T>> futures = DashLoader.THREAD_POOL.invokeAll(callable);
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <V, D extends Dashable> Map<Integer, V> execParallel(Int2ObjectMap<D> dashables, DashRegistry registry) {
        final Map<Integer, V> answerMap = new HashMap<>((int) Math.ceil(dashables.size() / 0.75));
        final Collection<Pointer2ObjectMap.Entry<V>> invoke = DashLoader.THREAD_POOL.invoke(new UndashTask<>(new ArrayList<>(dashables.int2ObjectEntrySet()), 100, registry));
        invoke.forEach((answer) -> answerMap.put(answer.key, answer.value));
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
