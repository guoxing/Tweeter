package json;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class for JSONObjects that are associative arrays, formatted as such:
 *    {"a": "word", "b": 1.0, "c": true, "d": [], "e", {}}
 *    
 * Keys must be strings, and values can be any valid JSON primitive, 
 * as well as JSONObjects.
 * @author marcelpuyat
 *
 */
public class JSONMap extends JSONObject {
	
	/**
	 * Internal map that maps from JSON keys to values. Values are of type Object,
	 * but due to restrictions on the value types in the implemented put methods,
	 * will end up only being of valid JSON value types.
	 */
	HashMap<String, Object> map;
	
	/**
	 * Initializes empty JSONMap
	 */
	public JSONMap() {
		map = new HashMap<String, Object>();
	}
	
	/* The following methods all simply add to the internal list.
	 * 5 different types of them are needed to support only the valid JSON types
	 */
	
	public void put(String key, String value) {
		map.put(key, value);
	}
	public void put(String key, Double value) {
		map.put(key, value);
	}
	public void put(String key, Integer value) {
		map.put(key, value);
	}
	public void put(String key, JSONObject value) {
		map.put(key, value);
	}
	public void put(String key, Boolean value) {
		map.put(key, value);
	}

	/**
	 * Returns a string formatted as a valid JSON associative array. Strings are wrapped
	 * in double quotes and escaped. See class comments for info on formatting.
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
}
