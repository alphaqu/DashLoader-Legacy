package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.DashRegistry;

public class EmptyData implements DataClass {
    @Override
    public void reload(DashRegistry registry) {

    }

    @Override
    public void apply(DashRegistry registry) {

    }

    @Override
    public void serialize(DashRegistry registry) {

    }
}
