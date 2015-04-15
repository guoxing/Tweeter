package org.general.logger;

/**
 * Logger for printing out useful information for debugging/dev purposes.
 * @author marcelpuyat
 *
 */
public class Logger {
	/**
	 * Prints to stdout the message passed in, prefixed with the class
	 * wherein this method was called from.
	 * 
	 * Note that this will not print anything out if called from within
	 * the logger class.
	 * @param message Message to be logged to stdout
	 */
    public static void log(String message) {
    	if (Thread.currentThread().getStackTrace().length > 1) {
	        System.out.println(Thread.currentThread()
	        		.getStackTrace()[2].getClassName()+"\n\t"+message);
    	}
    }
}
