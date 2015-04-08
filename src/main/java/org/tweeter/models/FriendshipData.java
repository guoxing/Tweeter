package org.tweeter.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.general.application.mvc.AppData;
import org.general.logger.Logger;

/**
 * Model that interfaces with the data module to retrieve friend/follower
 * information as well as add/delete friends.
 * 
 * This module operates at a layer with no knowledge of how memory is laid
 * out & no knowledge of Tweeter's API. It simply knows of friendships and
 * is able to query, update and delete them.
 * 
 * If a user A adds user B as their friend, then user A is user B's follower.
 * This information is used when displaying the statuses to appear on a user's
 * home timeline.
 * 
 * Note that because friendships are simply noted by user id's, this class
 * is never actually instantiated. A list of long-types is sufficient in
 * representing the list of friends/followers for a particular user.
 * @author marcelpuyat
 *
 */
public class FriendshipData extends AppData {
    
    private static final String FRIENDSHIP_DATA_FILENAME = "friend_data.txt";
    private static final int LOG_ENTRY_USER_ID_INDEX = 0;
    private static final int LOG_ENTRY_FRIEND_ID_INDEX = 1;
    private static final int LOG_ENTRY_ACTION_INDEX = 2;
    private static final int NUM_FIELDS_IN_LOG_ENTRY = 3;
    
    private static HashMap<Long, Set<Long>> friendshipMap;
    private static HashMap<Long, Set<Long>> followerMap;
    
    public enum FriendAction {
        ADD(1),
        DELETE(0);
        
        private int num;
        private FriendAction(int num) {
            this.num = num;
        }
        
        public static FriendAction parseAction(String str) throws InvalidDataFormattingException {
            switch (Integer.parseInt(str)) {
                case 0:
                    return FriendAction.DELETE;
                case 1:
                    return FriendAction.ADD;
                default:
                    throw new InvalidDataFormattingException("Action must be either 0 (for deletion) or 1 (for addition)");
            }
        }
    }
    
    /**
     * Returns list of ids of friends of the given user. Will be empty
     * if the user has no friends.
     * @param userId
     * @return
     */
    public static Set<Long> getUserFriends(long userId) {
        Logger.log("Getting friends of "+userId);
        return friendshipMap.getOrDefault(userId, new HashSet<Long>());
    }
    
    /**
     * Returns list of ids of followers (i.e. those users that have added
     * this user as their friend) of the given user. Will be empty
     * if the user has no followers.
     * @param userId
     * @return
     */
    public static Set<Long> getUserFollowers(long userId) {
        Logger.log("Getting followers of "+userId);
        return followerMap.getOrDefault(userId, new HashSet<Long>());
    }
    
    /**
     * After this method is called, the user with id friendId will
     * be a friend of the user with id userId. If there was already a friendship
     * here to begin with, nothing happens.
     * @param userId
     * @param friendId
     */
    public static void addFriend(Long userId, Long friendId) {
        Logger.log(friendId+" is now "+userId+"'s friend");
        
        List<String> addFriendLogEntry = Arrays.asList(new String[]{
                userId.toString(), friendId.toString(), String.valueOf(FriendAction.ADD.num)});
        appendToFile(FRIENDSHIP_DATA_FILENAME, addFriendLogEntry);
        appendToMapSet(userId, friendId, friendshipMap);
        appendToMapSet(friendId, userId, followerMap);
    }
    
    private static void appendToMapSet(Long userId, Long friendId, HashMap<Long, Set<Long>> map) {
        if (map.get(userId) == null) {
            Set<Long> newFriendSet = new HashSet<Long>();
            newFriendSet.add(friendId);
            map.put(userId, newFriendSet);
        } else {
            map.get(userId).add(friendId);
        }
    }
    
    /**
     * After this method is called, the user with id friendId will no longer
     * be a friend of the user with id userId. If there was no friendship
     * here to begin with, nothing happens.
     * @param userId
     * @param friendId
     */
    public static void deleteFriend(Long userId, Long friendId) {
        Logger.log(friendId+" is no longer "+userId+"'s friend");
        
        List<String> deleteFriendLogEntry = Arrays.asList(new String[]{
                userId.toString(), friendId.toString(), String.valueOf(FriendAction.DELETE.num)});
        appendToFile(FRIENDSHIP_DATA_FILENAME, deleteFriendLogEntry);
        removeFromMapSet(userId, friendId, friendshipMap);
        removeFromMapSet(friendId, userId, followerMap);
    }
    
    private static void removeFromMapSet(Long userId, Long friendId, HashMap<Long, Set<Long>> map) {
        Set<Long> friendSet = map.get(userId);
        if (friendSet != null) {
            friendSet.remove(friendId);
        }
    }

    @Override
    public void recover() {
        friendshipMap = new HashMap<Long, Set<Long>>();
        followerMap = new HashMap<Long, Set<Long>>();
        while (true/*replace with iteration through lines of FRIENDSHIP_DATA_FILENAME, returning false when done*/) {
            String logEntry = /* AppData.getNextLogEntry(friendship file)*/null;
            try {
                replayLogEntry(logEntry);
            } catch (InvalidDataFormattingException e) {
                e.printStackTrace();
                // TODO: Decide what to do on error here
            }
        }
    }
    
    private static final String INVALID_DATA_FORMATTING_EXCEPTION_MSG = "Log entry for friendship must have 3 "
            + "delimited values: userId (a 64-bit positive integer), friendId (a 64-bit positive integer), "
            + "action (0 for remove, 1 for add)";
    
    private void replayLogEntry(String logEntry) throws InvalidDataFormattingException {
        String[] delimitedLogEntry = logEntry.split(AppData.DELIMITER);
        if (delimitedLogEntry.length != NUM_FIELDS_IN_LOG_ENTRY) {
            throw new InvalidDataFormattingException(INVALID_DATA_FORMATTING_EXCEPTION_MSG);
        }
        
        Long userId;
        Long friendId;
        FriendAction action;
        try {
            userId = Long.parseLong(delimitedLogEntry[LOG_ENTRY_USER_ID_INDEX]);
            friendId = Long.parseLong(delimitedLogEntry[LOG_ENTRY_FRIEND_ID_INDEX]);
            action = FriendAction.parseAction(delimitedLogEntry[LOG_ENTRY_ACTION_INDEX]);
        } catch (Exception e) {
            throw new InvalidDataFormattingException(INVALID_DATA_FORMATTING_EXCEPTION_MSG);
        }
        
        if (action == FriendAction.ADD) {
            appendToMapSet(userId, friendId, friendshipMap);
            appendToMapSet(friendId, userId, followerMap);
        } else {
            removeFromMapSet(userId, friendId, friendshipMap);
            removeFromMapSet(friendId, userId, followerMap);
        }
    }
}
