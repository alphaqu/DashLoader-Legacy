package net.quantumfusion.dashloader.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class TimeHelper {

    public static double getDecimalMs(Instant start, Instant stop) {
        return Math.round(Duration.between(start, stop).toMillis() / 100d) / 10d;
    }
}
