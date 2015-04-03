package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.tweeter.controllers.helpers.ErrorResponse;
import org.tweeter.controllers.helpers.ParameterRetriever;
import org.tweeter.models.Friendship;

public class FriendshipsController {
    
    public static void createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("my_id", params);
            friendId = ParameterRetriever.getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        Friendship.addFriend(userId, friendId);
        returnEmptyJSONObject();
    }
    
    private static void returnEmptyJSONObject() {
        // TODO: Call response handler
    }
    
    public static void deleteFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("my_id", params);
            friendId = ParameterRetriever.getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        Friendship.deleteFriend(userId, friendId);
        returnEmptyJSONObject();
    }
    
    public static void getFollowers(Map<String, String> params) {
        Long userId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        List<Long> followerIds = Friendship.getUserFollowers(userId);
        
        respondWithJSONIdList(followerIds);
    }
    
    public static void getFriends(Map<String, String> params) {
        Long userId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        List<Long> friendIds = Friendship.getUserFriends(userId);
        
        respondWithJSONIdList(friendIds);
    }
    
    /**
     * Returns a JSONObject of the form:
     *  {"ids": [1, 6, 3, 9, 10]}
     * Where the ids in the array will be those from the list of ids
     * passed in (in order).
     * @param ids
     * @return
     */
    private static void respondWithJSONIdList(List<Long> ids) {
        JSONMap jSONresponse = new JSONMap();
        JSONList listOfIds = new JSONList();
        for (Long id : ids) {
            listOfIds.add(id);
        }
        jSONresponse.put("ids", listOfIds);
        
        // TODO: Interface with http response handler
    }
}
