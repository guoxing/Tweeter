package org.general.application;

import java.util.Map;

import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.StatusCode;
import org.general.json.JSONMap;

/**
 * Abstract class that is the base controller for application logic.
 * 
 * Provides methods for subclasses to generate AppResponses and retrieve
 * required and/or optional parameters.
 * 
 * @author marcelpuyat
 *
 */
public abstract class Controller {
    /**
     * Retrieves a string value associated with a passed in string key from
     * params map. Use this when the controller action requires the parameter
     * associated with this key to exist.
     * 
     * @param paramKey
     *            Required string parameter
     * @param params
     *            Parameters map
     * @return Long value associated with key passed in
     * @throws IllegalArgumentException
     *             Thrown if map does not contain given key
     */
    public static String getRequiredString(String paramKey,
            Map<String, String> params) throws IllegalArgumentException {
        String val = params.get(paramKey);
        if (val == null) {
            throw new IllegalArgumentException(paramKey
                    + " is a required parameter");
        }
        return val;
    }

    /**
     * Retrieves a long value associated with a passed in string key from params
     * map. Use this when the controller action requires the parameter
     * associated with this key to exist.
     * 
     * @param paramKey
     *            Required string parameter
     * @param params
     *            Parameters map
     * @return String value associated with key passed in
     * @throws IllegalArgumentException
     *             Thrown if map does not contain given key or if map contains
     *             given key but value cannot be parsed into a long
     */
    public static Long getRequiredLong(String paramKey,
            Map<String, String> params) throws IllegalArgumentException {

        if (params.get(paramKey) == null) {
            throw new IllegalArgumentException(paramKey
                    + " is a required parameter");
        }

        Long val = null;
        try {
            val = Long.parseLong(params.get(paramKey));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(paramKey
                    + " must be a 64-bit integer");
        }

        return val;
    }

    /**
     * Retrieves a long value associated with a passed in string key from params
     * map. Use this when the controller action does not require the parameter
     * associated with this key to exist (it is merely optional).
     * 
     * @param paramKey
     *            Optional string parameter
     * @param params
     *            Parameters map
     * @param defaultValue
     *            Default value to be returned if params does not contain value
     * @return String value associated with key passed in, or defaultValue if
     *         params does not contain this parameter.
     * @throws IllegalArgumentException
     *             Thrown if map contains given key but value cannot be parsed
     *             into a long
     */
    public static Long getOptionalLongOrDefault(String paramKey,
            Map<String, String> params, Long defaultValue)
            throws IllegalArgumentException {
        if (params.get(paramKey) == null)
            return defaultValue;

        Long val = null;
        try {
            val = Long.parseLong(params.get(paramKey));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(paramKey
                    + " must be a 64-bit integer");
        }

        return val;
    }
    
    public static void respondWithJSONError(StatusCode code, String errorMessage, HTTPResponse res) {
        JSONMap json = new JSONMap();
        json.put("error", errorMessage);
        res.send(code, json.toString());
    }
}
