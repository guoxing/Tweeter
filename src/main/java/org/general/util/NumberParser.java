package org.general.util;

import java.util.Scanner;

/**
 * Utility class for determining if a string is a representation
 * of a number or not.
 * @author marcelpuyat
 *
 */
public class NumberParser {
    /** For details on exactly what determines if a string is an integer, see 
     * {@link java.util.Scanner#nextInt}
     */
    
    /**
     * True if str represents an integer, false if not.
     */
    public static boolean isNumber(String str, int radix) {
        Scanner sc = new Scanner(str.trim());
        try {
            if(!sc.hasNextInt(radix)) return false;
            sc.nextInt(radix);
            // At this point, if we had a valid integer, there should be no more tokens
            return !sc.hasNext();
        } finally { 
            sc.close(); 
        }
    }
    
    /**
     * True if str represents an integer (base 10), false if not.
     */
    public static boolean isNumber(String str) {
        return isNumber(str, 10);
    }
}
