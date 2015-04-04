package org.tweeter.controllers;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.general.application.ApplicationInterface.ApplicationDatagram;
import org.general.application.ApplicationInterface.ApplicationResult;
import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONable;
import org.tweeter.config.Router;
import org.tweeter.models.Status;

public class StatusesController extends Controller {
    
    private static final int DEFAULT_TIMELINE_SIZE = 20;
    
    public static ApplicationDatagram updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            status = getRequiredStringParam("status", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        try {
            Status.updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        return new ApplicationDatagram(new JSONMap().toString(), ApplicationResult.SUCCESS);
    }
    
    
    
    public static ApplicationDatagram getHomeTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            return respondWithInvalidParamError(e.getMessage());
        }
        
        List<? extends JSONable> statuses = null;
        
        // TODO: Get appropriate statuses
        
        JSONMap tweetsAsJSON = respondWithJSONListOfTweets(JSONList.toJSONList(statuses));
        return new ApplicationDatagram(tweetsAsJSON.toString(), ApplicationResult.SUCCESS);
    }
    
    public static ApplicationDatagram getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = getRequiredLongParam("my_id", params);
            count = getOptionalLongParam("count", params);
            if (count == null) {
                count = 20L;
            }
            maxId = getOptionalLongParam("max_id", params);
        } catch (InvalidParameterException e) {
            respondWithInvalidParamError(e.getMessage());
        }
        
        List<? extends JSONable> statuses;
        if (maxId != null) {
            statuses = Status.getUserStatuses(userId, count, maxId);
        } else {
            statuses = Status.getUserStatuses(userId, count);
        }
        
        JSONMap tweetsAsJSON = respondWithJSONListOfTweets(JSONList.toJSONList(statuses));
        return new ApplicationDatagram(tweetsAsJSON.toString(), ApplicationResult.SUCCESS);
    }
    
    private static JSONMap respondWithJSONListOfTweets(JSONList listOfStatuses) {
        JSONMap tweets = new JSONMap();
        tweets.put("tweets", listOfStatuses);
        return tweets;
    }
}
