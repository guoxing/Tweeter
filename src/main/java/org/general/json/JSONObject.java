package org.general.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An immutable class that represents JSON formatting.
 * 
 * See http://json.org/ for more details on JSON formatting.
 * 
 * @author marcelpuyat
 *
 */
public class JSONObject {
    public static enum Type {
        LIST, MAP, NUMBER, STRING
    }
    private Type type;
    
    // Only 1 of these 4 will not be null, depending on the type of the JSONObject
    private Map<String, JSONObject> map;
    private List<JSONObject> list;
    private String str;
    private Number num;
    
    public JSONObject(Number num) { this.type = Type.NUMBER; this.num = num; }
    public JSONObject(String str) { this.type = Type.STRING; this.str = str; }
    public JSONObject(Collection<? extends JSONObject> list) { 
        this.type = Type.LIST;
        this.list = new ArrayList<JSONObject>(list); 
    }
    public JSONObject(Map<String, ? extends JSONObject> map) { 
        this.type = Type.MAP;
        this.map = new HashMap<String, JSONObject>(map); 
    }

    /**
     * Mapping from characters (that are not unicode) that should be escaped 
     * to their appropriate replacements. Static initializer sets up the hashmap
     * for use in the jsonEscape method.
     */
    private static HashMap<Character, String> nonUnicodeEscapeChars;
    static {
        nonUnicodeEscapeChars = new HashMap<Character, String>();
        nonUnicodeEscapeChars.put('\b', "\\b");
        nonUnicodeEscapeChars.put('\f', "\\f");
        nonUnicodeEscapeChars.put('\n', "\\n");
        nonUnicodeEscapeChars.put('\r', "\\r");
        nonUnicodeEscapeChars.put('\t', "\\t");
        nonUnicodeEscapeChars.put('\\', "\\\\");
        nonUnicodeEscapeChars.put('\"', "\\\"");
    }

    /**
     * Returns string form (in valid JSON formatting) of the internal object. 
     */
    public String toJson() {
        switch (type) {
            case LIST: {
                return "[" + list.stream()
                        .map(JSONObject::toJson)
                        .collect(Collectors.joining(", "))
                        + "]";
            }
            case MAP: {
                return "{" + map.keySet().stream()
                        .map(key -> 
                            jsonEscape(key) + ": " + map.get(key).toJson())
                        .collect(Collectors.joining(", "))
                        + "}";
            }
            case STRING: return jsonEscape(str);
            case NUMBER: return String.valueOf(num);
            default: return null; // Will never reach here bec type is always declared.
        }
    }

    /**
     * Given a string that may or may not conform to proper JSON string format,
     * will return a string that does adheres to JSON format.
     * 
     * @param val
     *            string to be escaped
     * @return json-escaped string
     */
    private static String jsonEscape(String str) {
        if (str == null) return "null";
        StringBuilder escapedString = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (nonUnicodeEscapeChars.containsKey(c)) {
                escapedString.append(nonUnicodeEscapeChars.get(c));
            }
            else if (Character.isISOControl(c)) {
                escapedString.append("\\u");
                escapedString.append(String.format("%04x", (int) c));
            } else {
                escapedString.append(c);
            }
        }
        return "\"" + escapedString.toString() + "\"";
    }
    
    /**
     * Interface for classes that can convert themselves into JSON form, using the JSONObject class.
     * @author marcelpuyat
     *
     */
    public interface JSONable {
        public JSONObject toJsonObject();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JSONObject)) return false;
        JSONObject otherAsJson = (JSONObject)other;
        if (otherAsJson.type != this.type) return false;
        switch (this.type) {
            case NUMBER:
                return this.num.equals(otherAsJson.num);
            case STRING:
                return this.str.equals(otherAsJson.str);
            case LIST:
                return this.list.equals(otherAsJson.list);
            case MAP:
                return this.map.equals(otherAsJson.map);
            default:
                // Should never reach here
                return false;
        }
    }
    
    // Convenience methods for generating a JSONObject of type list
    
    /**
     * Returns a JSONObject of type list given a list of objects that are JSONSerializable
     */
    public static JSONObject fromJSONables(List<? extends JSONable> list) {
        return new JSONObject(list.stream()
                .map(jsonable -> jsonable.toJsonObject())
                .collect(Collectors.toList()));
    }
    
    /**
     * Returns a JSONObject of type list given a list of strings
     */
    public static JSONObject fromStrings(List<? extends String> list) {
        return new JSONObject(list.stream()
                .map(JSONObject::new)
                .collect(Collectors.toList()));
    }
    
    /**
     * Returns a JSONObject of type list given a list of numbers.
     * 
     * Note that although the body of this method is the same as
     * that of fromStrings, they refer to different JSONObject::new
     * methods and thus cannot be combined.
     */
    public static JSONObject fromNumbers(List<? extends Number> list) {
        return new JSONObject(list.stream()
                .map(JSONObject::new)
                .collect(Collectors.toList()));
    }
}
