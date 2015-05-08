package org.general.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class JSONObject {
    /**
     * Classes that subclass off of this must provide an implementation for a
     * toString method that returns a string that adheres to valid JSON
     * formatting.
     */
    
    public static enum Type {
        LIST, MAP, NUMBER, STRING
    }
    
    private Type type;
    
    // Only one of these 4 will not be null, depending on the type of the JSONObject
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
     * Mapping from JSON characters that should be escaped to their appropriate
     * replacements. Static initializer sets up the hashmap for use in the
     * jsonEscape method.
     */
    private static HashMap<Character, String> escapeChars;
    static {
        escapeChars = new HashMap<Character, String>();
        escapeChars.put('\b', "\\b");
        escapeChars.put('\f', "\\f");
        escapeChars.put('\n', "\\n");
        escapeChars.put('\r', "\\r");
        escapeChars.put('\t', "\\t");
        escapeChars.put('\\', "\\\\");
        escapeChars.put('\"', "\\\"");
    }

    /**
     * Returns valid JSON as a string
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
                    .map(key -> jsonEscape(key)+": "+map.get(key).toJson())
                    .collect(Collectors.joining(", "))
                    + "}";
        }
            
        case STRING: return jsonEscape(str);
        case NUMBER: return String.valueOf(num);
        default: return null;
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
            if (escapeChars.containsKey(c)) {
                escapedString.append(escapeChars.get(c));
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
    public interface JSONSerializable {
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
    
    // Convenience functions for generating a JSONObject of type list
    
    /**
     * Returns a JSONObject of type list given a list of objects that are JSONSerializable
     */
    public static JSONObject fromSerializables(List<? extends JSONSerializable> list) {
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
     * Returns a JSONObject of type list given a list of numbers
     */
    public static JSONObject fromNumbers(List<? extends Number> list) {
        return new JSONObject(list.stream()
                .map(JSONObject::new)
                .collect(Collectors.toList()));
    }
}
