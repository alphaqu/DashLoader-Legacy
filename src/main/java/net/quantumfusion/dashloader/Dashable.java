package net.quantumfusion.dashloader;

public interface Dashable<K> {
    K toUndash(DashRegistry registry);
}
