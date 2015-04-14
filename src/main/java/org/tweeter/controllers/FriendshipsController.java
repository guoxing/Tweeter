package org.tweeter.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.Controller;
import org.general.data.InvalidDataFormattingException;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.tweeter.data.FriendshipData;

public class FriendshipsController extends Controller {
    
    private static final String PARAMS_MY_ID_KEY = "my_id";
    private static final String PARAMS_USER_ID_KEY = "user_id";

    public static AppResponse createFriendship(Map<String, String> params) {
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLongParam(PARAMS_MY_ID_KEY, params);
            friendId = getRequiredLongParam(PARAMS_USER_ID_KEY, params);
            FriendshipData.getInstance().addFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
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
            userId = getRequiredLongParam(PARAMS_MY_ID_KEY, params);
            friendId = getRequiredLongParam(PARAMS_USER_ID_KEY, params);
            FriendshipData.getInstance().deleteFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
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
            userId = getRequiredLongParam(PARAMS_USER_ID_KEY, params);
            followerIds.addAll(FriendshipData.getInstance().getUserFollowers(userId));
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
        }

        return generateSuccessResponse(generateJSONIdList(followerIds).toString());
    }
    
    public static AppResponse getFriends(Map<String, String> params) {
        Long userId = null;
        List<Long> friendIds = new ArrayList<Long>();
        try {
            userId = getRequiredLongParam(PARAMS_USER_ID_KEY, params);
            friendIds.addAll(FriendshipData.getInstance().getUserFriends(userId));
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
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
