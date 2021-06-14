package net.quantumfusion.dashloader.util;

import it.unimi.dsi.fastutil.ints.IntComparator;

public class SuffixCreator {


    public static IntComparator get(int[] js, int[] ks) {
        return new IntComparator() {
            public int compare(int i, int j) {
                return js[i] == js[j] ? Integer.compare(ks[i], ks[j]) : Integer.compare(js[i], js[j]);
            }

            public int compare(Integer integer, Integer integer2) {
                return this.compare(integer, integer2);
            }
        };
    }

}
