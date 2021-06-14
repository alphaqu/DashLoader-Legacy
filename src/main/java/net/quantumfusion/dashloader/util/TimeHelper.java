package net.quantumfusion.dashloader.util;

import java.time.Duration;
import java.time.Instant;

public class TimeHelper {

    public static double getDecimalS(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).toMillis() / 100d) / 10d;
    }

    public static double getDecimalS(Instant start) {
        return getDecimalS(start, Instant.now());
    }

    public static double getMs(Instant start, Instant stop) {
        return Duration.between(start, stop).toMillis();
    }

    public static double getMs(Instant start) {
        return getMs(start, Instant.now());
    }

    public static float smartGetTime(Instant start, Instant stop) {
        final long millis = Duration.between(start, stop).toMillis();
        if (millis < 2000) {
            return millis;
        } else {
            return Math.round(millis / 100f) / 10f;
        }
    }
}
