package org.tweeter.data;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.general.data.DataStorage;
import org.general.util.Logger;

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
public class FriendshipData {

    private static final String FILE_NAME = "friend.db";

    // persistent storage
    private DataStorage<FriendshipEntry> storage;
    private HashMap<Long, Set<Long>> friendCache;
    private HashMap<Long, Set<Long>> followerCache;

    private static FriendshipData friendshipData;

    /**
     * @throws IOException if cannot instantiate
     */
    private FriendshipData() throws IOException {
        storage = new DataStorage<FriendshipEntry>(FILE_NAME,
                FriendshipEntry.class, FriendshipEntry.ENTRY_SIZE);
        // warm up cache
        friendCache = new HashMap<Long, Set<Long>>();
        followerCache = new HashMap<Long, Set<Long>>();
        DataStorage<FriendshipEntry>.EntryReader reader = storage.new EntryReader();
        FriendshipEntry entry;
        while ((entry = reader.readNext()) != null) {
            updateFriendshipCache(entry);
        }
        reader.close();
    }

    /**
     * Retrieve an (and the only) instance of FriendshipData
     * 
     * @return An instance of FriendshipData
     * @throws IOException on instantiation error
     */
    public static FriendshipData getInstance() throws IOException {
        if (friendshipData == null) {
            friendshipData = new FriendshipData();
        }
        return friendshipData;
    }

    /**
     * Returns set of ids of friends of the given user.
     * 
     * @param userId
     * @return A unmodifiable set of friend ids. Empty if the user has no
     *         friends.
     */
    public Set<Long> getUserFriends(long userId) {
        Logger.log("Getting friends of " + userId);
        return Collections.unmodifiableSet(friendCache.getOrDefault(userId,
                new HashSet<Long>()));
    }

    /**
     * Returns a set of ids of followers (i.e. those users that have added this
     * user as their friend) of the given user.
     * 
     * @param userId
     * @return A unmodifiable set of follower ids. Empty if the user has no
     *         followers.
     */
    public Set<Long> getUserFollowers(long userId) {
        Logger.log("Getting followers of " + userId);
        return Collections.unmodifiableSet(followerCache.getOrDefault(userId,
                new HashSet<Long>()));
    }

    /**
     * After this method is called, the user with id friendId will be a friend
     * of the user with id userId. If there was already a friendship here to
     * begin with, nothing happens.
     * 
     * @param userId
     * @param friendId
     * @throws IOException if unable to add friend
     */
    public void addFriend(Long userId, Long friendId) throws IOException {
        Logger.log(friendId + " is now " + userId + "'s friend");
        if (friendCache.containsKey(userId) && friendCache.get(userId).contains(friendId)) {
            return;
        }
        FriendshipEntry entry = new FriendshipEntry(FriendshipEntry.ACTION_ADD,
                userId, friendId);
        storage.appendToFile(entry);
        updateFriendshipCache(entry);
    }

    /**
     * After this method is called, the user with id friendId will no longer be
     * a friend of the user with id userId. If there was no friendship here to
     * begin with, nothing happens.
     * 
     * @param userId
     * @param friendId
     * @throws IOException if unable to delete friend
     */
    public void deleteFriend(Long userId, Long friendId) throws IOException {
        Logger.log(friendId + " is no longer " + userId + "'s friend");
        if (friendCache.containsKey(userId) && !friendCache.get(userId).contains(friendId)) {
            return;
        }
        FriendshipEntry entry = new FriendshipEntry(
                FriendshipEntry.ACTION_REMOVE, userId, friendId);
        storage.appendToFile(entry);
        updateFriendshipCache(entry);
    }

    /*
     * Update both friendCache and followerCache based on the passed in entry.
     */
    private void updateFriendshipCache(FriendshipEntry entry) {
        if (entry.action == FriendshipEntry.ACTION_ADD) {
            friendCache.putIfAbsent(entry.userId, new HashSet<Long>());
            friendCache.get(entry.userId).add(entry.friendId);
            followerCache.putIfAbsent(entry.friendId, new HashSet<Long>());
            followerCache.get(entry.friendId).add(entry.userId);
        } else {
            Set<Long> followingSet = friendCache.get(entry.userId);
            if (followingSet != null) {
                followingSet.remove(entry.friendId);
            }
            Set<Long> followerSet = followerCache.get(entry.friendId);
            if (followerSet != null) {
                followerSet.remove(entry.userId);
            }
        }
    }
}
