package org.tweeter.data;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.general.data.DataEntry;
import org.general.json.JSONObject;
import org.general.json.JSONObject.JSONSerializable;

/**
 * Represents a status. A status is immutable after creation.
 * 
 * @author Guoxing Li
 *
 */
public class Status extends DataEntry implements JSONSerializable {

    /**
     * Date format wherein: <br>
     * 1st token is 3 letters of the day of the week <br>
     * 2nd token is 3 letters of the month <br>
     * 3rd token is the date (using 2 characters) <br>
     * 4th token is 2 digits of the hours:2 digits of the minutes:2 digits of
     * the seconds <br>
     * 5th token is the time zone <br>
     * 6th token is 4 digits of the year
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
     * Text of the status. This must be shorter than MAX_TWEET_LENGTH
     * characters.
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
    private static final int MAX_DATE_LENGTH = 140;

    // Entry size in bytes. Used for DataStorage.
    // statusId + userId + text + date
    public static final int ENTRY_SIZE = Long.BYTES + Long.BYTES
            + MAX_TWEET_LENGTH + MAX_DATE_LENGTH;

    /**
     * Nullary constructor required by DataStorage.
     */
    public Status() {
    }

    /**
     * Constructs a Status object, passing in the statusId, userId, text and
     * time.
     * 
     * Note that time here is passed in as a Date object.
     * 
     * @param statusId
     *            status id
     * @param userId
     *            user id
     * @param text
     *            status text
     * @param time
     *            time (as a Date object)
     * @throws IllegalArgumentException
     *             Thrown if length of text is greater than MAX_TWEET_LENGTH
     */
    public Status(long statusId, long userId, String text, Date time) {
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
     * Returns id of status. See Status class comments for info on a status id.
     * 
     * @return status id
     */
    public long getStatusId() {
        return statusId;
    }

    /**
     * Returns user id of status creator
     * 
     * @return user id (of type long) of status creator
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Returns the text of the status
     * 
     * @return text of the status
     */
    public String getText() {
        return text;
    }

    /**
     * Returns time the status was created, formatted as DATE_FORMAT (see
     * comments of instance variable DATE_FORMAT for details)
     * 
     * @return time (as a string) this status was created
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns a JSONObject of the form: {"id": 123, "user": 321, "text":
     * "This is a status of at most 140 chars", "time":
     * "Sun Oct 26 20:52:35 PDT 2014"}
     * 
     * Wherein the fields are filled in with the private instance variables of
     * the status instance.
     */
    @Override
    public JSONObject toJsonObject() {
        Map<String, JSONObject> map = new HashMap<>();
        map.put("id", new JSONObject(statusId));
        map.put("user", new JSONObject(userId));
        map.put("text", new JSONObject(text));
        map.put("time", new JSONObject(time));
        return new JSONObject(map);
    }

    @Override
    public void unmarshal(ByteBuffer in) {
        checkValid(in, ENTRY_SIZE);
        statusId = in.getLong();
        userId = in.getLong();
        text = readString(in, MAX_TWEET_LENGTH);
        time = readString(in, MAX_DATE_LENGTH);
    }

    @Override
    public ByteBuffer marshal() {
        ByteBuffer out = ByteBuffer.allocate(ENTRY_SIZE);
        out.putLong(statusId);
        out.putLong(userId);
        out.put(Arrays.copyOf(text.getBytes(), MAX_TWEET_LENGTH));
        if (time.length() > MAX_DATE_LENGTH) {
            throw new IllegalStateException(
                    "Time string length is too large. Max: " + MAX_DATE_LENGTH
                            + " Received: " + time.length());
        }
        out.put(Arrays.copyOf(time.getBytes(), MAX_DATE_LENGTH));
        return out;
    }

}