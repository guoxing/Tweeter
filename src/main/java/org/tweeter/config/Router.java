package org.tweeter.config;

import java.util.Map;

import org.general.application.ApplicationInterface;
import org.general.application.ApplicationInterface.AppResponse.AppResponseStatus;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

/**
 * Responds to an application request by routing the request (using its address)
 * to a particular controller's method.
 * @author marcelpuyat
 *
 */
public class Router implements ApplicationInterface {
	/**
	 * To add a new action (aka a new API endpoint):
	 * 		1. Create a new address (comprised of a method and a path, i.e. POST /friendships/create)
	 * 		2. Add a new method to a controller (or create a new controller with a method), and
	 * 		   have this method (which receives the parameters of the request) return an AppResponse object.
	 * 		3. Add the new address to the switch statement in the route method in this class and
	 * 		   call the new method you created.
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
	 * Responds to application request with an application response object.
	 * @param request Application request (must not be null)
	 * @throws NullPointerException if request is null
	 * @return Application response
	 */
    public AppResponse respondToAppReq(AppRequest request) {
    	if (request == null) throw new NullPointerException("App request cannot be null");
        return route(request);
    }
    
    /**
     * Routes the given request to a controller's action and returns
     * the response of that action.
     * @param request Application request
     * @return Application response.
     */
    private static AppResponse route(AppRequest request) {
        Map<String, String> params = request.getParams();
        switch(request.getAddress()) {
            case CREATE_FRIENDSHIP_ADDRESS:
                return FriendshipsController.createFriendship(params);
            case DESTROY_FRIENDSHIP_ADDRESS:
                return FriendshipsController.deleteFriendship(params);
            case GET_FOLLOWERS_ADDRESS:
                return FriendshipsController.getFollowers(params);
            case GET_FRIENDS_ADDRESS:
                return FriendshipsController.getFriends(params);
            case UPDATE_STATUS_ADDRESS:
                return StatusesController.updateStatus(params);
            case GET_USER_TIMELINE_ADDRESS:
                return StatusesController.getUserTimeline(params);
            case GET_HOME_TIMELINE_ADDRESS:
                return StatusesController.getHomeTimeline(params);
            default:
                return new AppResponse("Invalid destination", AppResponseStatus.INVALID_DESTINATION);
        }
    }
}
