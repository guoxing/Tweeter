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
 * @author marcelpuyat
 *
 */
public class Tweeter {

    // Command line options
    private static final String HELP_OPTION = "-help";
    private static final String PORT_OPTION = "-port";
    private static final String WORKSPACE_OPTION = "-workspace";
    
    // Default fields for http specific to Tweeter
    private static final String DEFAULT_SERVER_NAME = "Tweeter/1.0";
    private static int DEFAULT_PORT = 8080;
    private static final String DEFAULT_RESPONSE_VERSION = "HTTP/1.1";
    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final DateFormat RESPONSE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    
    /**
     * To add a new API endpoint:
     * 1. Define a method in a controller that takes in an HTTPRequest and returns a JSONObject
     * 2. Add a route using addRoute in the static initializer, specifying the path, http method
     *    and the controller method you created.
     */
    
    /**
     * Map from an API endpoint path to its respective HTTP method and the controller method that is called
     * to handle this endpoint.
     */
    private static Map<String, Pair<HTTPRequest.Method, ControllerMethod<HTTPRequest, JSONObject>>> routerMap = new HashMap<>();
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
        String portAsStr = null;
        
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals(PORT_OPTION) && i + 1 < args.length) {
                portAsStr = args[i + 1];
                if (portAsStr != null && !NumberParser.isNumber(portAsStr)) {
                    Logger.log("Port must be a number. Invalid port: " + portAsStr);
                    return;
                }
            }
            if (args[i].equals(WORKSPACE_OPTION) && i + 1 < args.length) {
                DataStorage.setPathToWorkspace(args[i + 1] + "/");
            }
            if (args[i].equals(HELP_OPTION)) {
                System.out.println("-port port that will listen for requests to Tweeter. Default: 8080"
                        + "\n-workspace path to files used for data storage. Default: .");
                return;
            }
        }

        // Will never throw exception because we made sure portAsStr is a number.
        int port = portAsStr == null ? DEFAULT_PORT : Integer.parseInt(portAsStr); 

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
     * @param req
     *            HTTP Request to route
     * @param res
     *            HTTP Response that the JSON response is sent over
     */
    private static void handle(HTTPRequest req, HTTPResponse res) {
        res.setDefaults(DEFAULT_RESPONSE_VERSION, DEFAULT_RESPONSE_CONTENT_TYPE, RESPONSE_DATE_FORMAT,
                TimeZone.getTimeZone("GMT"), new Date());
        String reqURI = req.getURI();
        HTTPRequest.Method httpMethod = req.getMethod();
        Logger.log(httpMethod + " " + reqURI);
        
        if (!routerMap.containsKey(reqURI)) {
            respondWithJSONError(HTTPResponse.StatusCode.NOT_FOUND , reqURI + " is not a valid API endpoint", res);
            return;
        }
        
        if (routerMap.get(reqURI).getFirst() != httpMethod) {
            // If we reach here, we know path is valid, so this must mean only the HTTP method is invalid.
            respondWithJSONError(HTTPResponse.StatusCode.BAD_REQUEST, "Invalid HTTP Method for path: "
                    + reqURI + ". Should be "+routerMap.get(reqURI).getFirst()+" instead of "+httpMethod, res);
            return;
        }
        
        try {
            JSONObject response = routerMap.get(reqURI).getSecond().apply(req);
            res.send(HTTPResponse.StatusCode.OK, response.toJson());
        } catch (InvalidHttpParametersException e) {
            respondWithJSONError(StatusCode.BAD_REQUEST, e.getMessage(), res);
        } catch (IOException e) {
            e.printStackTrace(); // Print error message so we only reveal cause to devs and not users
            respondWithJSONError(StatusCode.SERVER_ERROR, "Internal Server Error", res);
            throw new IOError(e);
        }
    }
    
    /**
     * Assigns a particular HTTP method and controller method to an API endpoint path in our routerMap.
     */
    private static void addRoute(HTTPRequest.Method method, String path, 
            ControllerMethod<HTTPRequest, JSONObject> reqHandler) {
        /* This is a thin method that hides the ugly syntax for creating a pair of this type over and over
           when creating routes */
        routerMap.put(path, new Pair<HTTPRequest.Method, ControllerMethod<HTTPRequest, JSONObject>>(method, reqHandler));
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
     * 
     * Thanks, Java.
     */
    private interface ControllerMethod<ReqType, ResType> {
        ResType apply(ReqType req) throws InvalidHttpParametersException, IOException;
    }
    
}
