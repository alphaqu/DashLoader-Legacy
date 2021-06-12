package net.quantumfusion.dashloader.util;

import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;

public class TaskHandler {
    public static final int TOTALTASKS = 22;
    private String task;
    private Logger logger;
    private int tasksComplete;

    public void logAndTask(String s) {
        logger.info(s);
        tasksComplete++;
        task = s;
    }

    public void completedTask() {
        tasksComplete++;
    }

    public void setCurrentTask(String task) {
        this.task = task;
    }

    public Text getText() {
        return Text.of("(" + tasksComplete + "/" + TOTALTASKS + ") " + task);
    }

    public double getProgress() {
        return tasksComplete == 0 ? 0 : tasksComplete / (float) TOTALTASKS;
    }

    public TaskHandler(Logger logger) {
        task = "Starting DashLoader";
        tasksComplete = 0;
        this.logger = logger;
    }
}
