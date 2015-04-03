package org.general.http;

/**
 * An generic interface to handle HTTP requests. HTTPServer calls the handle
 * method when it receives a request. Application layer router should implement
 * this interface to perform actions on the incoming HTTPRequest and construct
 * and send HTTPResponse.
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
