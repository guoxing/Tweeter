package org.general.json;

/**
 * Interface for classes that want to be JSON-serializable. A class that
 * implement this must use JSONMap and/or JSONList in the toJSON method to build
 * its JSONObject as desired.
 * 
 * @author marcelpuyat
 *
 */
public interface JSONable {

	/**
	 * Returns a valid JSONObject
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJSON();
}
