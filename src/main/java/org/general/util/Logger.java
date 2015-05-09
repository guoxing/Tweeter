package org.general.util;

/**
 * Logger for printing out useful information for debugging/dev purposes.
 * 
 * @author marcelpuyat
 *
 */
public class Logger {
    /**
     * Prints to stdout the message passed in, prefixed with the class and
     * method wherein this log method was called from.
     * 
     * Note that this will not print anything out if called from within the
     * logger class.
     * 
     * @param message
     *            Message to be logged to stdout
     */
    public static void log(String message) {
        if (Thread.currentThread().getStackTrace().length > 1) {
            StackTraceElement elemBeforeLogMethod = Thread.currentThread()
                    .getStackTrace()[2];
            System.out.println(elemBeforeLogMethod.getClassName() + "#"
                    + elemBeforeLogMethod.getMethodName() + "\n\t" + message);
        }
    }
}
