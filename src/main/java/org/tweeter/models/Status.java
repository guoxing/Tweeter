package org.tweeter.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.general.json.JSONMap;
import org.general.json.JSONObject;
import org.general.json.JSONSerializable;

/**
 * Represents a status.
 * 
 * @author Guoxing Li
 *
 */
public class Status implements JSONSerializable {

	/**
	 * Date format wherein: <br>
	 * 	1st token is 3 letters of the day of the week <br>
	 * 	2nd token is 3 letters of the month <br>
	 *  3rd token is the date (using 2 characters) <br>
	 *  4th token is 2 digits of the hours:2 digits of the minutes:2 digits of the seconds <br>
	 *  5th token is the time zone <br>
	 *  6th token is 4 digits of the year
	 */
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    /**
     * This is a 64-bit non-negative integer. Lower status id's correspond to
     * statuses that were created earlier.
     */
    private long statusId;
    
    /**
     * This is a 64-bit non-negative integer corresponding to the user id of the
     * user who created this status.
     */
    private long userId;
    
    /**
     * Text of the status. This must be shorter than MAX_TWEET_LENGTH characters.
     */
    private String text;
    
    /**
     * Time this status was created (as a string). This will be formatted using
     * SimpleDateFormat and the DATE_FORMAT string specified above.
     */
    private String time;
    
    /**
     * Max number of characters permitted in a status.
     */
    private static final int MAX_TWEET_LENGTH = 140;

    /**
     * Constructs a Status object, passing in the statusId, userId, text and time.
     * 
     * Note that time here is passed in as a Date object.
     * 		
     * @param statusId status id
     * @param userId user id
     * @param text status text
     * @param time time (as a Date object)
     * @throws IllegalArgumentException Thrown if length of text is greater than
     * MAX_TWEET_LENGTH
     */
    public Status(long statusId, long userId, String text, Date time)
    	throws IllegalArgumentException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.statusId = statusId;
        this.userId = userId;
        if (text.length() > MAX_TWEET_LENGTH) {
            throw new IllegalArgumentException("Tweet must be at most "
                    + MAX_TWEET_LENGTH + " characters (" + text + ")");
        }
        this.text = text;
        this.time = dateFormat.format(time);
    }

    /**
     * Constructs a Status object, passing in the statusId, userId, text and time.
     * 
     * Note that time here is passed in as a string of the form DATE_FORMAT
     * (see comments of instance variable DATE_FORMAT above)
     * 		
     * @param statusId status id
     * @param userId user id
     * @param text status text
     * @param time time string
     * @throws IllegalArgumentException Thrown if length of text is greater than
     * MAX_TWEET_LENGTH
     */
    public Status(long statusId, long userId, String text, String time)
    		throws IllegalArgumentException{
        this.statusId = statusId;
        this.userId = userId;
        if (text.length() > MAX_TWEET_LENGTH) {
            throw new IllegalArgumentException("Tweet must be at most "
                    + MAX_TWEET_LENGTH + " characters (" + text + ")");
        }
        this.text = text;
        this.time = time;
    }

    /**
     * Returns id of status. See Status class comments for info on a status id.
     * @return status id
     */
    public long getStatusId() {
        return statusId;
    }

    /**
     * Returns user id of status creator
     * @return user id (of type long) of status creator
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Returns the text of the status
     * @return text of the status
     */
    public String getText() {
        return text;
    }

    /**
     * Returns time the status was created, formatted as DATE_FORMAT
     * (see comments of instance variable DATE_FORMAT for details)
     * 
     * @return time (as a string) this status was created
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns a JSONObject of the form: 
     * {"id": 123, "user": 321, "text":
     * "This is a status of at most 140 chars", "time":
     * "Sun Oct 26 20:52:35 PDT 2014"}
     * 
     * Wherein the fields are filled in with the private instance
     * variables of the status instance.
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