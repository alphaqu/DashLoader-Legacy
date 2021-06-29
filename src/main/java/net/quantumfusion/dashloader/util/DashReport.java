package net.quantumfusion.dashloader.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DashReport {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Entry> entries = new ArrayList<>();
    private static final List<Pair<Instant, String>> times = new ArrayList<>();
    private static final int BARSIZE = 60;
    private static final char BARCHAR = '*';

    public static void addEntry(Entry entry) {
        entries.add(entry);
    }

    public static void addTime(Instant time, String text) {
        times.add(Pair.of(time, text));
    }

    public static void printReport() {
        printBorder("Minecraft Samples");
        List<Entry> dashEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if (!entry.dashReport) {
                printEntry(entry);
            } else {
                dashEntries.add(entry);
            }
        }
        printBorder("DashLoader Samples");
        for (Entry dashEntry : dashEntries) {
            printEntry(dashEntry);
        }
        printBorder("Launch Time");
        printLaunch();
        printBorder("DashReport End");
    }

    private static void printLaunch() {
        final Instant stop = Instant.now();
        for (Pair<Instant, String> time : times) {
            LOGGER.info("--- [{}] {}", TimeHelper.smartGetTime(time.getLeft(), stop), time.getRight());
        }
    }

    private static void printBorder(String text) {
        final String border = getBorder(text.length());
        LOGGER.info("{}[{}]{}", border, text, border);
    }

    public static String getBorder(int textLength) {
        return String.valueOf(BARCHAR).repeat(Math.max(0, Math.round((BARSIZE - textLength) / 2f)));
    }

    private static void printEntry(Entry entry) {
        LOGGER.info("--- [{}] {}", TimeHelper.smartGetTime(entry.start, entry.stop), entry.name);
    }

    public static class Entry {
        Instant start;
        Instant stop;
        String name;
        boolean dashReport;

        public Entry(Instant start, String name, boolean dashReport) {
            this.start = start;
            this.stop = Instant.now();
            this.name = name;
            this.dashReport = dashReport;
        }
    }


}
