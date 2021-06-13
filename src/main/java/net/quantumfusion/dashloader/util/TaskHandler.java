package net.quantumfusion.dashloader.util;

import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;

public class TaskHandler {
    public static final int TOTALTASKS = 9;
    private static final float taskStep = 1f / TOTALTASKS;
    private String task;
    private final Logger logger;
    private int tasksComplete;

    private int subTotalTasks = 1;
    private int subTasksComplete = 0;

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

    public void setSubtasks(int tasks) {
        subTotalTasks = tasks;
        subTasksComplete = 0;
    }

    public void completedSubTask() {
        subTasksComplete++;
    }

    public Text getText() {
        return Text.of("(" + tasksComplete + "/" + TOTALTASKS + ") " + task);
    }

    public Text getSubText() {
        return TOTALTASKS == tasksComplete ? Text.of("") : Text.of("[" + subTasksComplete + "/" + subTotalTasks + "] ");
    }

    public double getProgress() {
        return (subTasksComplete == subTotalTasks && tasksComplete == TOTALTASKS) ? 1 : (tasksComplete == 0 ? 0 : tasksComplete / (float) TOTALTASKS) + (((float) subTasksComplete / subTotalTasks) * taskStep);
    }

    public TaskHandler(Logger logger) {
        task = "Starting DashLoader";
        tasksComplete = 0;
        this.logger = logger;
    }
}
