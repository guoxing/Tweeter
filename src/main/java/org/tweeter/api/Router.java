package org.tweeter.api;

import java.util.HashMap;
import java.util.Map;

import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.StatusCode;
import org.general.http.InvalidHttpParametersException;
import org.general.json.JSONObject;
import org.general.util.Logger;
import org.general.util.Pair;

/**
 * Responds to an HTTP request by routing the request to a particular controller's method.
 * In Tweeter's current implementation, these responses will always be in JSON.
 * 
 * @author marcelpuyat
 *
 */
public class Router {
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
        addRoute("/statuses/update", HTTPRequest.Method.POST, StatusesController::updateStatus);
        addRoute("/statuses/home_timeline.json", HTTPRequest.Method.GET, StatusesController::getHomeTimeline);
        addRoute("/statuses/user_timeline.json", HTTPRequest.Method.GET, StatusesController::getUserTimeline);
        addRoute("/friends/ids.json", HTTPRequest.Method.GET, FriendshipsController::getFriends);
        addRoute("/followers/ids.json", HTTPRequest.Method.GET, FriendshipsController::getFollowers);
        addRoute("/friendships/destroy", HTTPRequest.Method.POST, FriendshipsController::deleteFriendship);
        addRoute("/friendships/create", HTTPRequest.Method.POST, FriendshipsController::createFriendship);
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
    public static void route(HTTPRequest req, HTTPResponse res) {
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
        }
    }
    
    /**
     * Assigns a particular HTTP method and controller method to an API endpoint path in our routerMap.
     */
    private static void addRoute(String path, HTTPRequest.Method method, ControllerMethod<HTTPRequest, JSONObject> reqHandler) {
        routerMap.put(path, new Pair<HTTPRequest.Method, ControllerMethod<HTTPRequest, JSONObject>>(method, reqHandler));
    }
    
    private static void respondWithJSONError(StatusCode code,
            String errorMessage, HTTPResponse res) {
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
    private interface ControllerMethod<Req, Res> {
        Res apply(Req req) throws InvalidHttpParametersException;
    }
}
