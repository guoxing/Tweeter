package org.general.json;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class for JSONObjects that are associative arrays, formatted as such: 
 * {"a": "word", "b": 1.0, "c": true, "d": [], "e": {}, "f": null}
 * 
 * Keys must be strings, and values can be any valid JSON primitive, as well as
 * JSONObjects.
 * 
 * See http://json.org/ for more details on JSON formatting.
 * 
 * @author marcelpuyat
 *
 */
public class JSONMap extends JSONObject {

    /**
     * Internal map that maps from JSON keys to values. Values are of type
     * Object, but due to restrictions on the value types in the implemented put
     * methods, will end up only being of valid JSON value types.
     */
    private HashMap<String, Object> map;

    /**
     * Initializes empty JSONMap
     */
    public JSONMap() {
        map = new HashMap<String, Object>();
    }

    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value Value that is a string. Can be null or empty.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, String value) throws NullPointerException {
        this.genericPut(key, value);
    }

    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value Value that is a double. Can be null.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, Double value) throws NullPointerException {
        this.genericPut(key, value);
    }
    
    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value Value that is a long. Can be null.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, Long value) throws NullPointerException {
        this.genericPut(key, value);
    }

    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value Value that is an integer. Can be null.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, Integer value) throws NullPointerException {
        this.genericPut(key, value);
    }

    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value Value that is a JSONObject. Can be null or empty.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, JSONObject value) throws NullPointerException {
        this.genericPut(key, value);
    }

    /**
     * @param key Key that is a string. Cannot be null. Can be empty.
     * @param value  Value that is an boolean. Can be null.
     * @throws NullPointerException Thrown if key is null
     */
    public void put(String key, Boolean value) throws NullPointerException {
        this.genericPut(key, value);
    }

    /**
     * Private version of put that checks if key is null and throws exception if
     * so. If not, will put key-value entry into map.
     * 
     * @param key Key that must not be null.
     * @param value Value that is limited to valid JSON value types through the
     * exposed public put methods.
     * @throws NullPointerException Thrown if key is null
     */
    private void genericPut(String key, Object value) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("Key for a JSONMap cannot be null");
        } else {
            map.put(key, value);
        }
    }

    /**
     * Returns a string formatted as a valid JSON associative array.
     * See http://json.org/ for more details on JSON formatting.
     */
    public String toString() {
        String jsonString = "{";

        Set<Entry<String, Object>> entries = map.entrySet();
        int count = 0;
        for (Entry<String, Object> entry : entries) {
            count++;
            jsonString += JSONObject.jsonEscape(entry.getKey()) + ": "
                    + JSONObject.jsonEscape(entry.getValue());

            if (count < entries.size()) {
                // Only insert commas until just before the last element
                jsonString += ", ";
            }
        }
        return jsonString + "}";
    }
    
    @Override
    /**
     * Equals another object if they are both JSONList types and
     * internal lists equal each other.
     */
    public boolean equals(Object other) {
    	return (other instanceof JSONMap && ((JSONMap)other).map.equals(this.map));
    }
}
