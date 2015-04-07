package org.general.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that represents an HTTP request.
 *
 * @author Guoxing Li
 *
 */
public class HTTPRequest {

    // HTTP methods
    public static class Method {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }

    private static final String ENCODING = "UTF-8";
    // URIREGEX is used to extract the path from an absolute URI
    private static final String URIREGEX = "^https?://[^/]+(.+)";
    // QUERYREGEX is used to separate path and query string
    private static final String QUERYREGEX = "([^?]+)(?:\\?(.+))?";

    private BufferedReader in;

    private String method;
    private String absoluteURI;
    private String URI;
    @SuppressWarnings("unused")
    private String version;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    /**
     * Construct a new HTTPRequest associated with an InputStream from a Socket.
     * 
     * @param in
     * @throws Exception
     */
    public HTTPRequest(InputStream in) throws Exception {
        this.in = new BufferedReader(new InputStreamReader(in));
        process();
    }

    /**
     * Process the request, set the corresponding properties.
     * 
     * @throws Exception
     */
    private void process() throws Exception {
        String requestLine = in.readLine();
        String[] splited = requestLine.split("\\s+");
        method = splited[0];
        absoluteURI = splited[1];
        version = splited[2];

        // process URI/queryString
        Pattern uriP = Pattern.compile(URIREGEX);
        Matcher uriM = uriP.matcher(splited[1]);
        String fullURI;
        if (uriM.find()) {
            fullURI = uriM.group(1);
        } else {
            fullURI = new String(absoluteURI);
        }
        Pattern queryP = Pattern.compile(QUERYREGEX);
        Matcher queryM = queryP.matcher(fullURI);
        queryM.find();
        URI = URLDecoder.decode(queryM.group(1), ENCODING);
        String queryString = queryM.group(2);
        queryParams = new HashMap<String, String>();
        if (queryString != null) {
            addQueryParams(queryString);
        }

        // populate headers
        headers = new HashMap<String, String>();
        String line = in.readLine();
        while (line != null && !line.isEmpty()) {
            String key = line.split(":")[0].trim();
            String value = line.split(":")[1].trim();
            headers.put(key, value);
            line = in.readLine();
        }
        
        // process body
        if (line.isEmpty() && headers.get("Content-Length") != null) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            if (contentLength <= 0)
                return;
            char[] bodyBuffer = new char[contentLength];
            in.read(bodyBuffer, 0, contentLength);
            String bodyAsString = URLDecoder.decode(
                    String.copyValueOf(bodyBuffer), ENCODING);
            if (bodyAsString != null && method.equals(Method.POST)) {
                addQueryParams(bodyAsString);
            }
        }
    }

    private void addQueryParams(String queryString) throws Exception {
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            queryParams.put(URLDecoder.decode(key, ENCODING),
                    URLDecoder.decode(value, ENCODING));
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
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
     * Get a map of the query parameters. The parameters could be in the URI or
     * in the body if it's a POST request. Keys and values are decoded.
     * 
     * @return a Map of query parameters
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getAbsoluteURI() {
        return absoluteURI;
    }

}
