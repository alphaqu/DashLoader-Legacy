package net.quantumfusion.dash.cache.misc;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class DashLoaderInfo {

    long modInfo;

    public DashLoaderInfo(long modInfo) {
        this.modInfo = modInfo;
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
            ModMetadata metadata = modContainer.getMetadata();
            out += metadata.getVersion().getFriendlyString().chars().asLongStream().sum();
        }
        return new DashLoaderInfo(~out + 0x69);
    }
}
