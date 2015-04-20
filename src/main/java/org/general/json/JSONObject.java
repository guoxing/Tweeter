package org.general.json;

import java.util.HashMap;

/**
 * Abstract class that represents a JSON object. The interface of this class
 * permits one only to retrieve (not modify) the JSON representation of an
 * instance using its toString method.
 * 
 * See http://json.org/ for more details on JSON formatting.
 * 
 * @author marcelpuyat
 *
 */
public abstract class JSONObject {
    /**
     * Classes that subclass off of this must provide an implementation for a
     * toString method that returns a string that adheres to valid JSON
     * formatting.
     */

    /**
     * Mapping from JSON characters that should be escaped to their appropriate
     * replacements. Static initializer sets up the hashmap for use in the
     * jsonEscape method.
     * 
     * Note that backslashes are escaped twice due to regex conventions on
     * backslashes.
     */
    private static HashMap<String, String> escapeChars;
    static {
        escapeChars = new HashMap<String, String>();
        escapeChars.put("\b", "\\\\b");
        escapeChars.put("\n", "\\\\n");
        escapeChars.put("\f", "\\\\f");
        escapeChars.put("\r", "\\\\r");
        escapeChars.put("\t", "\\\\t");
        escapeChars.put("\"", "\\\\\"");
        escapeChars.put("\\\\", "\\\\\\\\");
        escapeChars.put("/", "\\\\/");
    }

    /**
     * Returns valid JSON as a string
     */
    public abstract String toString();

    /**
     * Returns string form of object that adheres to JSON format.
     * 
     * Rules: If object is a string, will replace special characters (see
     * escapeChars) and wrap string in double quotes
     * 
     * If object is null, will return "null" (note this is a non-empty String of
     * 4 characters, and not a null value)
     * 
     * Else, simply returns the return value of the object's toString method.
     * 
     * @param val
     *            Object to be escaped
     * @return json-escaped string form of object
     */
    public static String jsonEscape(Object val) {
        if (val == null) {
            return "null";
        } else if (val instanceof String) {
            return "\"" + replaceSpecialChars(val.toString()) + "\"";
        } else {
            return val.toString();
        }
    }

    /**
     * Uses statically initialized hashmap of characters to be escaped (and
     * their replacements) to return a string with these characters replaced
     * appropriately.
     * 
     * @param str
     *            String to be escaped
     * @return escaped string
     */
    private static String replaceSpecialChars(String str) {
        for (String charToEscape : escapeChars.keySet()) {
            str = str.replaceAll(charToEscape, escapeChars.get(charToEscape));
        }
        return str;
    }

    /**
     * Subclasses must implement equals method.
     */
    public abstract boolean equals(Object other);
}
