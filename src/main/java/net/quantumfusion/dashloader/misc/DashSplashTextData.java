package net.quantumfusion.dashloader.misc;

import io.activej.serializer.StringFormat;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeStringFormat;

import java.util.List;

public class DashSplashTextData {
    @Serialize(order = 0)
    @SerializeStringFormat(value = StringFormat.UTF8, path = {0, 0})
    public final List<String> splashList;

    public DashSplashTextData(@Deserialize("splashList") List<String> splashList) {
        this.splashList = splashList;
    }


    public List<String> toUndash() {
        return splashList;
    }


}
