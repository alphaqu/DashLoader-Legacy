package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.Dashable;
import net.quantumfusion.dashloader.data.serialization.Pointer2ObjectMap;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.quantumfusion.dashloader.DashLoader.LOGGER;

public class ThreadHelper {


    public static void exec(Runnable... runnables) {
        exec(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
    }

    public static <T> void exec(List<Callable<T>> callable) {
        final List<Future<T>> futures = DashLoader.THREAD_POOL.invokeAll(callable);
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <K, V> void execForEach(Map<K, V> map, BiConsumer<K, V> consumer) {
        map.forEach(consumer);
//        Collection<Callable<Object>> callable = new ArrayList<>();
//        map.forEach((k, v) -> callable.add(Executors.callable(() -> consumer.accept(k,v))));
//        final Collection<Future<Object>> futures = DashLoader.THREAD_POOL.invokeAll(callable);
//        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <V> void execForEach(Collection<V> map, Consumer<V> consumer) {
        map.forEach(consumer);
//        Collection<Callable<Object>> callable = new ArrayList<>();
//        map.forEach((v) -> callable.add(Executors.callable(() -> consumer.accept(v))));
//        final Collection<Future<Object>> futures = DashLoader.THREAD_POOL.invokeAll(callable);
//        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }


    public static <D extends Dashable<R>, R> Int2ObjectMap<R> execParallel(Int2ObjectMap<D> dashables, DashRegistry registry) {
        final Int2ObjectMap<R> answerMap = new Int2ObjectOpenHashMap<>((int) Math.ceil(dashables.size() / 0.75));
        final UndashTask<R, D> task = new UndashTask<>(new ArrayList<>(dashables.int2ObjectEntrySet()), 100, registry);
        final ArrayList<Pointer2ObjectMap.Entry<R>> invoke = DashLoader.THREAD_POOL.invoke(task);
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

    public static class UndashTask<R, D extends Dashable<R>> extends RecursiveTask<ArrayList<Pointer2ObjectMap.Entry<R>>> {
        private final List<Int2ObjectMap.Entry<D>> tasks;
        private final int threshold;
        private final DashRegistry registry;


        public UndashTask(List<Int2ObjectMap.Entry<D>> tasks, int threshold, DashRegistry registry) {
            this.tasks = tasks;
            this.threshold = threshold;
            this.registry = registry;
        }


        @Override
        protected ArrayList<Pointer2ObjectMap.Entry<R>> compute() {
            final int size = tasks.size();
            if (size < threshold) {
                return computeDirectly();
            } else {
                final int half = size / 2;
                final UndashTask<R, D> first = new UndashTask<>(tasks.subList(0, half), threshold, registry);
                final UndashTask<R, D> second = new UndashTask<>(tasks.subList(half, size), threshold, registry);
                invokeAll(first, second);
                final Throwable exception = first.getException();
                final Throwable exception1 = second.getException();
                if (exception != null) {
                    LOGGER.fatal("Thread failed. Reason: ", exception);
                }

                if (exception1 != null) {
                    LOGGER.fatal("Thread failed. Reason: ", exception1);
                }

                return combine(first.join(), second.join());
            }
        }

        public final ArrayList<Pointer2ObjectMap.Entry<R>> combine(final ArrayList<Pointer2ObjectMap.Entry<R>> list, final ArrayList<Pointer2ObjectMap.Entry<R>> list2) {
            list.ensureCapacity(list.size() * 2);
            list.addAll(list2);
            return list;
        }

        protected final ArrayList<Pointer2ObjectMap.Entry<R>> computeDirectly() {
            final ArrayList<Pointer2ObjectMap.Entry<R>> count = new ArrayList<>(tasks.size());
            tasks.forEach(dashable -> {
                final Pointer2ObjectMap.Entry<R> oEntry = new Pointer2ObjectMap.Entry<>(dashable.getIntKey(), dashable.getValue().toUndash(registry));
                count.add(oEntry);
            });
            return count;
        }

        public static class ApplyTask extends RecursiveAction {
            final List<DashModel> tasks;
            final int threshold;
            final DashRegistry registry;


            public ApplyTask(List<DashModel> tasks, int threshold, DashRegistry registry) {
                this.tasks = tasks;
                this.threshold = threshold;
                this.registry = registry;
            }

            public final Pair<List<DashModel>, List<DashModel>> split(final List<DashModel> list, final int size) {
                final List<DashModel> first = new ArrayList<>();
                final List<DashModel> second = new ArrayList<>();
                final int i1 = size / 2;
                for (int i = 0; i < i1; i++)
                    first.add(list.get(i));
                for (int i = i1; i < size; i++)
                    second.add(list.get(i));
                return Pair.of(first, second);
            }

            @Override
            protected void compute() {
                final int size = tasks.size();
                if (size < threshold) {
                    computeDirectly();
                } else {
                    final Pair<List<DashModel>, List<DashModel>> subtask = split(tasks, size);
                    final ApplyTask subTask1 = new ApplyTask(subtask.getKey(), threshold, registry);
                    final ApplyTask subTask2 = new ApplyTask(subtask.getValue(), threshold, registry);
                    invokeAll(subTask1, subTask2);
                }
            }

            protected void computeDirectly() {
                tasks.forEach(dashable -> dashable.apply(registry));
            }
        }

    }
}
