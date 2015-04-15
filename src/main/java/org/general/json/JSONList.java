package org.general.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for JSONObjects that are arrays, formatted as such:
 * [1.0, "word", true, {}, []]
 * 
 * Elements can be any valid JSON primitive, as well as JSONObjects.
 * 
 * See http://json.org/ for more details on JSON formatting.
 * 
 * @author marcelpuyat
 *
 */
public class JSONList extends JSONObject {

    /**
     * Internal list that stores elements added. Holds elements of type Object,
     * but due to limitations on types of the add methods provided, will only
     * end up holding valid JSON types.
     */
    private List<Object> list;

    /**
     * Initializes empty JSONList
     */
    public JSONList() {
        list = new ArrayList<Object>();
    }

    /**
     * Adds a string to the JSONList. Can be null or empty.
     * @param val String to be added to list. Can be null or empty.
     */
    public void add(String val) {
        list.add(val);
    }

    /**
     * Adds a boolean to the JSONList. Can be null.
     * @param val Boolean to be added to list. Can be null.
     */
    public void add(Boolean val) {
        list.add(val);
    }

    /**
     * Adds an integer to the JSONList. Can be null.
     * @param val Integer to be added to list. Can be null.
     */
    public void add(Integer val) {
        list.add(val);
    }

    /**
     * Adds a double to the JSONList. Can be null.
     * @param val Double to be added to list. Can be null.
     */
    public void add(Double val) {
        list.add(val);
    }
    
    /**
     * Adds a long to the JSONList. Can be null.
     * @param val Long to be added to list. Can be null.
     */
    public void add(Long val) {
        list.add(val);
    }

    /**
     * Adds a JSONObject to the JSONList. Can be null or empty.
     * @param val JSONObject to be added to list. Can be null or empty.
     */
    public void add(JSONObject val) {
        list.add(val);
    }

    /**
     * Packs together varargs of JSONSerializable objects into a JSONList.
     * 
     * @param jSONSerializables Varargs of objects that implement JSONSerializable interface
     * @return JSONList of objects passed in, ordered in the same order passed in
     */
    public static JSONList toJSONList(JSONSerializable... jSONSerializables) {
        JSONList list = new JSONList();
        for (JSONSerializable jsonSerializables : jSONSerializables) {
            list.add(jsonSerializables.toJSON());
        }
        return list;
    }

    /**
     * Packs together a collection of JSON-serializable objects into a JSONList.
     * 
     * @param jSONSerializables Collection of objects that implement JSONSerializable interface
     * @return JSONList of objects passed in, ordered in the same order passed in
     */
    public static JSONList toJSONList(Collection<? extends JSONSerializable> jSONSerializables) {
        JSONList list = new JSONList();
        for (JSONSerializable jSONSerializable : jSONSerializables) {
            list.add(jSONSerializable.toJSON());
        }
        return list;
    }

    /**
     * Returns a string formatted as a valid JSON array.
     * See http://json.org/ for more details on JSON formatting.
     */
    public String toString() {
        String jsonString = "[";

        int count = 0;
        for (Object obj : list) {
            count++;
            jsonString += JSONObject.jsonEscape(obj);

            if (count < list.size()) {
                // Only insert commas until just before the last element
                jsonString += ", ";
            }
        }
        return jsonString + "]";
    }
    
    @Override
    /**
     * Equals another object if they are both JSONList types and
     * internal lists equal each other.
     */
    public boolean equals(Object other) {
    	return (other instanceof JSONList && ((JSONList)other).list.equals(this.list));
    }

}
