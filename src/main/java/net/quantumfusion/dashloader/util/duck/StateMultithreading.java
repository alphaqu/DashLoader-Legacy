package net.quantumfusion.dashloader.util.duck;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StateMultithreading {
    @Nullable
    public static List<Callable<Object>> tasks = new ArrayList<>();
}
