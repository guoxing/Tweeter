package org.tweeter.controllers;

import java.util.List;
import java.util.Map;

import org.general.json.JSONList;
import org.general.json.JSONObject;
import org.general.json.JSONable;
import org.tweeter.models.Status;

public class StatusesController {
    
    private static final int DEFAULT_TIMELINE_SIZE = 20;
    
    public static void updateStatus(Map<String, String> params) {
        Long userId = null;
        String status = null;
        try {
            userId = Long.parseLong(params.get("my_id"));
            status = params.get("status");
            if (status == null) {
                throw new IllegalArgumentException("Must enter a status");
            }
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        } catch (IllegalArgumentException e) {
            // TODO: Must enter a status
        }
        
        try {
            Status.updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            // TODO: Tweet too long here
        }
    }
    
    public static void getHomeTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = Long.parseLong(params.get("my_id"));
            count = Long.parseLong(params.get("user_id"));
            
            // Max count is optional, so only parse if not null
            String maxIdAsString = params.get("max_count");
            if (maxIdAsString != null) {
                maxId = Long.parseLong(maxIdAsString);
            }
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        List<? extends JSONable> statuses = null;
        
        // TODO: Get appropriate statuses
        
        JSONObject statusesAsJson = JSONList.toJSONList(statuses);
        // TODO: Return JSON
    }
    
    public static void getUserTimeline(Map<String, String> params) {
        Long userId = null;
        Long count = null;
        Long maxId = null;
        try {
            userId = Long.parseLong(params.get("my_id"));
            count = Long.parseLong(params.get("user_id"));
            
            // Max count is optional, so only parse if not null
            String maxIdAsString = params.get("max_count");
            if (maxIdAsString != null) {
                maxId = Long.parseLong(maxIdAsString);
            }
        } catch (NumberFormatException e) {
            // TODO: Invalid parameter response here
        }
        
        List<? extends JSONable> statuses;
        if (maxId != null) {
            statuses = Status.getUserStatuses(userId, count, maxId);
        } else {
            statuses = Status.getUserStatuses(userId, count);
        }
        JSONObject statusesAsJson = JSONList.toJSONList(statuses);
        // TODO: Return JSON
    }
}
