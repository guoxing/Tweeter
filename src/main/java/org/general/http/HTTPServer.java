package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;

import org.general.http.HTTPRequest.InvalidHttpFormattingException;
import org.general.util.Logger;

/**
 * A generic HTTPServer that converts sockets into HTTPRequest and HTTResponse
 * objects, and calls a specified handler to take action given these objects.
 * 
 * In charge of handling errors when parsing HTTP requests.
 *
 * @author Guoxing Li
 *
 */
public class HTTPServer {

    private ServerSocket serverSocket;
    private String name;
    private int port;

    /**
     * Will begin listening on given port for HTTP requests, parsing them from a
     * socket and handing an HTTPReq and HTTPRes object to the given handler.
     * 
     * @throws HttpServerException
     *             if server can no longer accept any requests
     */
    public HTTPServer(String name, int port,
            BiConsumer<HTTPRequest, HTTPResponse> httpHandler)
            throws HttpServerException {
        this.name = name;
        this.port = port;
        Logger.log("Server " + name + " started on port " + port + ".");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new HttpServerException(e.getMessage());
        }

        while (true) {
            HTTPRequest request = null;
            HTTPResponse response = null;
            Socket s = null;
            try {
                s = serverSocket.accept();
                request = new HTTPRequest(s.getInputStream());
                response = new HTTPResponse(s.getOutputStream(), name);
                httpHandler.accept(request, response);
            } catch (InvalidHttpFormattingException e) {
                response.send(HTTPResponse.StatusCode.BAD_REQUEST,
                        "Request is malformatted");
            } catch (IOException e) {
                Logger.log("IOException when parsing HTTP req/res: "
                        + e.getStackTrace());
                // Cause of error is printed out to devs but not shown to users
                response.send(HTTPResponse.StatusCode.SERVER_ERROR,
                        "Internal server error. Unable to parse request.");
            } finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void shutdown() throws IOException {
        if (serverSocket != null) {
            Logger.log("Server " + name + " shut down on port " + port + ".");
            serverSocket.close();
            serverSocket = null;
        }
    }

    /**
     * An exception wherein an http server can no longer accept any requests.
     */
    public class HttpServerException extends Exception {
        private static final long serialVersionUID = 1L;

        public HttpServerException(String msg) {
            super(msg);
        }
    }
}
