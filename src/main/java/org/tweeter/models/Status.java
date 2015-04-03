package org.tweeter.models;

import java.util.Date;
import java.util.List;

import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.general.json.JSONable;

/**
 * Model that interfaces with data module to retrieve and update
 * statuses.
 * 
 * This module operates at a layer with no knowledge of how memory is laid
 * out & no knowledge of Tweeter's API. It simply knows of statuses and
 * is able to query and update them.
 * 
 * Statuses contain a statusId (64-bit integer unique to this status, and increasing
 * with the time the status was created), a userId for the status' user, text which
 * must be at most of length MAX_TWEET_LENGTH, and a time.
 * @author marcelpuyat
 *
 */
public class Status implements JSONable {
    
    private static final int MAX_TWEET_LENGTH = 140;
    
    /* Fields for an instance of a status. All of these must exist. */
    private long statusId;
    private long userId;
    private String text;
    private Date time;
    
    public long getStatusId() {
        return this.statusId;
    }
    
    public long userId() {
        return this.userId;
    }
    
    public String getText() {
        return this.text;
    }
    
    public Date getTime() {
        return this.time;
    }
    
    /**
     * Private constructor. Will create a status for user with given
     * userId and text. Takes care of assigning a statusId and time-stamping
     * the status.
     * @param userId
     * @param text
     */
    private Status(long userId, String text) {
        // TODO: This is just a placeholder.
        this.statusId = 12910818; // TODO: Get nextValidStatusId from data module
        
        // TODO: Update nextValidStatusId using data module interface (which will flush new one to disk)
        
        this.userId = userId;
        this.text = text;
        this.time = new Date();
    }
    
    /**
     * Creates new status for the given user. This method takes care of timestamping
     * the status and assigning it a status id.
     * @param userId user id of user that is updating their status
     * @param text status text
     * @throws IllegalArgumentException if tweet is longer than MAX_TWEET_LENGTH
     */
    public static void updateStatus(long userId, String text) throws IllegalArgumentException {
        if (text.length() > MAX_TWEET_LENGTH) {
            throw new IllegalArgumentException("Tweet must be " + MAX_TWEET_LENGTH + 
                    " characters (" + text + ")");
        }
        
        // TODO: Interface with data module
    }
    
    /**
     * Returns list of statuses for the given user.
     * 
     * Will attempt to populate list with numStatuses statuses. If user does
     * not have this many statuses, will simply return all of the user's statuses.
     * 
     * Will be empty if user has no statuses.
     * @param userId
     * @param numStatuses Desired number of statuses to retrieve. Will return all
     * of user's statuses if user does not have this many statuses.
     * @return
     */
    public static List<Status> getUserStatuses(long userId, long numStatuses) {
        return null;
    }
    
    /**
     * Returns list of statuses for the given user.
     * 
     * Will attempt to populate list with numStatuses statuses. If user does
     * not have this many statuses, will simply return all of the user's statuses.
     * 
     * Will also limit results to statuses that have a statusId <= maxId.
     * 
     * Will be empty if user has no statuses.
     * @param userId
     * @param numStatuses Desired number of statuses to retrieve. Will return all
     * of user's statuses if user does not have this many statuses.
     * @param maxId Limits results to statuses that have a statusId <= maxId
     * @return 
     */
    public static List<Status> getUserStatuses(long userId, long numStatuses, long maxId) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    /**
     * Returns a JSONObject of the form:
     * {"id": 123, "user": 321, "text": "This is a status of at most 140 chars", 
     * "time": "Sun Oct 26 20:52:35 PDT 2014"}
     */
    public JSONObject toJSON() {
        JSONMap statusJson = new JSONMap();
        statusJson.put("id", statusId);
        statusJson.put("user", userId);
        statusJson.put("text", text);
        statusJson.put("time", time.toGMTString());
        return statusJson;
    }
}
