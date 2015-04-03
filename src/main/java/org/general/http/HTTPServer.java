package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A generic HTTPServer that serves HTTPRequest and HTTPResponse.
 *
 * @author Guoxing Li
 *
 */
public class HTTPServer {

    // server name
    public String name;

    private static int DEFAULT_PORT = 8080;

    private ServerSocket ss;
    private HTTPHandler handler;
    private int port;

    public HTTPServer(String name, HTTPHandler handler) {
        this(DEFAULT_PORT, name, handler);
    }

    public HTTPServer(int port, String name, HTTPHandler handler) {
        this.port = port;
        this.name = name;
        this.handler = handler;
    }

    public void start() throws IOException {
        ss = new ServerSocket(port);
        while (true) {
            Socket s = ss.accept();
            HTTPRequest request;
            HTTPResponse response = new HTTPResponse(s.getOutputStream(), name);
            try {
                request = new HTTPRequest(s.getInputStream());
                handler.handle(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HTTPResponse.StatusCode.BAD_REQUEST,
                        "Request is malformatted");
            }
            s.close();
        }
    }

    public void shutdown() throws IOException {
        ss.close();
    }
}
