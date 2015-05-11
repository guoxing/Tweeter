package org.general.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * A class that represents an HTTP response.
 * 
 * User can use this class to instantiate a HTTPResponse, send
 *
 * @author Guoxing Li
 *
 */
public class HTTPResponse {

    public enum StatusCode {
        OK(200, "OK"), BAD_REQUEST(400, "Bad Request"), NOT_FOUND(404,
                "Not Found"), SERVER_ERROR(500, "Internal Server Error");

        private int num;
        private String message;

        private StatusCode(int num, String message) {
            this.num = num;
            this.message = message;
        }

        public int getNum() {
            return this.num;
        }

        public String getMessage() {
            return this.message;
        }
    }

    private static final String HEADER_DATE_KEY = "Date";
    private static final String HEADER_SERVER_KEY = "Server";
    private static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH_KEY = "Content-Length";

    private PrintWriter out;
    private Map<String, String> headers;
    private String version;
    private boolean sent; // whether this response has been sent

    /**
     * Constructor for HTTPResponse.
     * 
     * @param out
     *            The OutputStream where the respond goes to.
     * @param serverName
     *            The server name that will appear in header
     */
    public HTTPResponse(OutputStream out, String serverName) {
        this.out = new PrintWriter(out);
        headers = new HashMap<String, String>();
        headers.put(HEADER_SERVER_KEY, serverName);
        sent = false;
    }
    
    public void setDefaults(String version, String contentType, DateFormat dateFormat,
            TimeZone timeZone, Date date) {
        setVersion(version);
        setHeader(HEADER_CONTENT_TYPE_KEY, contentType);
        dateFormat.setTimeZone(timeZone);
        setHeader(HEADER_DATE_KEY, dateFormat.format(date));
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Send this HTTPResponse with the specified StatusCode and body.
     * 
     * @return Whether the send is success
     */
    public boolean send(StatusCode code, String body) {
        if (sent) {
            // Prevents re-sending of the same response
            return false;
        }

        if (this.version == null) {
            throw new NullPointerException("HTTP version must be set "
                    + "before sending");
        }
        if (body == null) {
            body = "";
        }
        if (code == null) {
            throw new NullPointerException("HTTP Status Code cannot be null");
        }

        // set content-length
        headers.put(HEADER_CONTENT_LENGTH_KEY, Integer.toString(body.length()));

        // write to stream
        out.println(version + " " + Integer.toString(code.getNum()) + " "
                + code.getMessage());
        for (String key : headers.keySet()) {
            out.println(key + ": " + headers.get(key));
        }
        out.println();
        out.println(body);
        out.close();
        sent = true;
        return true;
    }
}
