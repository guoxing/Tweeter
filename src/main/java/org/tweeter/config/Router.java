package org.tweeter.config;

import java.util.Map;

import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * The router is in charge of mapping an HTTP-method & path pair (along with
 * the parameters) to an appropriate controller's method.
 * 
 * It interfaces with the HTTPRequestHandler module, being passed in an HTTPRequest.
 * It then unwraps the HTTPRequest to put together an Action, comprised of
 * the http method, path, and the parameters. The http method and path together
 * uniquely correspond to a controller's method.
 * @author marcelpuyat
 *
 */
public class Router {
    
    /**
     * Helper class that simply wraps together the fields from an HTTP request
     * that are necessary to route a request to a controller's method. The
     * httpMethod combined with the path make up what is referred to as an
     * address. This is then used by the route method in a switch statement
     * to take actions corresponding to the address.
     * @author marcelpuyat
     *
     */
    private static class Action {
        private String httpMethod;
        private String path;
        private Map<String, String> params;
        
        public Action(String httpMethod, String path, Map<String, String> params) {
            this.httpMethod = httpMethod;
            this.path = path;
            this.params = params;
        }
        
        public Map<String, String> getParams() {
            return this.params;
        }
        
        public String getAddress() {
            return httpMethod + " " + path;
        }
    }
    
//    public static void route(HTTPRequest req) {
//        
//    }
    
    /**
     * Routes an httpMethod path pair to a controller's method, passing along
     * the parameters.
     * 
     * The httpMethod, path, and parameter are passed into the method through a
     * helper class (Action) to abstract away all the details of an HTTPRequest.
     * @param action Holds the address (i.e. the httpMethod path pair) and the parameters
     */
    private static void route(Action action) {
        Map<String, String> params = action.getParams();
        switch(action.getAddress()) {
            case "POST /friendships/create":
                FriendshipsController.createFriendship(params);
                return;
            case "POST /friendships/destroy":
                FriendshipsController.deleteFriendship(params);
                return;
            case "GET /followers/ids.json":
                FriendshipsController.getFollowers(params);
                return;
            case "GET /friends/ids.json":
                FriendshipsController.getFriends(params);
                return;
            case "POST /statuses/update":
                StatusesController.updateStatus(params);
                return;
            case "GET /statuses/user_timeline.json":
                StatusesController.getUserTimeline(params);
                return;
            case "GET /statuses/home_timeline.json":
                StatusesController.getHomeTimeline(params);
                return;
            default:
                // TODO: 404 error (invalid path/method pair)
                return;
        }
    }
}
