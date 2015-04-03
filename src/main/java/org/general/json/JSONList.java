package org.general.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for JSONObjects that are arrays, formatted as such: [1.0, "word", true,
 * {}, []]
 * 
 * Elements can be any valid JSON primitive, as well as JSONObjects.
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

    /*
     * The following methods all simply add to the internal list. 5 different
     * types of them are needed to support only the valid JSON types
     */
    public void add(String val) {
        list.add(val);
    }

    public void add(Boolean val) {
        list.add(val);
    }

    public void add(Integer val) {
        list.add(val);
    }

    public void add(Double val) {
        list.add(val);
    }
    
    public void add(Long val) {
        list.add(val);
    }

    public void add(JSONObject val) {
        list.add(val);
    }

    /**
     * Packs together an array of JSONabble objects into a JSONList.
     * 
     * @param jSONabbles
     *            Collection of objects that implement JSONabble interface
     * @return ordered JSONList of objects passed in
     */
    public static JSONList toJSONList(JSONable... jSONabbles) {
        JSONList list = new JSONList();
        for (JSONable jSONabble : jSONabbles) {
            list.add(jSONabble.toJSON());
        }
        return list;
    }

    /**
     * Packs together a collection of JSONabble objects into a JSONList.
     * 
     * @param jSONabbles
     *            Collection of objects that implement JSONabble interface
     * @return ordered JSONList of objects passed in
     */
    public static JSONList toJSONList(Collection<? extends JSONable> jSONabbles) {
        JSONList list = new JSONList();
        for (JSONable jSONabble : jSONabbles) {
            list.add(jSONabble.toJSON());
        }
        return list;
    }

    /**
     * Returns a string formatted as a valid JSON array. Strings elements are
     * wrapped in double quotes and escaped. See class comments for info on
     * formatting.
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

}
