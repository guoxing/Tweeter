package org.general.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents an HTTP response.
 *
 * @author Guoxing Li
 *
 */
public class HTTPResponse {

    // HTTP status code
    public enum StatusCode {
        OK(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        SERVER_ERROR(500, "Internal Server Error");
        
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

    public static class HeaderField {
        public static final String DATE = "Date";
        public static final String SERVER = "Server";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_LENGTH = "Content-Length";
    }

    private PrintWriter out;
    private Map<String, String> headers;
    private String version;
    private boolean sent; // whether this response has been sent

    public HTTPResponse(OutputStream out, String serverName) {
        this.out = new PrintWriter(out);
        headers = new HashMap<String, String>();
        headers.put(HeaderField.SERVER, serverName);
        sent = false;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

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
        headers.put(HeaderField.CONTENT_LENGTH, Integer.toString(body.length()));

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
