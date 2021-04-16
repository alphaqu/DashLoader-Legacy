package net.quantumfusion.dash.util;

import java.time.Duration;
import java.time.Instant;

public class TimeHelper {

    public static double getDecimalMs(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).getNano() / 100000.0) / 10.0;
    }
}
