package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;

import org.general.util.Logger;

/**
 * A generic HTTPServer that serves HTTPRequest and HTTPResponse.
 * In charge of handling invalid HTTP formatting errors.
 *
 * @author Guoxing Li
 *
 */
public class HTTPServer {

    // server name
    public String name;

    private ServerSocket ss;
    private int port;
    private BiConsumer<HTTPRequest, HTTPResponse> httpHandler;

    public HTTPServer(int port, String name, BiConsumer<HTTPRequest, HTTPResponse> httpHandler) {
        this.port = port;
        this.name = name;
        this.httpHandler = httpHandler;
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
            
            httpHandler.accept(request, response);
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
