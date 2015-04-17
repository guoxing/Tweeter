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
        OK(200),
        BAD_REQUEST(400),
        NOT_FOUND(404),
        SERVER_ERROR(500);
        
        private int num;
        private StatusCode(int num) {
            this.num = num;
        }
        public int getNum() {
            return this.num;
        }
    }

    public static class HeaderField {
        public static final String DATE = "Date";
        public static final String SERVER = "Server";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_LENGTH = "Content-Length";
    }

    public static final Map<StatusCode, String> StatusMessage;
    static {
        StatusMessage = new HashMap<StatusCode, String>();
        StatusMessage.put(StatusCode.OK, "OK");
        StatusMessage.put(StatusCode.BAD_REQUEST, "Bad Request");
        StatusMessage.put(StatusCode.NOT_FOUND, "Not Found");
        StatusMessage.put(StatusCode.SERVER_ERROR, "Interal Server Error");
    }

    private PrintWriter out;
    private StatusCode statusCode;
    private Map<String, String> headers;
    @SuppressWarnings("unused")
    private String body;
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
        this.statusCode = code;
        this.body = body;

        // set content-length
        headers.put(HeaderField.CONTENT_LENGTH, Integer.toString(body.length()));

        // write to stream
        out.println(version + " " + Integer.toString(statusCode.getNum()) + " "
                + StatusMessage.get(statusCode));
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
