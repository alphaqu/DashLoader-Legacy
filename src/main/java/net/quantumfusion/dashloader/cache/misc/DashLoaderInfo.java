package net.quantumfusion.dashloader.cache.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.quantumfusion.dashloader.DashLoader;

public class DashLoaderInfo {

    @Serialize(order = 0)
    public long modInfo;
    @Serialize(order = 1)
    public double dashFormatVersion;


    public DashLoaderInfo(@Deserialize("modInfo") long modInfo,
                          @Deserialize("dashFormatVersion") double dashFormatVersion) {
        this.modInfo = modInfo;
        this.dashFormatVersion = dashFormatVersion;
    }

    /**
     * <h1>Alpha stroke code</h1>
     * <h2>if you change it someone will get their kneecaps removed</h2>
     *
     * @return version to compare
     */
    public static DashLoaderInfo create() {
        long out = 420;
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            if (!modContainer.getMetadata().getId().equals("dashloader")) {
                ModMetadata metadata = modContainer.getMetadata();
                out += metadata.getVersion().getFriendlyString().chars().asLongStream().sum();
            }
        }
        return new DashLoaderInfo(~out + 0x69, DashLoader.formatVersion);
    }

    public boolean equals(DashLoaderInfo old) {
        return modInfo == old.modInfo && dashFormatVersion == old.dashFormatVersion;
    }
}
