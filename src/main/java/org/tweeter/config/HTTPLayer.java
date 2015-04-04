package org.tweeter.config;

import org.general.application.ApplicationInterface.ApplicationAction;
import org.general.application.ApplicationInterface.ApplicationDatagram;
import org.general.application.ApplicationInterface.ApplicationResult;
import org.general.http.HTTPHandler;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;

public class HTTPLayer implements HTTPHandler {
    
    private Router router;
    
    public HTTPLayer() {
        this.router = new Router();
    }
    
    @Override
    public void handle(HTTPRequest req, HTTPResponse res) {
        ApplicationAction action = new ApplicationAction(req.getMethod(), req.getURI(), req.getQueryParams());
        ApplicationDatagram applicationResponse = router.respondToAction(action);
        String body = applicationResponse.getBody();
        ApplicationResult result = applicationResponse.getResult();
        res.setBody(body);
        
        switch (result) {
            case SUCCESS:
                res.sendSuccess(HTTPResponse.StatusCode.OK);
                return;
            case INVALID_PARAMETERS:
                res.sendError(HTTPResponse.StatusCode.BAD_REQUEST, body);
                return;
            case INVALID_PATH:
                res.sendError(HTTPResponse.StatusCode.NOT_FOUND, body);
                return;
        }
    }

}
