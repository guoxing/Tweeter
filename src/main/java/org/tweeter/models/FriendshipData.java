package org.tweeter.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    public enum Action {
        ADD(1),
        DELETE(0);
        
        private int num;
        private Action(int num) {
            this.num = num;
        }
        
        public int getNum() {
            return this.num;
        }
    }
    
    /**
     * Returns list of ids of friends of the given user. Will be empty
     * if the user has no friends.
     * @param userId
     * @return
     */
    public static List<Long> getUserFriends(long userId) {
        Logger.log("Getting friends of "+userId);
        return new ArrayList<Long>();
    }
    
    /**
     * Returns list of ids of followers (i.e. those users that have added
     * this user as their friend) of the given user. Will be empty
     * if the user has no followers.
     * @param userId
     * @return
     */
    public static List<Long> getUserFollowers(long userId) {
        Logger.log("Getting followers of "+userId);
        return new ArrayList<Long>();
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
                userId.toString(), friendId.toString(), String.valueOf(Action.ADD.num)});
        appendToFile(FRIENDSHIP_DATA_FILENAME, addFriendLogEntry);
        return;
    }
    
    /**
     * After this method is called, the user with id friendId will no longer
     * be a friend of the user with id userId. If there was no friendship
     * here to begin with, nothing happens.
     * @param userId
     * @param friendId
     */
    public static void deleteFriend(long userId, long friendId) {
        Logger.log(friendId+" is no longer "+userId+"'s friend");
        return;
    }

    @Override
    public void recover() {
        // TODO Auto-generated method stub
        
    }
}
