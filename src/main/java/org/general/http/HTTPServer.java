package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.general.util.Logger;

/**
 * A generic HTTPServer that serves HTTPRequest and HTTPResponse.
 *
 * @author Guoxing Li
 *
 */
public abstract class HTTPServer {

    // server name
    public String name;

    private ServerSocket ss;
    private int port;

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
            } catch (IOException e) {
                e.printStackTrace();
                response.send(HTTPResponse.StatusCode.BAD_REQUEST,
                        "Request is malformatted");
                s.close();
                continue;
            }
            
            handle(request, response);
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
