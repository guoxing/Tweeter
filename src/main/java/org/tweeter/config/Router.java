package org.tweeter.config;

import org.general.application.Controller;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.StatusCode;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * Responds to an application request by routing the request (using its address)
 * to a particular controller's method.
 * 
 * @author marcelpuyat
 *
 */
public class Router {
    /**
     * To add a new action (aka a new API endpoint): 1. Create a new address
     * (comprised of a method and a path, i.e. POST /friendships/create) 2. Add
     * a new method to a controller (or create a new controller with a method),
     * and have this method (which receives the parameters of the request)
     * return an AppResponse object. 3. Add the new address to the switch
     * statement in the route method in this class and call the new method you
     * created.
     */

    /**
     * Address for creating a friendship
     */
    private static final String CREATE_FRIENDSHIP_ADDRESS = "POST /friendships/create";

    /**
     * Address for destroying a friendship
     */
    private static final String DESTROY_FRIENDSHIP_ADDRESS = "POST /friendships/destroy";

    /**
     * Address for retrieving a user's followers
     */
    private static final String GET_FOLLOWERS_ADDRESS = "GET /followers/ids.json";

    /**
     * Address for retrieving a user's friends
     */
    private static final String GET_FRIENDS_ADDRESS = "GET /friends/ids.json";

    /**
     * Address for updating a user's status
     */
    private static final String UPDATE_STATUS_ADDRESS = "POST /statuses/update";

    /**
     * Address for retrieving a user's timeline
     */
    private static final String GET_USER_TIMELINE_ADDRESS = "GET /statuses/user_timeline.json";

    /**
     * Address for updating a user's home timeline
     */
    private static final String GET_HOME_TIMELINE_ADDRESS = "GET /statuses/home_timeline.json";

    /**
     * Default message prefix for when a path is invalid
     */
    private static final String FILE_NOT_FOUND_MESSAGE = "File not found:";

    /**
     * Routes the given request to a controller's action and returns the
     * response of that action.
     * 
     * @param request
     *            HTTP Request
     * @return body response
     */
    static void route(HTTPRequest req, HTTPResponse res) {
        System.out.println(req.getMethod() + " " + req.getURI());
        switch (req.getMethod() + " " + req.getURI()) {
        case CREATE_FRIENDSHIP_ADDRESS:
            FriendshipsController.createFriendship(req.getQueryParams(), res);
            return;
        case DESTROY_FRIENDSHIP_ADDRESS:
            FriendshipsController.deleteFriendship(req.getQueryParams(), res);
            return;
        case GET_FOLLOWERS_ADDRESS:
            FriendshipsController.getFollowers(req.getQueryParams(), res);
            return;
        case GET_FRIENDS_ADDRESS:
            FriendshipsController.getFriends(req.getQueryParams(), res);
            return;
        case UPDATE_STATUS_ADDRESS:
            StatusesController.updateStatus(req.getQueryParams(), res);
            return;
        case GET_USER_TIMELINE_ADDRESS:
            StatusesController.getUserTimeline(req.getQueryParams(), res);
            return;
        case GET_HOME_TIMELINE_ADDRESS:
            StatusesController.getHomeTimeline(req.getQueryParams(), res);
            return;
        default:
            Controller.respondWithJSONError(StatusCode.NOT_FOUND,
                    FILE_NOT_FOUND_MESSAGE + " " + req.getURI(), res);
        }
    }
}
