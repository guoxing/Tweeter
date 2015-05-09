package org.tweeter.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.general.http.HTTPRequest;
import org.general.http.InvalidHttpParametersException;
import org.general.json.JSONObject;
import org.general.util.Logger;
import org.tweeter.data.FriendshipData;

/**
 * In charge of API endpoints regarding friendships between users.
 * 
 * @author marcelpuyat
 *
 */
public class FriendshipsController {
    /**
     * Key for my_id parameter
     */
    private static final String PARAMS_MY_ID_KEY = "my_id";
    /**
     * Key for user_id parameter
     */
    private static final String PARAMS_USER_ID_KEY = "user_id";

    /**
     * Creates a friendship from the long value in params associated with
     * "my_id" to the long value in params associated with "user_id".
     * 
     * Will return an empty JSON object.
     * 
     * If the friendship already exists, will still return empty JSON
     * object and success.
     *
     * @throws InvalidHttpParametersException if user_id or my_id param does not exist or is not a number 
     * @throws IOException if unable to write data
     */
    public static JSONObject createFriendship(HTTPRequest req) throws InvalidHttpParametersException, IOException {
        Long userId = req.getRequiredLongParam(PARAMS_MY_ID_KEY);
        Long friendId = req.getRequiredLongParam(PARAMS_USER_ID_KEY);
        Logger.log("Creating friendship between " + userId + " and "+ friendId);
        FriendshipData.getInstance().addFriend(userId, friendId);
        return new JSONObject(new HashMap<>());
    }

    /**
     * Deletes a friendship from the long value in params associated with
     * "my_id" to the long value in params associated with "user_id".
     * 
     * Will return an empty JSON object.
     * 
     * If the friendship did not exist, will still return empty JSON object.
     * 
     * @throws InvalidHttpParametersException if user_id or my_id param does not exist or is not a number
     * @throws IOException if unable to write data
     */
    public static JSONObject deleteFriendship(HTTPRequest req) throws InvalidHttpParametersException, IOException {
        Long userId = req.getRequiredLongParam(PARAMS_MY_ID_KEY);
        Long friendId = req.getRequiredLongParam(PARAMS_USER_ID_KEY);
        Logger.log("Deleting friendship between " + userId + " and " + friendId);
        FriendshipData.getInstance().deleteFriend(userId, friendId);
        return new JSONObject(new HashMap<>());
    }

    /**
     * Returns JSONObject of the form: 
     * {ids: [1, 2, 3, etc...]} Returns {ids: []} if user has no followers.
     * 
     * Params must contain a long value associated with PARAMS_USER_ID_KEY.
     * @throws InvalidHttpParametersException if user_id param does not exist or is not a number
     * @throws IOException if unable to read data
     */
    public static JSONObject getFollowers(HTTPRequest req) throws InvalidHttpParametersException, IOException {
        Long userId = req.getRequiredLongParam(PARAMS_USER_ID_KEY);
        Logger.log("Getting followers JSON of "+ userId);
        List<Long> followerIds = new ArrayList<Long>();
        followerIds.addAll(FriendshipData.getInstance().getUserFollowers(userId));
        return generateJSONIdList(followerIds);
    }

    /**
     * Returns JSON array of the friends of a given user.
     * 
     * On success, body of the response contains a JSONObject of the form:
     * {"ids": [1, 2, 3, etc...]} Returns {"ids": []} if user has no friends.
     * 
     * Params must contain a long value associated with PARAMS_USER_ID_KEY.
     * @throws InvalidHttpParametersException if user_id param does not exist or is not a number
     * @throws IOException if unable to read data
     */
    public static JSONObject getFriends(HTTPRequest req) throws InvalidHttpParametersException, IOException {
        Long userId = req.getRequiredLongParam(PARAMS_USER_ID_KEY);
        Logger.log("Getting friends JSON of "+ userId);
        List<Long> friendIds = new ArrayList<Long>();
        friendIds.addAll(FriendshipData.getInstance().getUserFriends(userId));
        return generateJSONIdList(friendIds);
    }

    /**
     * Returns a JSONObject of the form: {"ids": [1, 6, 3, 9, 10]} Where the ids
     * in the array will be those from the list of ids passed in (in order).
     */
    private static JSONObject generateJSONIdList(List<Long> ids) {
        Map<String, JSONObject> map = new HashMap<>();
        map.put("ids", JSONObject.fromNumbers(ids));
        return new JSONObject(map);
    }
}
