package net.quantumfusion.dashloader.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.quantumfusion.dashloader.DashLoader;
import net.quantumfusion.dashloader.util.ReloadEnum;

public class DashMetadata {

    @Serialize(order = 0)
    public long modInfo;
    @Serialize(order = 1)
    public double dashFormatVersion;


    public DashMetadata(@Deserialize("modInfo") long modInfo,
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
    public static DashMetadata create() {
        long out = 420;
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            if (!modContainer.getMetadata().getId().equals("dashloader")) {
                ModMetadata metadata = modContainer.getMetadata();
                out += metadata.getVersion().getFriendlyString().chars().asLongStream().sum();
            }
        }
        return new DashMetadata(~out + 0x69, DashLoader.formatVersion);
    }

    public ReloadEnum getState(DashMetadata old) {
        return modInfo != old.modInfo ? ReloadEnum.MOD_CHANGE : dashFormatVersion != old.dashFormatVersion ? ReloadEnum.FORMAT_CHANGE : ReloadEnum.ACCEPT;
    }
}
