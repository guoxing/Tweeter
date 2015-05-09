package org.tweeter.main;

import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.general.data.DataStorage;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.HeaderField;
import org.general.http.HTTPResponse.StatusCode;
import org.general.http.HTTPServer;
import org.general.http.InvalidHttpParametersException;
import org.general.json.JSONObject;
import org.general.util.Logger;
import org.general.util.Pair;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * In charge of starting up an http server with passed in argument options, routing
 * requests to appropriate controllers and handling all application errors.
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
    private static final String RESPONSE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    
    private static HTTPServer server;
    
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
     * @param args
     *            can include a port option, help option, and workspace option
     * @throws IOException
     *             Thrown if there is a problem shutting down server. Stack trace
     *             is printed.
     */
    public static void main(String[] args) throws IOException {

        Map<String, String> argOptions = parseArgumentOptions(args);
        if (argOptions.containsKey(HELP_OPTION)) {
            System.out.println("-port port_number\n-workspace path");
            return;
        }
        if (argOptions.containsKey(WORKSPACE_OPTION)) {
            DataStorage.setPathToWorkspace(argOptions.get(WORKSPACE_OPTION));
        }
        if (argOptions.containsKey(PORT_OPTION)) {
            server = new HTTPServer(Integer.parseInt(argOptions
                    .get(PORT_OPTION)), DEFAULT_SERVER_NAME, Tweeter::handle);
        } else {
            server = new HTTPServer(DEFAULT_PORT, DEFAULT_SERVER_NAME, Tweeter::handle);
        }

        try {
            server.start();
        } catch (IOError e) {
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
                argOptions.put(WORKSPACE_OPTION, args[i + 1] + "/");
            }
            if (args[i].equals(HELP_OPTION)) {
                argOptions.put(HELP_OPTION, "");
            }
        }
        return argOptions;
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
        setDefaultsOnResponse(res);
        String reqURI = req.getURI();
        HTTPRequest.Method httpMethod = req.getMethod();
        Logger.log(httpMethod + " " + req.getURI());
        
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
    
    private static void setDefaultsOnResponse(HTTPResponse res) {
        res.setVersion(DEFAULT_RESPONSE_VERSION);
        res.setHeader(HeaderField.CONTENT_TYPE, DEFAULT_RESPONSE_CONTENT_TYPE);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
                RESPONSE_DATE_FORMAT);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        res.setHeader(HeaderField.DATE, dateFormatGmt.format(new Date()));
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
