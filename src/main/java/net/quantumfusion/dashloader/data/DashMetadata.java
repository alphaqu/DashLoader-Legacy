package net.quantumfusion.dashloader.data;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Collection;

public class DashMetadata {
    public String modInfo;
    public String resourcePacks;


    public DashMetadata() {

    }

    public void setMods(FabricLoader loader) {
        long modInfoData = 0;
        for (ModContainer mod : loader.getAllMods()) {
            for (char c : mod.getMetadata().getVersion().getFriendlyString().toCharArray()) {
                modInfoData += c;
            }
        }
        modInfo = Long.toHexString(modInfoData);
    }

    public void setResourcePacks(Collection<String> resourcePacks) {
        long resourcePackData = 0;
        for (String resourcePack : resourcePacks) {
            for (char c : resourcePack.toCharArray()) {
                resourcePackData += c;
            }
        }
        this.resourcePacks = Long.toHexString(resourcePackData);
    }


    public String getId() {
        return modInfo + "-" + resourcePacks;
    }

}
