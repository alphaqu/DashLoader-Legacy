package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class UndashTask<K, D extends Dashable> extends RecursiveTask<Collection<Map.Entry<Long, K>>> {
    private final List<Map.Entry<Long, D>> tasks;
    private final int threshold;
    private final DashRegistry registry;


    public UndashTask(List<Map.Entry<Long, D>> tasks, int threshold, DashRegistry registry) {
        this.tasks = tasks;
        this.threshold = threshold;
        this.registry = registry;
    }

    public UndashTask<K, D>[] split(List<Map.Entry<Long, D>> list, int size) {
        final int half = (int) Math.ceil(size / 2f);
        List<Map.Entry<Long, D>> first = new ArrayList<>(half);
        List<Map.Entry<Long, D>> second = new ArrayList<>(half);
        final int i1 = size / 2;
        for (int i = 0; i < i1; i++)
            first.add(list.get(i));
        for (int i = i1; i < size; i++)
            second.add(list.get(i));
        return new UndashTask[]{new UndashTask<>(first, threshold, registry), new UndashTask<>(second, threshold, registry)};
    }

    @Override
    protected Collection<Map.Entry<Long, K>> compute() {
        final int size = tasks.size();
        if (size < threshold) {
            return computeDirectly();
        } else {
            final UndashTask<K, D>[] split = split(tasks, size);
            final UndashTask<K, D> first = split[0];
            final UndashTask<K, D> second = split[1];
            invokeAll(first, second);
            return combine(first.join(), second.join());
        }
    }

    public final Collection<Map.Entry<Long, K>> combine(final Collection<Map.Entry<Long, K>> list, final Collection<Map.Entry<Long, K>> list2) {
        list.addAll(list2);
        return list;
    }

    protected final Collection<Map.Entry<Long, K>> computeDirectly() {
        final Collection<Map.Entry<Long, K>> count = new ArrayList<>(tasks.size());
        tasks.forEach(dashable -> count.add(Pair.of(dashable.getKey(), dashable.getValue().toUndash(registry))));
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
