package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONable;
import org.tweeter.controllers.helpers.ErrorResponse;
import org.tweeter.controllers.helpers.ParameterRetriever;
import org.tweeter.models.Status;

public class StatusesController {
    
    private static final int DEFAULT_TIMELINE_SIZE = 20;
    
    public static void updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("my_id", params);
            status = ParameterRetriever.getRequiredStringParam("status", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        try {
            Status.updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            respondWithInvalidStatusError(e.getMessage());
        }
    }
    
    private static void respondWithInvalidStatusError(String message) {
     // TODO: Interface with http response handler
    }
    
    public static void getHomeTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("my_id", params);
            count = ParameterRetriever.getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = ParameterRetriever.getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        List<? extends JSONable> statuses = null;
        
        // TODO: Get appropriate statuses
        
        respondWithJSONList(JSONList.toJSONList(statuses));
    }
    
    public static void getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = ParameterRetriever.getRequiredLongParam("my_id", params);
            count = ParameterRetriever.getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = ParameterRetriever.getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            ErrorResponse.respondWithInvalidParamError(e.getMessage());
        }
        
        List<? extends JSONable> statuses;
        if (maxId != null) {
            statuses = Status.getUserStatuses(userId, count, maxId);
        } else {
            statuses = Status.getUserStatuses(userId, count);
        }
        
        respondWithJSONList(JSONList.toJSONList(statuses));
    }
    
    private static void respondWithJSONList(JSONList listOfStatuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", listOfStatuses);
        // TODO: Return JSON through http response module
    }
}
