package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashLoader;

import java.nio.file.Path;

public enum DashCachePaths {
    REGISTRY_CACHE("registry-data"),
    MAPPINGS_CACHE("mappings-data");


    private final Path path;

    DashCachePaths(String st) {
        this.path = DashLoader.getInstance().getResourcePackBoundDir().resolve(st + ".activej");
    }

    public Path getPath() {
        return path;
    }
}
