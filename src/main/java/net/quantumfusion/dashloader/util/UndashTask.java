package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.Dashable;
import net.quantumfusion.dashloader.model.DashModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class UndashTask<K, D extends Dashable> extends RecursiveTask<ArrayList<Map.Entry<Integer, K>>> {
    private final List<Map.Entry<Integer, D>> tasks;
    private final int threshold;
    private final DashRegistry registry;


    public UndashTask(List<Map.Entry<Integer, D>> tasks, int threshold, DashRegistry registry) {
        this.tasks = tasks;
        this.threshold = threshold;
        this.registry = registry;
    }


    @Override
    protected ArrayList<Map.Entry<Integer, K>> compute() {
        final int size = tasks.size();
        if (size < threshold) {
            return computeDirectly();
        } else {
            final int half = size / 2;
            final UndashTask<K, D> first = new UndashTask<>(tasks.subList(0, half), threshold, registry);
            final UndashTask<K, D> second = new UndashTask<>(tasks.subList(half, size), threshold, registry);
            invokeAll(first, second);
            return combine(first.join(), second.join());
        }
    }

    public final ArrayList<Map.Entry<Integer, K>> combine(final ArrayList<Map.Entry<Integer, K>> list, final ArrayList<Map.Entry<Integer, K>> list2) {
        list.ensureCapacity(list.size() * 2);
        list.addAll(list2);
        return list;
    }

    protected final ArrayList<Map.Entry<Integer, K>> computeDirectly() {
        final ArrayList<Map.Entry<Integer, K>> count = new ArrayList<>(tasks.size());
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
