package org.general.json;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class for JSONObjects that are associative arrays, formatted as such: {"a":
 * "word", "b": 1.0, "c": true, "d": [], "e", {}}
 * 
 * Keys must be strings, and values can be any valid JSON primitive, as well as
 * JSONObjects.
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
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	public void put(String key, String value) throws NullPointerException {
		this.safePut(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	public void put(String key, Double value) throws NullPointerException {
		this.safePut(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	public void put(String key, Integer value) throws NullPointerException {
		this.safePut(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	public void put(String key, JSONObject value) throws NullPointerException {
		this.safePut(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	public void put(String key, Boolean value) throws NullPointerException {
		this.safePut(key, value);
	}

	/**
	 * Private version of put that checks if key is null and throws exception if
	 * so.
	 * 
	 * @param key
	 * @param value
	 * @throws NullPointerException
	 *             if key is null
	 */
	private void safePut(String key, Object value) throws NullPointerException {
		if (key == null) {
			throw new NullPointerException("Key for a JSONMap cannot be null");
		} else {
			map.put(key, value);
		}
	}

	/**
	 * Returns a string formatted as a valid JSON associative array. Strings are
	 * wrapped in double quotes and escaped. See class comments for info on
	 * formatting.
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
