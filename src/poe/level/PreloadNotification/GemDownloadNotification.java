package poe.level.PreloadNotification;

import javafx.application.Preloader;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Preloader notification that reports a new gem download.
 * This is delivered to preloader .
 */

public class GemDownloadNotification implements Preloader.PreloaderNotification {
    // Too bad this isn't already available in a Java core class
    private static final String lineSeparator;

    static {
        String prop = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("line.separator"));
        lineSeparator = prop != null ? prop : "\n";
    }

    private String gemName;

    /**
     * Constructs an error notification.
     *
     * @param gemName a string describing the gem name; must be non-null
     */
    public GemDownloadNotification(String gemName) {
        if (gemName == null) throw new NullPointerException();

        this.gemName = gemName;
    }


    /**
     * Retrieves the gem name .
     * It may be the empty string, but is always non-null.
     *
     * @return the gem name
     */
    public String getGemName() {
        return gemName;
    }


    /**
     * Returns a string representation of this {@code ErrorNotification} object.
     * @return a string representation of this {@code ErrorNotification} object.
     */
    @Override public String toString() {
        StringBuilder str = new StringBuilder("Preloader.GemDownloadNotification: ");

        return str.toString();
    }
}
