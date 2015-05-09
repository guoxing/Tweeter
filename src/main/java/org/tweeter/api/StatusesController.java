package org.tweeter.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.http.HTTPRequest;
import org.general.http.InvalidHttpParametersException;
import org.general.json.JSONObject;
import org.general.util.Logger;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.Status;
import org.tweeter.data.StatusData;

/**
 * In charge of API endpoints regarding users' statuses.
 * 
 * @author marcelpuyat
 *
 */
public class StatusesController {
    /**
     * If timeline size parameter is not given when retrieving a timeline, will
     * retrieve this many statuses.
     */
    private static final Long DEFAULT_TIMELINE_SIZE = 20L;
    /**
     * If max id parameter is not given, will not put a bound on max id of
     * statuses to retrieve (besides bound on Long type).
     */
    private static final Long DEFAULT_MAX_ID = Long.MAX_VALUE;
    /**
     * Parameter that holds the id of the user to perform a given action on
     */
    private static final String PARAMS_MY_ID_KEY = "my_id";
    /**
     * Parameter that holds a new status body
     */
    private static final String PARAMS_STATUS_KEY = "status";
    /**
     * Parameter that indicate the max number of statuses to retrieve
     */
    private static final String PARAMS_COUNT_KEY = "count";
    /**
     * Parameter that indicates what the maximum id of any retrieved status
     * should be
     */
    private static final String PARAMS_MAX_ID_KEY = "max_id";

    /**
     * Updates the status of a user.
     * 
     * Parameters must include a user_id (which must be parsable into a number)
     * and a status.
     * 
     * Will return an empty JSON object.
     * 
     * @throws InvalidHttpParametersException if my_id does not exist or is not a number,
     * or if status does not exist
     */
    public static JSONObject updateStatus(HTTPRequest req) throws InvalidHttpParametersException {
        Long userId = req.getRequiredLongParam(PARAMS_MY_ID_KEY);
        String status = req.getStringRequiredParam(PARAMS_STATUS_KEY);
        Logger.log("Updating status of " + userId);
        StatusData.getInstance().updateStatus(userId, status);
        return new JSONObject(new HashMap<>());
    }

    /**
     * Returns home timeline (in json) of a given user. The home
     * timeline includes statuses of all the user's friends and the user's own
     * statuses.
     * 
     * See generateJSONOfTweets method for format of JSON object returned.
     * 
     * Parameters must include a user_id to indicate whose user home timeline
     * should be returned, and may optionally include a count to indicate the
     * max number of tweets to get and a max_id to indicate the max id of any
     * status to be retrieved.
     * 
     * @throws InvalidHttpParametersException if user_id param does not exist or is not a number,
     * or if count or max_id param is not a number
     */
    public static JSONObject getHomeTimeline(HTTPRequest req) throws InvalidHttpParametersException {
        Long userId = req.getRequiredLongParam(PARAMS_MY_ID_KEY);
        Long count = req.getOptionalLongParam(PARAMS_COUNT_KEY, DEFAULT_TIMELINE_SIZE);
        Long maxId = req.getOptionalLongParam(PARAMS_MAX_ID_KEY, DEFAULT_MAX_ID);
        Logger.log("Returning JSON of home timeline of " + userId);

        Set<Long> friendIds = FriendshipData.getInstance().getUserFriends(userId);
        Set<Long> userIds = new HashSet<Long>();
        userIds.addAll(friendIds);
        userIds.add(userId);
        List<Status> statuses = StatusData.getInstance().getStatusesOnUserIds(userIds,
                count, maxId);

        return generateJSONOfTweets(statuses);
    }

    /**
     * Returns a user timeline (in json) of a given user. The user
     * timeline includes all the statuses of the user.
     * 
     * See generateJSONOfTweets method for format of JSON object returned.
     * 
     * Parameters must include a user_id to indicate whose user timeline should
     * be returned, and may optionally include a count to indicate the max
     * number of tweets to get and a max_id to indicate the max id of any status
     * to be retrieved.

     * @throws InvalidHttpParametersException if user_id param does not exist or is not a number,
     * or if count or max_id param is not a number
     */
    public static JSONObject getUserTimeline(HTTPRequest req) throws InvalidHttpParametersException {
        Long userId = req.getRequiredLongParam(PARAMS_MY_ID_KEY);
        Long count = req.getOptionalLongParam(PARAMS_COUNT_KEY, DEFAULT_TIMELINE_SIZE);
        Long maxId = req.getOptionalLongParam(PARAMS_MAX_ID_KEY, DEFAULT_MAX_ID);
        Logger.log("Returning JSON of user timeline of " + userId);
        List<Status> statuses = StatusData.getInstance().getStatusesOnUserId(userId,
                count, maxId);
        return generateJSONOfTweets(statuses);
    }

    /**
     * Returns JSON List of statuses given a list of statuses (that should not
     * be null). Will be of a form as specified in this link:
     * https://web.stanford.edu/~ouster/cgi-bin/cs190-spring15/tweeter.php
     * 
     * Note that the order of statuses given in the list is retained in the JSONObject
     * returned.
     */
    private static JSONObject generateJSONOfTweets(List<Status> statuses) {
        Map<String, JSONObject> tweets = new HashMap<>();
        tweets.put("tweets", JSONObject.fromJSONables(statuses));
        return new JSONObject(tweets);
    }
}
