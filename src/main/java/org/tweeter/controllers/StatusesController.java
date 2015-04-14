package org.tweeter.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.Controller;
import org.general.data.InvalidDataFormattingException;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.StatusData;
import org.tweeter.models.Status;

/**
 * In charge of API endpoints regarding users' statuses
 * @author marcelpuyat
 *
 */
public class StatusesController extends Controller {

    private static final Long DEFAULT_TIMELINE_SIZE = 20L;
    private static final Long DEFAULT_MAX_ID = Long.MAX_VALUE;
    private static final String PARAMS_MY_ID_KEY = "my_id";
    private static final String PARAMS_STATUS_KEY = "status";
    private static final String PARAMS_COUNT_KEY = "count";
    private static final String PARAMS_MAX_ID_KEY = "max_id";

    public static AppResponse updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            status = getRequiredString(PARAMS_STATUS_KEY, params);
            StatusData.getInstance().updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        return generateSuccessResponse(new JSONMap().toString());
    }

    public static AppResponse getHomeTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        List<Status> statuses = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            count = getOptionalLongOrDefault(PARAMS_COUNT_KEY, params, DEFAULT_TIMELINE_SIZE);
            maxId = getOptionalLongOrDefault(PARAMS_MAX_ID_KEY, params, DEFAULT_MAX_ID);
            Set<Long> timelineUserIds = FriendshipData.getInstance()
                    .getUserFriends(userId);
            timelineUserIds.add(userId);
            Set<Long> statusIds;
            if (maxId == null) {
                statusIds = StatusData.getInstance().getStatusIdsOnUserIds(
                        timelineUserIds, count);
            } else {
                statusIds = StatusData.getInstance().getStatusIdsOnUserIds(
                        timelineUserIds, count, maxId);
            }
            statuses = StatusData.getInstance().getStatuses(statusIds);
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }

        return generateSuccessResponse(generateJSONListOfTweets(statuses)
                .toString());
    }

    public static AppResponse getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        List<Status> statuses = null;
        try {
            userId = getRequiredLong(PARAMS_MY_ID_KEY, params);
            count = getOptionalLongOrDefault(PARAMS_COUNT_KEY, params, DEFAULT_TIMELINE_SIZE);
            maxId = getOptionalLongOrDefault(PARAMS_MAX_ID_KEY, params, DEFAULT_MAX_ID);
            Set<Long> statusIds;
            if (maxId == null) {
                statusIds = StatusData.getInstance().getStatusIdsOnUserId(
                        userId, count);
            } else {
                statusIds = StatusData.getInstance().getStatusIdsOnUserId(
                        userId, count, maxId);
            }
            statuses = StatusData.getInstance().getStatuses(statusIds);
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
        	e.printStackTrace();
            return generateInternalErrorResponse();
        }

        return generateSuccessResponse(generateJSONListOfTweets(statuses)
                .toString());
    }

    private static JSONMap generateJSONListOfTweets(List<Status> statuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", JSONList.toJSONList(statuses));
        return tweets;
    }
}
