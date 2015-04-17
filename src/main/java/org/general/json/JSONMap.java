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
     * Associates key with value in JSONMap.
     * 
     * Key must not be null, and value must be a valid JSON value type.
     * 
     * @param key 
     *              Key that must not be null.
     * @param value 
     *              Value that must be a valid JSON value type
     * @throws NullPointerException 
     *              Thrown if key is null
     * @throws IllegalArgumentException
     *              Thrown if value is not a valid JSON value type
     */
    public void put(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("Key for a JSONMap cannot be null");
        } else if (!isValidJSONValueType(value)) {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE_TYPE_MESSAGE);
        } else {
            map.put(key, value);
        }
    }

    /**
     * Returns a string formatted as a valid JSON associative array.
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
