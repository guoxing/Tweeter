package org.tweeter.config;

import java.util.Map;

import org.tweeter.controllers.FriendshipsController;
import org.tweeter.controllers.StatusesController;

public class Router {
    
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
        }
    }
}
