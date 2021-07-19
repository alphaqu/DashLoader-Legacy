package net.oskarstrom.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.oskarstrom.dashloader.DashLoader;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.Dashable;
import net.oskarstrom.dashloader.data.serialization.Pointer2ObjectMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ThreadHelper {


    private static final int FORK_AMOUNT = 100;

    public static void exec(Runnable... runnables) {
        exec(Arrays.stream(runnables).map(Executors::callable).collect(Collectors.toList()));
    }

    public static <T> void exec(List<Callable<T>> callable) {
        final List<Future<T>> futures = DashLoader.THREAD_POOL.invokeAll(callable);
        sleepUntilTrue(() -> futures.stream().allMatch(Future::isDone));
    }

    public static <K, V> void execForEach(Map<K, V> map, BiConsumer<K, V> consumer) {
        map.forEach(consumer);
    }

    public static <V> void execForEach(Collection<V> map, Consumer<V> consumer) {
        map.forEach(consumer);
    }


    public static <D extends Dashable<R>, R> Int2ObjectMap<R> execParallel(Int2ObjectMap<D> dashables, DashRegistry registry) {
        return execParallel(dashables, d -> d.toUndash(registry));
    }


    public static <D, R> Int2ObjectMap<R> execParallel(Int2ObjectMap<D> dashables, Function<D, R> function) {
        return convertExec(dashables, dEntry -> new Pointer2ObjectMap.Entry<>(dEntry.getIntKey(), function.apply(dEntry.getValue())));
    }

    public static <O, R> Int2ObjectMap<R> convertExec(Int2ObjectMap<O> objects, Function<Int2ObjectMap.Entry<O>, Pointer2ObjectMap.Entry<R>> function) {
        final Int2ObjectMap<R> answerMap = new Int2ObjectOpenHashMap<>((int) Math.ceil(objects.size() / 0.75));
        final ConvertTask<R, O> task = new ConvertTask<>(new ArrayList<>(objects.int2ObjectEntrySet()), FORK_AMOUNT, function);
        final ArrayList<Pointer2ObjectMap.Entry<R>> invoke = DashLoader.THREAD_POOL.invoke(task);
        invoke.forEach((answer) -> answerMap.put(answer.key, answer.value));
        return answerMap;
    }

    public static <O> void applyExec(Int2ObjectMap<O> objects, Consumer<O> consumer) {
        final ApplyTask<O> task = new ApplyTask<>(new ArrayList<>(objects.values()), FORK_AMOUNT, consumer);
        DashLoader.THREAD_POOL.invoke(task);
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

    /**
     * @param <R> Return
     * @param <O> Original
     */
    public static class ConvertTask<R, O> extends RecursiveTask<ArrayList<Pointer2ObjectMap.Entry<R>>> {
        private final List<Int2ObjectMap.Entry<O>> tasks;
        private final int threshold;
        private final Function<Int2ObjectMap.Entry<O>, Pointer2ObjectMap.Entry<R>> function;


        public ConvertTask(List<Int2ObjectMap.Entry<O>> tasks, int threshold, Function<Int2ObjectMap.Entry<O>, Pointer2ObjectMap.Entry<R>> function) {
            this.tasks = tasks;
            this.threshold = threshold;
            this.function = function;
        }


        @Override
        protected ArrayList<Pointer2ObjectMap.Entry<R>> compute() {
            final int size = tasks.size();
            if (size < threshold) {
                return computeDirectly();
            } else {
                final int half = size / 2;
                final ConvertTask<R, O> first = new ConvertTask<>(tasks.subList(0, half), threshold, function);
                final ConvertTask<R, O> second = new ConvertTask<>(tasks.subList(half, size), threshold, function);
                invokeAll(first, second);
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
            for (Int2ObjectMap.Entry<O> task : tasks) {
                count.add(function.apply(task));
            }
            return count;
        }
    }

    public static class ApplyTask<O> extends RecursiveAction {
        final List<O> tasks;
        final int threshold;
        final Consumer<O> consumer;

        public ApplyTask(List<O> tasks, int threshold, Consumer<O> consumer) {
            this.tasks = tasks;
            this.threshold = threshold;
            this.consumer = consumer;
        }

        public final Pair<List<O>, List<O>> splitTasks(final List<O> list, final int size) {
            final List<O> first = new ArrayList<>();
            final List<O> second = new ArrayList<>();
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
                final Pair<List<O>, List<O>> subtask = splitTasks(tasks, size);
                final ApplyTask<O> subTask1 = new ApplyTask<>(subtask.getKey(), threshold, consumer);
                final ApplyTask<O> subTask2 = new ApplyTask<>(subtask.getValue(), threshold, consumer);
                invokeAll(subTask1, subTask2);
            }
        }

        protected void computeDirectly() {
            tasks.forEach(consumer);
        }
    }

}
