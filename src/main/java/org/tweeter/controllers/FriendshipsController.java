package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.ApplicationDatagram;
import org.general.application.ApplicationInterface.ApplicationResult;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.tweeter.models.Friendship;

public class FriendshipsController extends Controller {
    
    public static ApplicationDatagram createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        Friendship.addFriend(userId, friendId);
        return new ApplicationDatagram(generateEmptyJSONObject().toString(), ApplicationResult.SUCCESS);
    }
    
    private static JSONObject generateEmptyJSONObject() {
        return new JSONMap();
    }
    
    public static ApplicationDatagram deleteFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        Friendship.deleteFriend(userId, friendId);
        return new ApplicationDatagram(generateEmptyJSONObject().toString(), ApplicationResult.SUCCESS);
    }
    
    public static ApplicationDatagram getFollowers(Map<String, String> params) {
        Long userId = null;
        try {
            userId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        List<Long> followerIds = Friendship.getUserFollowers(userId);
        
        return new ApplicationDatagram(generateJSONIdList(followerIds).toString(), ApplicationResult.SUCCESS);
    }
    
    public static ApplicationDatagram getFriends(Map<String, String> params) {
        Long userId = null;
        try {
            userId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        List<Long> friendIds = Friendship.getUserFriends(userId);
        
        return new ApplicationDatagram(generateJSONIdList(friendIds).toString(), ApplicationResult.SUCCESS);
    }
    
    /**
     * Returns a JSONObject of the form:
     *  {"ids": [1, 6, 3, 9, 10]}
     * Where the ids in the array will be those from the list of ids
     * passed in (in order).
     * @param ids
     * @return
     */
    private static JSONObject generateJSONIdList(List<Long> ids) {
        JSONMap jSONresponse = new JSONMap();
        JSONList listOfIds = new JSONList();
        for (Long id : ids) {
            listOfIds.add(id);
        }
        jSONresponse.put("ids", listOfIds);
        
        return jSONresponse;
    }
}
