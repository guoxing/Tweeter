package org.general.application.mvc;

import java.util.List;

public abstract class AppData {
    
    protected static final String DELIMITER = " ";
    protected static final String FRIENDSHIP_DATA_FILENAME = "friend_data.txt";
    protected static final String STATUS_DATA_FILENAME = "friend_data.txt";
    
    protected static void appendToFile(String file, List<String> fields) {
        
    }
    
    public abstract void recover();
    
    @SuppressWarnings("serial")
    public static class InvalidDataFormattingException extends Exception {
        public InvalidDataFormattingException(String str) {
            super(str);
        }
        
        public String getMessage() {
            return super.getMessage();
        }
    }
}
