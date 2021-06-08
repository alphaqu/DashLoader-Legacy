package net.quantumfusion.dashloader.util;

import java.time.Duration;
import java.time.Instant;

public class TimeHelper {
    public static double getDecimalSeconds(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).toMillis() / 100d) / 10d;
    }
}
