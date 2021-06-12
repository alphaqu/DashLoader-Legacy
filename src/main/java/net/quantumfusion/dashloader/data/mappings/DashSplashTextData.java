package net.quantumfusion.dashloader.data.mappings;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.util.VanillaData;

import java.util.List;

public class DashSplashTextData {
    @Serialize(order = 0)
    public List<String> splashList;

    public DashSplashTextData(@Deserialize("splashList") List<String> splashList) {
        this.splashList = splashList;
    }

    public DashSplashTextData(VanillaData data) {
        splashList = data.getSplashText();
    }

    public List<String> toUndash() {
        return splashList;
    }


}
