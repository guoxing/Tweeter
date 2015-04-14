package org.tweeter.config;

import java.io.IOException;

import org.general.application.ApplicationInterface;
import org.general.application.ApplicationInterface.AppRequest;
import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.ApplicationInterface.AppResponse.AppResponseStatus;
import org.general.data.InvalidDataFormattingException;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPServer;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.StatusData;

public class TweeterServer extends HTTPServer {
    
	private TweeterServer(String name) {
		super(name);
	}
    private TweeterServer(int port, String name) {
		super(port, name);
	}

	private static ApplicationInterface appInterface;
    
    private static HTTPServer server;

    public static void main(String[] args) throws IOException,
            InvalidDataFormattingException {
        server = new TweeterServer("Tweeter/1.0");
        appInterface = new Router();
        
        // spin up data modules
        FriendshipData.getInstance();
        StatusData.getInstance();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            server.shutdown();
        }
    }
    
    protected void handle(HTTPRequest httpReq, HTTPResponse httpRes) {
        AppRequest appReq = new AppRequest(httpReq.getMethod() + 
        		" " + httpReq.getURI(), httpReq.getQueryParams());
        AppResponse appRes = appInterface.respondToAppReq(appReq);
        String body = appRes.getBody();
        AppResponseStatus result = appRes.getResponseStatus();
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
            case INTERNAL_ERROR:
                httpRes.sendError(HTTPResponse.StatusCode.SERVER_ERROR, "Server error.");
                return;
        }
    }
    
}
