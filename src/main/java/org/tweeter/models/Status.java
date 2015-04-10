package org.tweeter.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.general.json.JSONable;

/**
 * Represents a status.
 * 
 * @author Guoxing Li
 *
 */
public class Status implements JSONable {

    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    private long statusId;
    private long userId;
    private String text;
    private String time; // created time

    public Status(long statusId, long userId, String text, Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.statusId = statusId;
        this.userId = userId;
        this.text = text;
        this.time = dateFormat.format(time);
    }

    public Status(long statusId, long userId, String text, String time) {
        this.statusId = statusId;
        this.userId = userId;
        this.text = text;
        this.time = time;
    }

    public List<String> toEntry() {
        List<String> entry = new ArrayList<String>();
        entry.add(String.valueOf(statusId));
        entry.add(String.valueOf(userId));
        entry.add(text);
        entry.add(time);
        return entry;
    }

    public long getStatusId() {
        return statusId;
    }

    public long getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    /**
     * Returns a JSONObject of the form: {"id": 123, "user": 321, "text":
     * "This is a status of at most 140 chars", "time":
     * "Sun Oct 26 20:52:35 PDT 2014"}
     */
    @Override
    public JSONObject toJSON() {
        JSONMap json = new JSONMap();
        json.put("id", statusId);
        json.put("user", userId);
        json.put("text", text);
        json.put("time", time);
        return json;
    }
}