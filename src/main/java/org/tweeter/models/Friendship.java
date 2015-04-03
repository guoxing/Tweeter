package org.tweeter.models;

import java.util.List;

import org.general.json.JSONList;
import org.general.json.JSONMap;
import org.general.json.JSONObject;

public class Friendship {
    public static List<Long> getUserFriends(long userId) {
        return null;
    }
    
    public static List<Long> getUserFollowers(long userId) {
        return null;
    }
    
    public static void addFriend(long userId, long friendId) {
        return;
    }
    
    public static void deleteFriend(long userId, long friendId) {
        return;
    }
    
    public static JSONObject toJSON(List<Long> ids) {
        JSONMap returnObj = new JSONMap();
        JSONList listOfIds = new JSONList();
        for (Long id : ids) {
            listOfIds.add(id);
        }
        returnObj.put("ids", listOfIds);
        return returnObj;
    }
}
