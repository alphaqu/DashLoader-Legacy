package net.quantumfusion.dash.util;

import java.time.Duration;
import java.time.Instant;

public class TimeHelper {

    public static float getDecimalMs(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).getNano() / (double)100000) / 10f;
    }
}
