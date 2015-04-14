package org.tweeter.config;

import java.util.Map;

import org.general.application.ApplicationInterface;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

public class Router implements ApplicationInterface {
    
	private static final String CREATE_FRIENDSHIP_ADDRESS = "POST /friendships/create";
	private static final String DESTROY_FRIENDSHIP_ADDRESS = "POST /friendships/destroy";
	private static final String GET_FOLLOWERS_ADDRESS = "GET /followers/ids.json";
	private static final String GET_FRIENDS_ADDRESS = "GET /friends/ids.json";
	private static final String UPDATE_STATUS_ADDRESS = "POST /statuses/update";
	private static final String GET_USER_TIMELINE_ADDRESS = "GET /statuses/user_timeline.json";
	private static final String GET_HOME_TIMELINE_ADDRESS = "GET /statuses/home_timeline.json";
	
    @Override
    public AppResponse respondToAction(AppRequest request) {
        return route(request);
    }
    
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
                return new AppResponse("", AppResponseStatus.INVALID_DESTINATION);
        }
    }
}
