package org.tweeter.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.general.application.ApplicationInterface;
import org.general.application.ApplicationInterface.AppRequest;
import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.ApplicationInterface.AppResponse.AppResponseStatus;
import org.general.data.AppData;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPServer;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.StatusData;

public class TweeterServer extends HTTPServer {

    private static final String HELP_OPTION = "-help";
    private static final String PORT_OPTION = "-port";
    private static final String WORKSPACE_OPTION = "-workspace";
    private static final String DEFAULT_SERVER_NAME = "Tweeter/1.0";

    private TweeterServer(String name) {
        super(name);
    }

    private TweeterServer(int port, String name) {
        super(port, name);
    }

    private static ApplicationInterface appInterface;

    private static HTTPServer server;

    /**
     * Parses argument options and then starts up server.
     * 
     * @param args
     *            can include a port option, help option, and workspace option
     * @throws IOException
     *             Thrown if there is a problem
     */
    public static void main(String[] args) throws IOException {

        Map<String, String> argOptions = parseArgumentOptions(args);
        if (argOptions.containsKey(HELP_OPTION)) {
            System.out.println("-port port_number\n-workspace path");
            return;
        }
        if (argOptions.containsKey(WORKSPACE_OPTION)) {
            AppData.setPathToWorkspace(argOptions.get(WORKSPACE_OPTION));
        }
        if (argOptions.containsKey(PORT_OPTION)) {
            server = new TweeterServer(Integer.parseInt(argOptions
                    .get(PORT_OPTION)), DEFAULT_SERVER_NAME);
        } else {
            server = new TweeterServer("Tweeter/1.0");
        }

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

    private static Map<String, String> parseArgumentOptions(String[] args) {
        Map<String, String> argOptions = new HashMap<String, String>();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals(PORT_OPTION) && i + 1 < args.length) {
                argOptions.put(PORT_OPTION, args[i + 1]);
            }
            if (args[i].equals(WORKSPACE_OPTION) && i + 1 < args.length) {
                // End workspace path with a slash
                argOptions.put(WORKSPACE_OPTION, args[i + 1] + "/");
            }
            if (args[i].equals(HELP_OPTION)) {
                argOptions.put(HELP_OPTION, "");
            }
        }
        return argOptions;
    }

    @Override
    protected void handle(HTTPRequest httpReq, HTTPResponse httpRes) {
        AppRequest appReq = new AppRequest(httpReq.getMethod() + " "
                + httpReq.getURI(), httpReq.getQueryParams());
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
                    "File not found: " + appReq.getAddress());
            return;
        case INTERNAL_ERROR:
            httpRes.sendError(HTTPResponse.StatusCode.SERVER_ERROR,
                    "Server error.");
            return;
        }
    }

}
