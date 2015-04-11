package org.tweeter.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.application.ApplicationInterface.AppResponse;
import org.general.application.mvc.Controller;
import org.general.data.InvalidDataFormattingException;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.StatusData;
import org.tweeter.models.Status;

public class StatusesController extends Controller {
    
    private static final Long DEFAULT_TIMELINE_SIZE = 20L;
    
    public static AppResponse updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            status = getRequiredStringParam("status", params);
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
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = getOptionalLongParam("max_id", params);
            Set<Long> timelineUserIds = FriendshipData.getInstance().getUserFriends(userId);
            timelineUserIds.add(userId);
            if (maxId == null) {
                statuses = StatusData.getInstance().getStatuses(StatusData.getInstance().getStatusIdsOnUserIds(new ArrayList<Long>(timelineUserIds), count, DEFAULT_TIMELINE_SIZE));
            }
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
        return generateSuccessResponse(generateJSONListOfTweets(statuses).toString());
    }
    
    public static AppResponse getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        List<Status> statuses = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = DEFAULT_TIMELINE_SIZE;
            }
            maxId = getOptionalLongParam("max_id", params);
            if (maxId != null) {
                statuses = StatusData.getInstance().getStatuses(StatusData.getInstance().getStatusIdsOnUserId(userId, count, maxId));
            } else {
                statuses = StatusData.getInstance().getStatuses(StatusData.getInstance().getStatusIdsOnUserId(userId, count));
            }
        } catch (InvalidParameterException e) {
            generateInvalidParamResponse(e.getMessage());
        } catch (IOException | InvalidDataFormattingException e) {
            return generateInternalErrorResponse();
        }
        
        return generateSuccessResponse(generateJSONListOfTweets(statuses).toString());
    }
    
    private static JSONMap generateJSONListOfTweets(List<Status> statuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", JSONList.toJSONList(statuses));
        return tweets;
    }
}
