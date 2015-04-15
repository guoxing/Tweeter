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
import org.general.logger.Logger;
import org.tweeter.data.FriendshipData;

/**
 * In charge of API endpoints regarding friendships between users.
 * @author marcelpuyat
 *
 */
public class FriendshipsController extends Controller {
    
	/**
	 * Key for my_id parameter
	 */
    private static final String PARAMS_MY_ID_KEY = "my_id";
    
    /**
     * Key for user_id parameter
     */
    private static final String PARAMS_USER_ID_KEY = "user_id";

    /**
     * Creates a friendship from the long value in params associated with "my_id"
     * to the long value in params associated with "user_id".
     * 
     * Will return an empty JSON object on success, or a message with an 
     * error (and a corresponding response status) otherwise.
     * 
     * If the friendship already exists, will still 
     * respond with empty JSON object and success.
     * 
     * @param params Map that should contain values for PARAMS_MY_ID_KEY and PARAMS_USER_ID_KEY. Should not be null.
     * @return Response that will have a body with an empty JSON object on success, or a message with an error.
     */
    public static AppResponse createFriendship(Map<String, String> params) {
    	Logger.log("Creating friendship between " + 
    			params.get(PARAMS_MY_ID_KEY) + " and " + params.get(PARAMS_USER_ID_KEY));
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            friendId = getRequiredLong(PARAMS_USER_ID_KEY, params);
            FriendshipData.getInstance().addFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
        	e.printStackTrace();
            return generateInternalErrorResponse();
        }

        return generateSuccessResponse(new JSONMap().toString());
    }
    
    /**
     * Deletes a friendship from the long value in params associated with "my_id"
     * to the long value in params associated with "user_id".
     * 
     * Will return an empty JSON object on success, or a message with an 
     * error (and a corresponding response status) otherwise.
     * 
     * If the friendship did not exist, will still 
     * respond with empty JSON object and success.
     * 
     * @param params Map that should contain values for PARAMS_MY_ID_KEY and PARAMS_USER_ID_KEY. Should not be null. 
     * @return Response that will have a body with an empty JSON object on success, or a message with an error.
     */
    public static AppResponse deleteFriendship(Map<String, String> params) {
    	Logger.log("Deleting friendship between " + 
    			params.get(PARAMS_MY_ID_KEY) + " and " + params.get(PARAMS_USER_ID_KEY));
        Long userId = null;
        Long friendId = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            friendId = getRequiredLong(PARAMS_USER_ID_KEY, params);
            FriendshipData.getInstance().deleteFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
        	e.printStackTrace();
            return generateInternalErrorResponse();
        }

        return generateSuccessResponse(new JSONMap().toString());
    }
    
    /**
     * Returns an AppResponse with a body containing the followers of a given
     * user.
     * 
     * On success, body of the response contains a JSONObject of the form:
     * 		{ids: [1, 2, 3, etc...]}
     * Returns {ids: []} if user has no followers.
     * 
     * Params must contain a long value associated with PARAMS_USER_ID_KEY, or
     * response will contain error message and have an invalid parameter status.
     * 
     * @param params Map that should contain value for PARAMS_USER_ID_KEY. Should not be null.
     * @return Response that will have a body with a JSON object formatted
     * as mentioned above on success, or a message with an error.
     */
    public static AppResponse getFollowers(Map<String, String> params) {
    	Logger.log("Getting followers JSON of " + params.get(PARAMS_USER_ID_KEY));
        Long userId = null;
        List<Long> followerIds = new ArrayList<Long>();
        try {
            userId = getRequiredLong(PARAMS_USER_ID_KEY, params);
            followerIds.addAll(FriendshipData.getInstance().getUserFollowers(userId));
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
            return generateInvalidParamResponse(e.getMessage());
        }

        return generateSuccessResponse(generateJSONIdList(followerIds).toString());
    }
    
    /**
     * Returns an AppResponse with a body containing the friends of a given
     * user.
     * 
     * On success, body of the response contains a JSONObject of the form:
     * 		{ids: [1, 2, 3, etc...]}
     * Returns {ids: []} if user has no friends.
     * 
     * Params must contain a long value associated with PARAMS_USER_ID_KEY, or
     * response will contain error message and have an invalid parameter status.
     * 
     * @param params Map that should contain value for PARAMS_USER_ID_KEY. Should not be null.
     * @return Response that will have a body with a JSON object formatted
     * as mentioned above on success, or a message with an error.
     */
    public static AppResponse getFriends(Map<String, String> params) {
    	Logger.log("Getting friends JSON of " + params.get(PARAMS_USER_ID_KEY));
        Long userId = null;
        List<Long> friendIds = new ArrayList<Long>();
        try {
            userId = getRequiredLong(PARAMS_USER_ID_KEY, params);
            friendIds.addAll(FriendshipData.getInstance().getUserFriends(userId));
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
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
