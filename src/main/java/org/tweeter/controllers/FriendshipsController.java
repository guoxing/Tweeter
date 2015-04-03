package org.tweeter.controllers;

import java.util.List;
import java.util.Map;

import org.general.json.JSONObject;
import org.tweeter.models.Friendship;

public class FriendshipsController {
    
    public static void createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = Long.parseLong(params.get("my_id"));
            friendId = Long.parseLong(params.get("user_id"));
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        Friendship.addFriend(userId, friendId);
        // Return empty JSON object
    }
    
    public static void deleteFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = Long.parseLong(params.get("my_id"));
            friendId = Long.parseLong(params.get("user_id"));
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        Friendship.deleteFriend(userId, friendId);
        // Return empty JSON object
    }
    
    public static void getFollowers(Map<String, String> params) {
        Long userId = null;
        try {
            userId = Long.parseLong(params.get("user_id"));
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        List<Long> followerIds = Friendship.getUserFollowers(userId);
        
        JSONObject followerIdsAsJSON = Friendship.toJSON(followerIds);
        
        // TODO: Return followerIdsAsJSON
    }
    
    public static void getFriends(Map<String, String> params) {
        Long userId = null;
        try {
            userId = Long.parseLong(params.get("user_id"));
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        List<Long> friendIds = Friendship.getUserFriends(userId);
        
        JSONObject friendIdsAsJSON = Friendship.toJSON(friendIds);
        
        // TODO: Return friendIdsAsJSON
    }
}
