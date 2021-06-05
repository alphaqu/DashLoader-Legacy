package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ThreadHelper {
    public static void exec(Runnable... runnables) {
        final var futures = DashLoader.THREAD_POOL
            .invokeAll(
                Arrays.stream(runnables)
                    .map(Executors::callable)
                    .collect(Collectors.toList())
            );
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <U, D extends Dashable<U>> Int2ObjectSortedMap<U> execParallel(Int2ObjectSortedMap<D> dashables, DashRegistry registry) {
        final var resultMap = new Int2ObjectLinkedOpenHashMap<U>((int) Math.ceil(dashables.size() / 0.75));
        resultMap.putAll(DashLoader.THREAD_POOL.invoke(new UndashTask<>(dashables, 100, registry)));
        return resultMap;
    }

    public static void sneakySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleepUntilTrue(Supplier<Boolean> supplier) {
        while (!supplier.get()) {
            sneakySleep(10);
        }
    }

    public static void sleepUntilFalse(Supplier<Boolean> supplier) {
        while (supplier.get()) {
            sneakySleep(10);
        }
    }

    public static void sleepUntilTrue(Supplier<Boolean> supplier, long millis) {
        while (!supplier.get()) {
            sneakySleep(millis);
        }
    }

    public static void sleepUntilFalse(Supplier<Boolean> supplier, long millis) {
        while (supplier.get()) {
            sneakySleep(millis);
        }
    }
}
