package org.tweeter.config;

import org.general.application.ApplicationInterface.ApplicationRequest;
import org.general.application.ApplicationInterface.ApplicationResponse;
import org.general.application.ApplicationInterface.ApplicationResponseStatus;
import org.general.http.HTTPHandler;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;

public class HTTPLayer implements HTTPHandler {
    
    private Router router;
    
    public HTTPLayer() {
        this.router = new Router();
    }
    
    @Override
    public void handle(HTTPRequest httpReq, HTTPResponse httpRes) {
        ApplicationRequest applicationReq = new ApplicationRequest(httpReq.getMethod() + " " + httpReq.getURI(), httpReq.getQueryParams());
        ApplicationResponse applicationRes = router.respondToAction(applicationReq);
        String body = applicationRes.getBody();
        ApplicationResponseStatus result = applicationRes.getResult();
        httpRes.setBody(body);
        
        switch (result) {
            case SUCCESS:
                httpRes.sendSuccess(HTTPResponse.StatusCode.OK);
                return;
            case INVALID_PARAMETERS:
                httpRes.sendError(HTTPResponse.StatusCode.BAD_REQUEST, body);
                return;
            case INVALID_DESTINATION:
                httpRes.sendError(HTTPResponse.StatusCode.NOT_FOUND,
                        "File not found: "+applicationReq.getAddress());
                return;
        }
    }

}
