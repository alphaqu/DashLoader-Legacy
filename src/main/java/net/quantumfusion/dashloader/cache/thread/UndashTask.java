package net.quantumfusion.dashloader.cache.thread;

import net.quantumfusion.dashloader.util.Dashable;

import java.util.concurrent.RecursiveTask;

public class UndashTask<K> extends RecursiveTask<K> {

    Dashable dashable;

    public UndashTask(Dashable dashable) {
        this.dashable = dashable;
    }

    @Override
    protected K compute() {
        return dashable.toUndash();
    }
}
