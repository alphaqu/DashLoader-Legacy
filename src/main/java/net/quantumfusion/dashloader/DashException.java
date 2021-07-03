package net.quantumfusion.dashloader;

public class DashException extends RuntimeException {

    //no
    public DashException(String message) {
        super(message);
    }

    //no
    public DashException(String message, Exception e) {
        super(message, e);
    }
}
