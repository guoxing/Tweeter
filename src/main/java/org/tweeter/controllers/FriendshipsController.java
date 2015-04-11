package org.tweeter.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.mvc.Controller;
import org.general.data.InvalidDataFormattingException;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.tweeter.data.FriendshipData;

public class FriendshipsController extends Controller {
    
    public static AppResponse createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
            FriendshipData.getInstance().addFriend(userId, friendId);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
        
        return generateSuccessResponse(new JSONMap().toString());
    }
    
    public static AppResponse deleteFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            friendId = getRequiredLongParam("user_id", params);
            FriendshipData.getInstance().deleteFriend(userId, friendId);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
        
        return generateSuccessResponse(new JSONMap().toString());
    }
    
    public static AppResponse getFollowers(Map<String, String> params) {
        Long userId = null;
        List<Long> followerIds = new ArrayList<Long>();
        try {
            userId = getRequiredLongParam("user_id", params);
            followerIds.addAll(FriendshipData.getInstance().getUserFollowers(userId));
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
        return generateSuccessResponse(generateJSONIdList(followerIds).toString());
    }
    
    public static AppResponse getFriends(Map<String, String> params) {
        Long userId = null;
        List<Long> friendIds = new ArrayList<Long>();
        try {
            userId = getRequiredLongParam("user_id", params);
            friendIds.addAll(FriendshipData.getInstance().getUserFriends(userId));
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
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
