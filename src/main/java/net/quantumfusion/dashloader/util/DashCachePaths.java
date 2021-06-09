package net.quantumfusion.dashloader.util;

import net.quantumfusion.dashloader.DashLoader;

import java.nio.file.Path;

public enum DashCachePaths {
    DASH_METADATA("metadata"),
    MODELS("model-mappings"),
    SPRITEATLAS("spriteatlas-mappings"),
    BLOCKSTATE("blockstate-mappings"),
    PARTICLE("particle-mappings"),
    FONT("font-mappings"),
    SPLASH("splash-mappings"),
    REGISTRY_BLOCKSTATE("blockstate-registry"),
    REGISTRY_FONT("font-registry"),
    REGISTRY_IDENTIFIER("identifier-registry"),
    REGISTRY_IMAGE("image-registry"),
    REGISTRY_MODEL("model-registry"),
    REGISTRY_PREDICATE("predicate-registry"),
    REGISTRY_PROPERTY("property-registry"),
    REGISTRY_PROPERTYVALUE("propertyvalue-registry"),
    REGISTRY_SPRITE("sprite-registry");

    private final Path path;

    DashCachePaths(String st) {
        this.path = DashLoader.getConfig().resolve("quantumfusion/dashloader/" + st + ".activej");
    }

    public Path getPath() {
        return path;
    }
}
