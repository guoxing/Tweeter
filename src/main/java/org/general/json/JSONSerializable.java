package org.general.json;

/**
 * Interface for classes that want to be JSON-serializable. A class that adheres
 * to this interface this must use JSONMap and/or JSONList in the toJSON method
 * to build its JSONObject as desired.
 * 
 * See http://json.org/ for more details on JSON formatting.
 * 
 * @author marcelpuyat
 *
 */
public interface JSONSerializable {

    /**
     * Returns a valid JSONObject that represents instance of the class that
     * adheres to the JSONSerializable interface.
     * 
     * @return JSONObject JSON representing the instance of the class that
     *         adheres to the JSONSerializable interface
     */
    public JSONObject toJSON();
}
