package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.general.logger.Logger;

/**
 * A generic HTTPServer that serves HTTPRequest and HTTPResponse.
 *
 * @author Guoxing Li
 *
 */
public abstract class HTTPServer {

    // server name
    public String name;

    private static int DEFAULT_PORT = 8080;

    private ServerSocket ss;
    private int port;
    

    protected HTTPServer(String name) {
        this(DEFAULT_PORT, name);
    }

    protected HTTPServer(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public void start() throws IOException {
        if (ss != null) {
            Logger.log("Server " + name + " already started.");
            return;
        }
        Logger.log("Server " + name + " started.");
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
                handle(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HTTPResponse.StatusCode.SERVER_ERROR,
                        "Internal server error");
            }
            s.close();
        }
    }
    
    protected abstract void handle(HTTPRequest req, HTTPResponse res);

    public void shutdown() throws IOException {
        if (ss != null) {
            Logger.log("Server " + name + " shut down.");
            ss.close();
            ss = null;
        }
    }
}
