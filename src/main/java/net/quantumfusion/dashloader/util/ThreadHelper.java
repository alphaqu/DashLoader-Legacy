package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ThreadHelper {
    public static void exec(Runnable... runnables) {
        final List<Future<Object>> futures = DashLoader.THREADPOOL.invokeAll(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));

    }

    public static <U, D extends Dashable> Int2ObjectMap<U> execParallel(Int2ObjectMap<D> dashables, DashRegistry registry) {
        final var resultMap = new Int2ObjectLinkedOpenHashMap<U>((int) Math.ceil(dashables.size() / 0.75));
        resultMap.putAll(DashLoader.THREADPOOL.invoke(new UndashTask<>(new Int2ObjectLinkedOpenHashMap<>(dashables), 100, registry)));
        return resultMap;
    }

    public static <Var> Function<Var, Var> rebound() {
        return inputVar -> inputVar;
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
