package org.tweeter.config;

import org.general.application.ApplicationInterface.AppRequest;
import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.ApplicationInterface.AppResponseStatus;
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
        AppRequest appReq = new AppRequest(httpReq.getMethod() + " " + httpReq.getURI(), httpReq.getQueryParams());
        AppResponse appRes = router.respondToAction(appReq);
        String body = appRes.getBody();
        AppResponseStatus result = appRes.getResult();
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
                        "File not found: "+appReq.getAddress());
                return;
        }
    }

}
