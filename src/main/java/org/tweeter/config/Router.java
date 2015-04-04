package org.tweeter.config;

import java.util.Map;

import org.general.application.ApplicationInterface;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * The router is in charge of mapping an HTTP-method & path pair (along with
 * the parameters) to an appropriate controller's method, and then passing
 * the response of the method back to the HTTPHandler.
 * 
 * It implements HTTPHandler, being passed an HTTPRequest.
 * It then unwraps the HTTPRequest to put together an Action, comprised of
 * the http method, path, and the parameters. The http method and path together
 * uniquely correspond to a controller's method.
 * @author marcelpuyat
 *
 */
public class Router implements ApplicationInterface {
    
    @Override
    public ApplicationDatagram respondToAction(ApplicationAction action) {
        return route(action);
    }
    
    /**
     * Routes an httpMethod path pair to a controller's method, passing along
     * the parameters.
     * 
     * The httpMethod, path, and parameter are passed into the method through a
     * helper class (Action) to abstract away all the details of an HTTPRequest.
     * @param action Holds the address (i.e. the httpMethod path pair) and the parameters
     */
    private static ApplicationDatagram route(ApplicationAction action) {
        Map<String, String> params = action.getParams();
        switch(action.getAddress()) {
            case "POST /friendships/create":
                return FriendshipsController.createFriendship(params);
            case "POST /friendships/destroy":
                return FriendshipsController.deleteFriendship(params);
            case "GET /followers/ids.json":
                return FriendshipsController.getFollowers(params);
            case "GET /friends/ids.json":
                return FriendshipsController.getFriends(params);
            case "POST /statuses/update":
                return StatusesController.updateStatus(params);
            case "GET /statuses/user_timeline.json":
                return StatusesController.getUserTimeline(params);
            case "GET /statuses/home_timeline.json":
                return StatusesController.getHomeTimeline(params);
            default:
                return new ApplicationDatagram("", ApplicationResult.INVALID_PATH);
        }
    }
}
