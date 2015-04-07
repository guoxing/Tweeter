package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.general.logger.Logger;
import org.tweeter.config.HTTPLayer;

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
    
    public static void main(String[] args) throws IOException {
        new HTTPServer("Tweeter", new HTTPLayer()).start();
    }

    public HTTPServer(String name, HTTPHandler handler) {
        this(DEFAULT_PORT, name, handler);
    }

    public HTTPServer(int port, String name, HTTPHandler handler) {
        this.port = port;
        this.name = name;
        this.handler = handler;
    }

    public void start() throws IOException {
        if (ss != null) {
            Logger.log("Server " + name + " already started.");
            return;
        }
        ss = new ServerSocket(port);
        while (true) {
            Socket s = ss.accept();
            HTTPRequest request = null;
            HTTPResponse response = new HTTPResponse(s.getOutputStream(), name);

            try {
                request = new HTTPRequest(s.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HTTPResponse.StatusCode.BAD_REQUEST,
                        "Request is malformatted");
                s.close();
                continue;
            }
            try {
                handler.handle(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HTTPResponse.StatusCode.SERVER_ERROR,
                        "Internal server error");
            }
            s.close();
        }
    }

    public void shutdown() throws IOException {
        if (ss != null) {
            Logger.log("Server " + name + " shut down.");
            ss.close();
            ss = null;
        }
    }
}
