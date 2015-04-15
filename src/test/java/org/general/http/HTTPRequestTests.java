package org.general.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test cases for HTTPRequest.
 *
 * @author Guoxing Li
 *
 */
public class HTTPRequestTests {

    private static final String DEFAULT_HOST = "www.google.com";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private HTTPRequest generateRequest(String requestLine, String body)
            throws Exception {
        InputStream stream = new ByteArrayInputStream(
                (requestLine + "Host: " + DEFAULT_HOST + " \n"
                        + "Content-Type: " + DEFAULT_CONTENT_TYPE + "\n"
                        + "Content-Length: " + body.length() + "\n\n" + body)
                        .getBytes(StandardCharsets.UTF_8));
        return new HTTPRequest(stream);
    }

    @Test
    public void testGeneralParse() {
        HTTPRequest req = null;
        try {
            req = generateRequest(HTTPRequest.Method.POST
                    + " /path/to/resource?id=122 " + DEFAULT_VERSION + "\n",
                    "id=123&name=Guoxing%20Li");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Initialization fails.");
        }
        assertEquals(req.getMethod(), HTTPRequest.Method.POST);
        assertEquals(req.getURI(), "/path/to/resource");
        assertEquals(req.getHeaders().get("Host"), "www.google.com");
        assertEquals(req.getQueryParams().get("id"), "123");
        assertEquals(req.getQueryParams().get("name"), "Guoxing Li");
    }

    @Test
    public void testMalformattedReq() {
        try {
            @SuppressWarnings("unused")
			HTTPRequest req = generateRequest(HTTPRequest.Method.POST
                    + "/path  " + DEFAULT_VERSION + "\n", "name=Guoxing Li");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        fail("Expected error not found.");
    }

}
