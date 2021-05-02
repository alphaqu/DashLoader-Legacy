package net.quantumfusion.dashloader.misc;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.cache.DashRegistry;
import net.quantumfusion.dashloader.util.Dashable;

import java.util.List;

public class DashSplashTextData{
    @Serialize(order = 0)
    public final List<String> splashList;

    public DashSplashTextData(@Deserialize("splashList") List<String> splashList) {
        this.splashList = splashList;
    }


    public <K> K toUndash(DashRegistry registry) {
        return (K) splashList;
    }


}
