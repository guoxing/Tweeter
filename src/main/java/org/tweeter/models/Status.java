package org.tweeter.models;

import java.util.Date;
import java.util.List;

import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.general.json.JSONable;

public class Status implements JSONable {
    private static final int MAX_TWEET_LENGTH = 140;
    private static long nextValidStatusId;
    
    private long statusId;
    private long userId;
    private String text;
    private Date time;
    
    public static void updateStatus(long userId, String text) {
        if (text.length() > MAX_TWEET_LENGTH) {
            throw new IllegalArgumentException("Tweet must be " + MAX_TWEET_LENGTH + 
                    " characters (" + text + ")");
        }
        
        // TODO: Interface with data module
    }
    
    public static List<Status> getUserStatuses(long userId, long numStatuses) {
        return null;
    }
    
    public static List<Status> getUserStatuses(long userId, long numStatuses, long maxId) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public JSONObject toJSON() {
        JSONMap statusJson = new JSONMap();
        statusJson.put("id", statusId);
        statusJson.put("user", userId);
        statusJson.put("text", text);
        statusJson.put("time", time.toGMTString());
        return statusJson;
    }
}
