package org.general.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.general.json.JSONMap;

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
    private static final String DEFAULT_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    private PrintWriter out;
    private StatusCode statusCode;
    private Map<String, String> headers;
    private String body;
    private String version;
    private boolean sent; // whether this response has been sent

    public HTTPResponse(OutputStream out, String serverName) {
        this.out = new PrintWriter(out);
        headers = new HashMap<String, String>();
        headers.put(HeaderField.SERVER, serverName);
        sent = false;
        setDefaultFields();
    }

    private void setDefaultFields() {
        version = DEFAULT_VERSION;
        headers.put(HeaderField.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        headers.put(HeaderField.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Send this response with a success status code.
     * 
     * @param code
     * 
     * @return A boolean indicating whether the send is success
     */
    public boolean sendSuccess(StatusCode code) {
        if (code != StatusCode.OK) {
            return false;
        }
        statusCode = code;
        return doSend();
    }

    /**
     * Send this response with an error status code together with an error
     * message. This method resets body to the error message.
     * 
     * @param code
     * @param message
     * 
     * @return A boolean indicating whether the send is success
     */
    public boolean sendError(StatusCode code, String message) {
        if (code == StatusCode.OK) {
            return false;
        }
        statusCode = code;
        JSONMap newBody = new JSONMap();
        newBody.put("ErrorMsg", message);
        body = newBody.toString();
        return doSend();
    }

    private boolean doSend() {
        if (sent) {
            return false;
        }
        // set date header
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        headers.put(HeaderField.DATE, dateFormatGmt.format(new Date()));

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
