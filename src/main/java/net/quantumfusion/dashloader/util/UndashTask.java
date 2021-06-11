package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.model.DashModel;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class UndashTask<U, D extends Dashable> extends RecursiveTask<Int2ObjectOpenHashMap<U>> {
    private final Int2ObjectMap.Entry<D>[] tasks;
    private final int threshold;
    int start, end;
    private final DashRegistry registry;


    public UndashTask(Int2ObjectMap.Entry<D>[] tasks, int threshold, int start, int end, DashRegistry registry) {
        this.tasks = tasks;
        this.threshold = threshold;
        this.start = start;
        this.end = end;
        this.registry = registry;
    }

    public UndashTask(Int2ObjectMap<D> tasks, int threshold, DashRegistry registry) {
        final int size = tasks.size();
        var array = new Int2ObjectMap.Entry[size];
        this.tasks = tasks.int2ObjectEntrySet().toArray(array);
        this.threshold = threshold;
        this.start = 0;
        this.end = size;
        this.registry = registry;
    }


    @Override
    protected Int2ObjectOpenHashMap<U> compute() {
        if ((end - start) < threshold) {
            return computeDirectly();
        } else {
            final int middle = (start + end) / 2;
            UndashTask<U, D> subtaskA = new UndashTask<>(tasks, threshold, start, middle, registry);
            UndashTask<U, D> subtaskB = new UndashTask<>(tasks, threshold, middle, end, registry);
            subtaskA.fork();
            subtaskB.fork();
            return combine(subtaskA.join(), subtaskB.join());
        }
    }

    public final Int2ObjectOpenHashMap<U> combine(final Int2ObjectOpenHashMap<U> map, final Int2ObjectOpenHashMap<U> map2) {
        map.putAll(map2);
        return map;
    }

    protected final Int2ObjectOpenHashMap<U> computeDirectly() {
        final Int2ObjectOpenHashMap<U> count = new Int2ObjectOpenHashMap<>((int) ((end - start) / 0.75f));
        for (int i = start; i < end; i++) {
            final Int2ObjectMap.Entry<D> task = tasks[i];
            count.put(task.getIntKey(), task.getValue().toUndash(registry));
        }
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

        @Override
        protected void compute() {
            final int size = tasks.size();
            if (size < threshold) {
                computeDirectly();
            } else {
                final var half = size / 2;
                final ApplyTask subTask1 = new ApplyTask(tasks.subList(0, half), threshold, registry);
                final ApplyTask subTask2 = new ApplyTask(tasks.subList(half, size), threshold, registry);
                invokeAll(subTask1, subTask2);
            }
        }

        protected void computeDirectly() {
            tasks.forEach(dashable -> dashable.apply(registry));
        }
    }

}
