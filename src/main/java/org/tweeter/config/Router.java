package org.tweeter.config;

import java.util.Map;

import org.general.application.ApplicationInterface;
import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

public class Router implements ApplicationInterface {
    
    @Override
    public ApplicationDatagram respondToAction(ApplicationAction action) {
        return route(action);
    }
    
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
