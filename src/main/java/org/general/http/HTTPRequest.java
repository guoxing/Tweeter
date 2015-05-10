package org.general.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.general.util.NumberParser;

/**
 * A class that represents an HTTP request.
 * 
 * Provides utilities for retrieving required/optional parameters,
 * throwing exceptions when parameters do not exist or are of
 * invalid types.
 *
 * @author Guoxing Li
 *
 */
public class HTTPRequest {
    public static enum Method {
        GET("GET"), POST("POST");
        private String str;
        private Method(String str) {
            this.str = str;
        }
        public String toString() {
            return str;
        }
    }

    // URIREGEX is used to extract the path from an absolute URI
    private static final String URIREGEX = "^https?://[^/]+(.+)";
    // QUERYREGEX is used to separate path and query string
    private static final String QUERYREGEX = "([^?]+)(?:\\?(.+))?";

    private Method method;
    private String absoluteURI;
    private String URI;
    @SuppressWarnings("unused")
    private String version;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    public HTTPRequest(InputStream in) throws IOException, InvalidHttpFormattingException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
        
        // Read in first line of request
        String requestLine = inReader.readLine();
        String[] splitted = requestLine.split("\\s+");
        
        if (splitted.length != 3) 
            throw new InvalidHttpFormattingException(InvalidHttpFormattingException.INVALID_FIRST_LINE + 
                    " was given: " + requestLine);
        method = Method.valueOf(splitted[0]);
        absoluteURI = splitted[1];
        version = splitted[2];

        // process URI/queryString
        Pattern uriP = Pattern.compile(URIREGEX);
        Matcher uriM = uriP.matcher(splitted[1]);
        String fullURI;
        if (uriM.find()) {
            fullURI = uriM.group(1);
        } else {
            fullURI = new String(absoluteURI);
        }
        Pattern queryP = Pattern.compile(QUERYREGEX);
        Matcher queryM = queryP.matcher(fullURI);
        queryM.find();
        URI = URLDecoder.decode(queryM.group(1));
        String queryString = queryM.group(2);
        queryParams = new HashMap<String, String>();
        if (queryString != null) {
            addQueryParams(queryString);
        }

        // populate headers
        headers = new HashMap<String, String>();
        String line = inReader.readLine();
        while (line != null && !line.isEmpty()) {
            String key = line.split(":")[0].trim();
            String value = line.split(":")[1].trim();
            headers.put(key, value);
            line = inReader.readLine();
        }

        // process body
        if (line.isEmpty() && headers.get("Content-Length") != null) {
            if (!NumberParser.isNumber(headers.get("Content-Length"))) {
                throw new InvalidHttpFormattingException("Content length must be a number. Was: "
                        +headers.get("Content-Length"));
            }
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            if (contentLength <= 0)
                return;
            char[] bodyBuffer = new char[contentLength];
            inReader.read(bodyBuffer, 0, contentLength);
            String bodyAsString = URLDecoder.decode(String
                    .copyValueOf(bodyBuffer));
            if (bodyAsString != null && method.equals(Method.POST)) {
                addQueryParams(bodyAsString);
            }
        }
    }
    
    private void addQueryParams(String queryString) throws IOException {
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            queryParams.put(URLDecoder.decode(key), URLDecoder.decode(value));
        }
    }
    
    /**
     * @throws InvalidKeyException if param with key given does not exist
     */
    public String getHeaderValue(String key) throws InvalidKeyException {
        if (!headers.containsKey(key)) {
            throw new InvalidKeyException("Header with key: " +key+ " does not exist.");
        }
        return headers.get(key);
    }

    public Method getMethod() {
        return method;
    }

    /**
     * Get the URI that identifies a resource on a server. E.g.
     * "/path/to/resource". Decoded
     * 
     * @return a String represents the URI path
     */
    public String getURI() {
        return URI;
    }
    
    /**
     * @throws InvalidKeyException if param with key given does not exist
     */
    public String getParamValue(String key) throws InvalidKeyException {
        if (!queryParams.containsKey(key)) {
            throw new InvalidKeyException("Header with key: " +key+ " does not exist.");
        }
        return queryParams.get(key);
    }
    
    /**
     * Returns value for long param associated with given key.
     * @throws InvalidHttpParametersException if param does not exist or is not a number
     */
    public Long getRequiredLongParam(String key) throws InvalidHttpParametersException {
        return getLongParam(key, null, true);
    }
    
    /**
     * Returns value for long param associated with given key if it exists, or default value if not.
     * @throws InvalidHttpParametersException if param exists but is not a number
     */
    public Long getOptionalLongParam(String key, Long defaultValue) throws InvalidHttpParametersException {
        return getLongParam(key, defaultValue, false);
    }
    
    /**
     * If isRequired is true, defaultValue is not used.
     * If isRequired is false, defaultValue is returned if param does not exist.
     * 
     * @param key key for the param whose value is to be retrieved
     * @param defaultValue value to return if isRequired is false
     * @param isRequired if this is true, will throw exception if param does not exist
     * @return the long value associated with the given key
     * @throws InvalidHttpParametersException if the param is required and does not exist, or if the param
     * exists but is not a number
     */
    private Long getLongParam(String key, Long defaultValue, boolean isRequired) throws InvalidHttpParametersException {
        if (queryParams.get(key) == null) {
            if (!isRequired) return defaultValue;
            throw new InvalidHttpParametersException(key + " is a required parameter");
        }
        String strForm = queryParams.get(key);
        if (!NumberParser.isNumber(strForm)) {
            throw new InvalidHttpParametersException(key
                    + " must be a 64-bit integer. Invalid value given: " + strForm);
        }
        return Long.parseLong(strForm);
    }
    
    /**
     * Returns value for string param associated with given key
     * @throws InvalidHttpParametersException if param does not exist
     */
    public String getStringRequiredParam(String key) throws InvalidHttpParametersException {
        if (queryParams.get(key) == null) throw new InvalidHttpParametersException(key + " is a required parameter");
        return queryParams.get(key);
    }

    public String getAbsoluteURI() {
        return absoluteURI;
    }
    
    public class InvalidHttpFormattingException extends Exception {
        private static final long serialVersionUID = 1L;
        public static final String INVALID_FIRST_LINE = "First line must have HTTP Method, URI, and Version.";
        public InvalidHttpFormattingException(String msg) { super("Invalid HTTP Formatting: " + msg); }
    }

}
