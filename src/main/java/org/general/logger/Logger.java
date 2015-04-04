package org.general.logger;

public class Logger {
    public static void log(String message) {
        System.out.println(Thread.currentThread().getStackTrace()[2].getClassName()+"\n\t"+message);
    }
}
