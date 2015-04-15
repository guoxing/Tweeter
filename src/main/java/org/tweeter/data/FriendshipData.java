package org.tweeter.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.general.data.AppData;
import org.general.logger.Logger;
import org.general.application.InternalError;

/**
 * Singleton class that interfaces with the data module to retrieve
 * friend/follower information as well as add/delete friends.
 * 
 * If a user A adds user B as their friend, then user A is user B's follower.
 * This information is used when displaying the statuses to appear on a user's
 * home timeline.
 * 
 * @author marcelpuyat
 *
 */
public class FriendshipData extends AppData {

    private static final String FILE_NAME = "friend.db";
    private static final int ENTRY_USER_ID_INDEX = 0;
    private static final int ENTRY_FRIEND_ID_INDEX = 1;
    private static final int ENTRY_ACTION_INDEX = 2;
    private static final int NUM_COLS_IN_ENTRY = 3;

    private HashMap<Long, Set<Long>> followingCache;
    private HashMap<Long, Set<Long>> followerCache;

    private enum FriendAction {
        ADD(1), DELETE(0);

        private int num;

        private FriendAction(int num) {
            this.num = num;
        }

        public static FriendAction parseAction(String str)
                throws InternalError {
            switch (Integer.parseInt(str)) {
            case 0:
                return FriendAction.DELETE;
            case 1:
                return FriendAction.ADD;
            default:
            	Logger.log("Action must be either 0 (for deletion) or 1 (for addition)");
                throw new InternalError();
            }
        }
    }

    private static FriendshipData friendshipData;

    private FriendshipData() throws InternalError {
        super(FILE_NAME, NUM_COLS_IN_ENTRY);
    }

    /**
     * Retrieve an (and the only) instance of FriendshipData
     * 
     * @return An instance of FriendshipData
     */
    public static FriendshipData getInstance() throws InternalError {
        if (friendshipData == null) {
        	friendshipData = new FriendshipData();
        }
        return friendshipData;
    }

    /**
     * Returns list of ids of friends (followings) of the given user. Will be
     * empty if the user has no friends.
     * 
     * @param userId
     * @return A set of ids
     */
    public Set<Long> getUserFriends(long userId) {
        Logger.log("Getting friends of " + userId);
        Set<Long> res = followingCache.get(userId);
        res = res == null ? new HashSet<Long>() : res;
        return res;
    }

    /**
     * Returns list of ids of followers (i.e. those users that have added this
     * user as their friend) of the given user. Will be empty if the user has no
     * followers.
     * 
     * @param userId
     * @return A set of ids
     */
    public Set<Long> getUserFollowers(long userId) {
        Logger.log("Getting followers of " + userId);
        Set<Long> res = followerCache.get(userId);
        res = res == null ? new HashSet<Long>() : res;
        return res;
    }

    /**
     * After this method is called, the user with id friendId will be a friend
     * of the user with id userId. If there was already a friendship here to
     * begin with, nothing happens.
     * 
     * @param userId
     * @param friendId
     * @throws InvalidDataFormattingException
     * @throws IOException
     */
    public void addFriend(Long userId, Long friendId) throws InternalError {
        Logger.log(friendId + " is now " + userId + "'s friend");

        List<String> addFriendEntry = Arrays.asList(new String[] {
                userId.toString(), friendId.toString(),
                String.valueOf(FriendAction.ADD.num) });
        appendToFile(addFriendEntry);
        addFriendshipToCache(userId, friendId);
    }

    /**
     * After this method is called, the user with id friendId will no longer be
     * a friend of the user with id userId. If there was no friendship here to
     * begin with, nothing happens.
     * 
     * @param userId
     * @param friendId
     * @throws InvalidDataFormattingException
     * @throws IOException
     */
    public void deleteFriend(Long userId, Long friendId) throws InternalError {
        Logger.log(friendId + " is no longer " + userId + "'s friend");

        List<String> deleteFriendEntry = Arrays.asList(new String[] {
                userId.toString(), friendId.toString(),
                String.valueOf(FriendAction.DELETE.num) });
        appendToFile(deleteFriendEntry);
        removeFriendshipFromCache(userId, friendId);
    }

    @Override
    public void recover() throws InternalError {
        followingCache = new HashMap<Long, Set<Long>>();
        followerCache = new HashMap<Long, Set<Long>>();
        ForwardReader fr;
		try {
			fr = getForwardReader();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InternalError();
		}
        List<String> entry;
        while ((entry = fr.readEntry()) != null) {
            replayEntry(entry);
        }
    }

    private static final String INVALID_DATA_FORMATTING_EXCEPTION_MSG = "Log entry for friendship must have 3 "
            + "delimited values: userId (a 64-bit positive integer), friendId (a 64-bit positive integer), "
            + "action (0 for remove, 1 for add)";

    private void replayEntry(List<String> entry)
            throws InternalError {
        Long userId;
        Long friendId;
        FriendAction action;
        try {
            userId = Long.parseLong(entry.get(ENTRY_USER_ID_INDEX));
            friendId = Long.parseLong(entry.get(ENTRY_FRIEND_ID_INDEX));
        } catch (Exception e) {
        	Logger.log(INVALID_DATA_FORMATTING_EXCEPTION_MSG);
            throw new InternalError();
        }
        action = FriendAction.parseAction(entry.get(ENTRY_ACTION_INDEX));

        if (action == FriendAction.ADD) {
            addFriendshipToCache(userId, friendId);
        } else {
            removeFriendshipFromCache(userId, friendId);
        }
    }

    private void addFriendshipToCache(Long userId, Long friendId) {
        if (followingCache.get(userId) == null) {
            followingCache.put(userId, new HashSet<Long>());
        }
        followingCache.get(userId).add(friendId);
        if (followerCache.get(friendId) == null) {
            followerCache.put(friendId, new HashSet<Long>());
        }
        followerCache.get(friendId).add(userId);
    }

    private void removeFriendshipFromCache(Long userId, Long friendId) {
        Set<Long> followingSet = followingCache.get(userId);
        if (followingSet != null) {
            followingSet.remove(friendId);
        }
        Set<Long> followerSet = followerCache.get(friendId);
        if (followerSet != null) {
            followerSet.remove(userId);
        }
    }
}
