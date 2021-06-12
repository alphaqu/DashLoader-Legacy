package net.quantumfusion.dashloader.data;

public class DashMetadata {
    public long modInfo;

    public long resourcePacks;


    public DashMetadata(long modInfo, long resourcePacks) {
        this.modInfo = modInfo;
        this.resourcePacks = resourcePacks;
    }

    public String getId() {
        return modInfo + "-" + resourcePacks;
    }

}
