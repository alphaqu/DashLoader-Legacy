package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.DashRegistry;

public interface DataClass {
    void reload(DashRegistry registry);

    void apply(DashRegistry registry);

    void serialize(DashRegistry registry);
}
