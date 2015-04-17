package org.general.data;

/**
 * This exception is thrown when an invalid data format occurs while using
 * AppData.
 *
 * @author Guoxing Li
 *
 */
public class InvalidDataFormattingException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidDataFormattingException(String str) {
        super(str);
    }
}