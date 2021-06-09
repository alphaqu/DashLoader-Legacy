package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class UndashTask<U, D extends Dashable> extends RecursiveTask<Int2ObjectSortedMap<U>> {
    private final Int2ObjectSortedMap<D> tasks;
    private final int threshold;
    private final DashRegistry registry;


    public UndashTask(Int2ObjectSortedMap<D> tasks, int threshold, DashRegistry registry) {
        this.tasks = tasks;
        this.threshold = threshold;
        this.registry = registry;
    }

    @Override
    protected Int2ObjectSortedMap<U> compute() {
        final int size = tasks.size();
        if (size < threshold) {
            return computeDirectly();
        } else {
            final var half = size / 2;
            final UndashTask<U, D> first = new UndashTask<>(tasks.subMap(0, half), threshold, registry);
            final UndashTask<U, D> second = new UndashTask<>(tasks.subMap(half, tasks.size()), threshold, registry);
            invokeAll(first, second);
            return combine(first.join(), second.join());
        }
    }

    public final Int2ObjectSortedMap<U> combine(final Int2ObjectSortedMap<U> map, final Int2ObjectSortedMap<U> map2) {
        map.putAll(map2);
        return map;
    }

    protected final Int2ObjectSortedMap<U> computeDirectly() {
        final var count = new Int2ObjectLinkedOpenHashMap<U>(tasks.size());
        tasks.int2ObjectEntrySet().forEach(e -> count.put(e.getIntKey(), e.getValue().toUndash(registry)));
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
            final var half = size / 2;
            final var first = list.subList(0, half);
            final var second = list.subList(half, list.size());
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
