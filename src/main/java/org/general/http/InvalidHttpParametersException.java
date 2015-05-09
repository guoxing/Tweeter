package org.general.http;

/**
 * These exceptions indicate situations wherein a parameter was supposed to exist but did not,
 * or wherein a parameter's type was not what we expected.
 * @author marcelpuyat
 *
 */
public class InvalidHttpParametersException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InvalidHttpParametersException(String msg) {
        super(msg);
    }

}
