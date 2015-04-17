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
     * Adds an object to the JSONList.  Must be a valid JSON value type.
     * @param val 
     *              Object to be added to list. Must be a valid
     *              JSON value type.
     * @throws IllegalArgumentException
     *              Thrown if val is not a valid JSON value type
     */
    public void add(Object val) {
        if (!isValidJSONValueType(val)) {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE_TYPE_MESSAGE);
        }
        list.add(val);
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
    	return (other instanceof JSONList && 
    	        ((JSONList)other).list.equals(this.list));
    }

}
