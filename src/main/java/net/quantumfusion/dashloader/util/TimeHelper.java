package net.quantumfusion.dashloader.util;

import java.time.Duration;
import java.time.Instant;

public class TimeHelper {

    public static double getS1Decimal(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).toMillis() / 100d) / 10d;
    }

    public static double getS1Decimal(Instant start) {
        return getS1Decimal(start, Instant.now());
    }


}
