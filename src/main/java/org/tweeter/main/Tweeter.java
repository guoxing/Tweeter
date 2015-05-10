package org.tweeter.main;

import java.io.IOError;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.general.data.DataStorage;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.StatusCode;
import org.general.http.HTTPServer;
import org.general.http.HTTPServer.HttpServerException;
import org.general.http.InvalidHttpParametersException;
import org.general.json.JSONObject;
import org.general.util.Logger;
import org.general.util.NumberParser;
import org.general.util.Pair;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * In charge of starting up an http server with passed in argument options, routing
 * requests to appropriate controllers and handling all server and application errors.
 * 
 * Contains main entry point for Tweeter program.
 */
public class Tweeter {    
    // Default fields for http specific to Tweeter
    private static final String DEFAULT_SERVER_NAME = "Tweeter/1.0";
    private static int DEFAULT_PORT = 8080;
    private static final String DEFAULT_RESPONSE_VERSION = "HTTP/1.1";
    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final DateFormat RESPONSE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    
    /**
     * Map from an API endpoint path to its respective HTTP method and the controller method that is called
     * to handle this endpoint.
     */
    private static Map<String, Pair<HTTPRequest.Method, ControllerMethod>> routerMap = new HashMap<>();
    /**
     * To add a new API endpoint:
     * 1. Define a method in a controller that takes in an HTTPRequest and returns a JSONObject
     * 2. Add a route using addRoute in the static initializer, specifying the http method, path
     *    and the controller method you created.
     */
    static {
        addRoute(HTTPRequest.Method.POST, "/statuses/update",            StatusesController::updateStatus);
        addRoute(HTTPRequest.Method.GET,  "/statuses/home_timeline.json",StatusesController::getHomeTimeline);
        addRoute(HTTPRequest.Method.GET,  "/statuses/user_timeline.json",StatusesController::getUserTimeline);
        addRoute(HTTPRequest.Method.GET,  "/friends/ids.json",           FriendshipsController::getFriends);
        addRoute(HTTPRequest.Method.GET,  "/followers/ids.json",         FriendshipsController::getFollowers);
        addRoute(HTTPRequest.Method.POST, "/friendships/destroy",        FriendshipsController::deleteFriendship);
        addRoute(HTTPRequest.Method.POST, "/friendships/create",         FriendshipsController::createFriendship);
    }

    /**
     * Parses argument options and then starts up server.
     * 
     * Main entry point for Tweeter program.
     */
    public static void main(String[] args) {
        // Parse arg options
        int port = DEFAULT_PORT;
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-port") && i + 1 < args.length) {
                String portAsStr = args[i + 1];
                if (!NumberParser.isNumber(portAsStr)) {
                    Logger.log("Port must be a number. Invalid value given: " + portAsStr);
                    return;
                }
                port = Integer.parseInt(portAsStr); // Will not throw exception bec we checked if is number
            }
            if (args[i].equals("-workspace") && i + 1 < args.length) {
                DataStorage.setPathToWorkspace(args[i + 1] + "/");
            }
            if (args[i].equals("-help")) {
                System.out.println("-port\n\tport that will listen for requests to Tweeter. Default: 8080\n"
                        + "-workspace\n\tpath to files used for data storage. Default: .\n");
                return;
            }
        }
        
        HTTPServer server = null;
        try {
            server = new HTTPServer(DEFAULT_SERVER_NAME, port, Tweeter::handle);
        } catch (HttpServerException | IOError e) {
            e.printStackTrace();
            try {
                server.shutdown();
            } catch (IOException unableToShutdown) {
                unableToShutdown.printStackTrace();
            }
        }
    }

    /**
     * Routes the given request to a controller's method, receives the JSON response from
     * that method and responds with it.
     * 
     * @param httpReq
     *            HTTP Request to route
     * @param httpRes
     *            HTTP Response that the JSON response is sent over
     */
    private static void handle(HTTPRequest httpReq, HTTPResponse httpRes) {
        httpRes.setDefaults(DEFAULT_RESPONSE_VERSION, DEFAULT_RESPONSE_CONTENT_TYPE, RESPONSE_DATE_FORMAT,
                TimeZone.getTimeZone("GMT"), new Date());
        String reqURI = httpReq.getURI();
        HTTPRequest.Method httpMethod = httpReq.getMethod();
        Logger.log(httpMethod + " " + reqURI);
        
        if (!routerMap.containsKey(reqURI)) {
            respondWithJSONError(HTTPResponse.StatusCode.NOT_FOUND , reqURI + " is not a valid API endpoint", httpRes);
            return;
        }
        if (routerMap.get(reqURI).getFirst() != httpMethod) {
            // If we reach here, we know path is valid, so this must mean only the HTTP method is invalid.
            respondWithJSONError(HTTPResponse.StatusCode.BAD_REQUEST, "Invalid HTTP Method for path: "
                    + reqURI + ". Should be "+routerMap.get(reqURI).getFirst()+" instead of "+httpMethod, httpRes);
            return;
        }
        
        try {
            JSONObject response = routerMap.get(reqURI).getSecond().respond(httpReq);
            httpRes.send(HTTPResponse.StatusCode.OK, response.toJson());
        } catch (InvalidHttpParametersException e) {
            respondWithJSONError(StatusCode.BAD_REQUEST, e.getMessage(), httpRes);
        } catch (IOException e) {
            e.printStackTrace(); // Print error message so we only reveal cause to devs and not users
            respondWithJSONError(StatusCode.SERVER_ERROR, "Internal Server Error", httpRes);
            throw new IOError(e);
        }
    }
    
    /**
     * Assigns a particular HTTP method and controller method to an API endpoint path in our routerMap.
     */
    private static void addRoute(HTTPRequest.Method method, String path, ControllerMethod reqHandler) {
        /* Thin method that hides the ugly syntax for creating a pair of this type over and over
           when creating routes */
        routerMap.put(path, new Pair<HTTPRequest.Method, ControllerMethod>(method, reqHandler));
    }
    
    private static void respondWithJSONError(StatusCode code, String errorMessage, HTTPResponse res) {
        Map<String, JSONObject> map = new HashMap<>();
        map.put("error", new JSONObject(errorMessage));
        res.send(code, new JSONObject(map).toJson());
    }
    
    /**
     * This is required in order to make a functional reference to a method that throws an exception.
     * Here is a particularly enlightening StackOverflow post on this:
     * https://stackoverflow.com/questions/18198176/java-8-lambda-function-that-throws-exception
     * Thanks, Java.
     */
    private interface ControllerMethod {
        JSONObject respond(HTTPRequest req) throws InvalidHttpParametersException, IOException;
    }
    
}
