package org.general.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;

import org.general.http.HTTPRequest.InvalidHttpFormattingException;
import org.general.util.Logger;

/**
 * A generic HTTPServer that serves HTTPRequest and HTTPResponse.
 * In charge of handling errors when parsing HTTP requests.
 *
 * @author Guoxing Li
 *
 */
public class HTTPServer {

    private ServerSocket ss;
    private String name;
    private int port;

    public HTTPServer(String name, int port, BiConsumer<HTTPRequest, HTTPResponse> httpHandler)
            throws HttpServerException {
        this.name = name;
        this.port = port;
        Logger.log("Server " + name + " started on port "+port+".");
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            throw new HttpServerException(e.getMessage());
        }
        
        while (true) {
            HTTPRequest request = null;
            HTTPResponse response = null;
            Socket s = null;
            try {
                s = ss.accept();
                request = new HTTPRequest(s.getInputStream());
                response = new HTTPResponse(s.getOutputStream(), name);
            } catch (IOException | InvalidHttpFormattingException e) {
                if (e instanceof InvalidHttpFormattingException) {
                    response.send(HTTPResponse.StatusCode.BAD_REQUEST,
                            "Request is malformatted");
                }
                else { // IOException
                    e.printStackTrace();
                    response.send(HTTPResponse.StatusCode.SERVER_ERROR,
                            "Internal server error. Unable to parse request.");
                }
                tryClose(s);
                continue;
            }
            
            httpHandler.accept(request, response);
            tryClose(s);
        }
    }
    
    private void tryClose(Socket s) {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException {
        if (ss != null) {
            Logger.log("Server " + name + " shut down on port "+port+".");
            ss.close();
            ss = null;
        }
    }
    public class HttpServerException extends Exception {
        private static final long serialVersionUID = 1L;
        public HttpServerException(String msg) {
            super(msg);
        }
    }
}
