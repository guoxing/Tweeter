package org.tweeter.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.application.Controller;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.StatusCode;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.logger.Logger;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.Status;
import org.tweeter.data.StatusData;

/**
 * In charge of API endpoints regarding users' statuses.
 * 
 * On failure of any of this classes methods, will respond with JSON formatted
 * as such:
 * 
 * {"error": "Error message here"}
 * 
 * with an HTTP Status code of StatusCode.BAD_REQUEST if the parameters passed
 * in are malformed, or StatusCode.SERVER_ERROR if an internal error occurs.
 * 
 * @author marcelpuyat
 *
 */
public class StatusesController extends Controller {

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
     * Parameters must include a user_id (which must be parsable into a long)
     * and a status.
     * 
     * Will respond with an empty JSON object as a result on success, or a
     * message with an error (and a corresponding response status) on failure.
     * 
     * @param params
     *            Parameters that must include the keys "user_id" and "status"
     * @param res
     *            HTTP Response
     */
    public static void updateStatus(Map<String, String> params, HTTPResponse res) {
        Logger.log("Updating status of " + params.get(PARAMS_MY_ID_KEY));
        Long userId = null;
        String status = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            status = getRequiredString(PARAMS_STATUS_KEY, params);
            StatusData.getInstance().updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            respondWithJSONError(StatusCode.BAD_REQUEST, e.getMessage(), res);
            return;
        }
        res.send(StatusCode.OK, new JSONMap().toString());
    }

    /**
     * Responds with the home timeline (in json) of a given user. The home
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
     * @param params
     *            Parameters that must include the key "user_id" and may include
     *            "count" and "max_id"
     * @param res
     *            HTTP request
     */
    public static void getHomeTimeline(Map<String, String> params,
            HTTPResponse res) {
        Logger.log("Returning JSON of home timeline of "
                + params.get(PARAMS_MY_ID_KEY));
        Long userId = null;
        Long count = null;
        Long maxId = null;
        List<Status> statuses = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            count = getOptionalLongOrDefault(PARAMS_COUNT_KEY, params,
                    DEFAULT_TIMELINE_SIZE);
            maxId = getOptionalLongOrDefault(PARAMS_MAX_ID_KEY, params,
                    DEFAULT_MAX_ID);

            Set<Long> friendIds = FriendshipData.getInstance()
                    .getUserFriends(userId);
            Set<Long> userIds = new HashSet<Long>();
            userIds.addAll(friendIds);
            userIds.add(userId);

            Set<Long> statusIds = StatusData.getInstance()
                    .getStatusIdsOnUserIds(userIds, count, maxId);
            statuses = StatusData.getInstance().getStatuses(statusIds);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            respondWithJSONError(StatusCode.BAD_REQUEST, e.getMessage(), res);
            return;
        }

        res.send(StatusCode.OK, generateJSONOfTweets(statuses).toString());
    }

    /**
     * Responds with the user timeline (in json) of a given user. The user
     * timeline includes all the statuses of the user.
     * 
     * See generateJSONOfTweets method for format of JSON object returned.
     * 
     * Parameters must include a user_id to indicate whose user timeline should
     * be returned, and may optionally include a count to indicate the max
     * number of tweets to get and a max_id to indicate the max id of any status
     * to be retrieved.
     * 
     * @param params
     *            Parameters that must include the key "user_id" and may include
     *            "count" and "max_id"
     */
    public static void getUserTimeline(Map<String, String> params,
            HTTPResponse res) {
        Logger.log("Returning JSON of user timeline of "
                + params.get(PARAMS_MY_ID_KEY));
        Long userId = null;
        Long count = null;
        Long maxId = null;
        List<Status> statuses = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            count = getOptionalLongOrDefault(PARAMS_COUNT_KEY, params,
                    DEFAULT_TIMELINE_SIZE);
            maxId = getOptionalLongOrDefault(PARAMS_MAX_ID_KEY, params,
                    DEFAULT_MAX_ID);
            Set<Long> statusIds = StatusData.getInstance()
                    .getStatusIdsOnUserId(userId, count, maxId);
            statuses = StatusData.getInstance().getStatuses(statusIds);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            respondWithJSONError(StatusCode.BAD_REQUEST, e.getMessage(), res);
            return;
        }

        res.send(StatusCode.OK, generateJSONOfTweets(statuses).toString());
    }

    /**
     * Generates JSON List of statuses given a list of statuses (that should not
     * be null). Will be of the form:
     * 
     * {"tweets": [ {"id": 20115, "user": 84, "time":
     * "Mon Oct 27 18:02:57 PDT 2014", "text": "On my way home"}, {"id": 18442,
     * "user": 84, "time": "Sun Oct 26 20:52:35 PDT 2014", "text":
     * "Just saw a flying saucer!"} ]}
     * 
     * with tweets in the same order as given in the list.
     * 
     * @param statuses
     *            List of statuses
     * @return JSON object of tweets formatted as described above
     */
    private static JSONMap generateJSONOfTweets(List<Status> statuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", JSONList.toJSONList(statuses));
        return tweets;
    }
}
