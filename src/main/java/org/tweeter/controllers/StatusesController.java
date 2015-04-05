package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.ApplicationResponse;
import org.general.application.mvc.Controller;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.tweeter.models.Status;

public class StatusesController extends Controller {
    
    private static final Long DEFAULT_TIMELINE_SIZE = 20L;
    
    public static ApplicationResponse updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            status = getRequiredStringParam("status", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        try {
            Status.updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        return generateSuccessResponse(new JSONMap().toString());
    }
    
    
    
    public static ApplicationResponse getHomeTimeline(Map<String, String> params) {
        @SuppressWarnings("unused")
        Long userId = null;
        Long count = null;
        @SuppressWarnings("unused")
        Long maxId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            return generateInvalidParamResponse(e.getMessage());
        }
        
        List<Status> statuses = null;
        
        // TODO: Get appropriate statuses
        
        return generateSuccessResponse(generateJSONListOfTweets(statuses).toString());
    }
    
    public static ApplicationResponse getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = DEFAULT_TIMELINE_SIZE;
            }
            maxId = getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            generateInvalidParamResponse(e.getMessage());
        }
        
        List<Status> statuses;
        if (maxId != null) {
            statuses = Status.getUserStatuses(userId, count, maxId);
        } else {
            statuses = Status.getUserStatuses(userId, count);
        }
        
        return generateSuccessResponse(generateJSONListOfTweets(statuses).toString());
    }
    
    private static JSONMap generateJSONListOfTweets(List<Status> statuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", JSONList.toJSONList(statuses));
        return tweets;
    }
}
