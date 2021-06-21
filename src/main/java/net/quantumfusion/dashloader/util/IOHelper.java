package net.quantumfusion.dashloader.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class IOHelper {

    public static int[] toArray(IntBuffer buffer, int size) {
        int[] bufferOut = new int[size];
        buffer.put(bufferOut);
        return bufferOut;
    }

    public static float[] toArray(FloatBuffer buffer, int size) {
        float[] bufferOut = new float[size];
        buffer.put(bufferOut);
        return bufferOut;
    }
}
