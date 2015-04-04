package org.general.http;


/**
 * An interface to handle HTTP requests and generate HTTP responses. 
 * HTTPServer calls the handle method when it receives a request.
 *
 * @author Guoxing Li
 *
 */
public interface HTTPHandler {

    /**
     * Handle the incoming HTTPRequest, generate and send the response.
     * 
     * @param req
     * @param res
     */
    public void handle(HTTPRequest req, HTTPResponse res);

}
