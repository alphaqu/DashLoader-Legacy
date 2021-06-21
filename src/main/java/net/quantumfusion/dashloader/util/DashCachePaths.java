package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashLoader;

import java.nio.file.Path;

public enum DashCachePaths {
    REGISTRY_CACHE("registry-data"),
    MAPPINGS_CACHE("mappings-data");


    private final String path;

    DashCachePaths(String path) {
        this.path = path;
    }

    public Path getPath() {
        return DashLoader.getInstance().getResourcePackBoundDir().resolve(path + ".activej");

    }
}
