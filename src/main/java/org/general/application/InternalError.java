package org.general.application;

/**
 * Exception that is thrown when an error occurs whose cause
 * is something that should not be revealed to the client.
 * 
 * An example of which is an error that occurs in a database module.
 * The cause of the exception would be helpful if logged, but
 * should not be exposed to users.
 * @author marcelpuyat
 *
 */
public class InternalError extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Default message that reveals no information to client besides
	 * fact that error was on server's end.
	 */
	private static final String DEFAULT_MESSAGE = "Internal error";
	
	public InternalError() {
        super(DEFAULT_MESSAGE);
    }
}
