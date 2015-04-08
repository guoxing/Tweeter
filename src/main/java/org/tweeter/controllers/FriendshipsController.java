package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.mvc.Controller;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.tweeter.models.FriendshipData;

public class FriendshipsController extends Controller {
    
    public static AppResponse createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        FriendshipData.addFriend(userId, friendId);
        return generateSuccessResponse(new JSONMap().toString());
    }
    
    public static AppResponse deleteFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        FriendshipData.deleteFriend(userId, friendId);
        return generateSuccessResponse(new JSONMap().toString());
    }
    
    public static AppResponse getFollowers(Map<String, String> params) {
        Long userId = null;
        try {
            userId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        List<Long> followerIds = new ArrayList<Long>();
        followerIds.addAll(FriendshipData.getUserFollowers(userId));
        
        return generateSuccessResponse(generateJSONIdList(followerIds).toString());
    }
    
    public static AppResponse getFriends(Map<String, String> params) {
        Long userId = null;
        try {
            userId = getRequiredLongParam("user_id", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        List<Long> friendIds = new ArrayList<Long>();
        friendIds.addAll(FriendshipData.getUserFriends(userId));
        
        return generateSuccessResponse(generateJSONIdList(friendIds).toString());
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
