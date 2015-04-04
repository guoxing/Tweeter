package org.general.http;

import org.tweeter.config.Router;

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
//        Router.Response routerResponse = Router.route(req);
//        
//        switch (routerResponse.getResult()) {
//            case SUCCESS:
//                res.setBody(routerResponse.getBody());
//                res.sendSuccess(HTTPResponse.StatusCode.OK);
//            case INVALID_PATH:
//                res.sendError(HTTPResponse.StatusCode.NOT_FOUND, "Invalid path");
//            case BAD_REQUEST:
//                res.sendError(HTTPResponse.StatusCode.BAD_REQUEST, routerResponse.getBody());
//        }
//    }

}
