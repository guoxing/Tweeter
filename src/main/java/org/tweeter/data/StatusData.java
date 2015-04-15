package org.tweeter.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.data.AppData;
import org.general.data.InvalidDataFormattingException;
import org.general.logger.Logger;
import org.tweeter.models.Status;

/**
 * Singleton class to query/update status data.
 * 
 * @author Guoxing Li
 *
 */
public class StatusData extends AppData {

    private static final int NUM_COLS_IN_ENTRY = 4;
    private static final int ENTRY_STATUS_ID_IDX = 0;
    private static final int ENTRY_USER_ID_IDX = 1;
    private static final int ENTRY_TEXT_IDX = 2;
    private static final int ENTRY_TIME_IDX = 3;

    private static final int MAX_TWEET_LENGTH = 140;
    private static String PATH = "";
    private static final String FILE_NAME = "status.db";
    private static final int STATUS_CACHE_SIZE = 1_000;

    // the current maximum id
    private long currentId;
    // caches most recent statuses, statusId -> status
    private Map<Long, Status> statusCache;
    // caches all status ownership information, userId -> list of statusId, list
    // is in reverse order
    private Map<Long, List<Long>> ownershipCache;

    private static StatusData statusData;

    private StatusData() throws IOException, InvalidDataFormattingException {
        super(FILE_NAME, NUM_COLS_IN_ENTRY);
    }

    /**
     * Retrieve an (and the only) instance of StatusData
     * 
     * @return An instance of StatusData
     */
    public static StatusData getInstance() {
        if (statusData == null) {
            try {
                statusData = new StatusData();
            } catch (IOException | InvalidDataFormattingException e) {
                e.printStackTrace();
                throw new Error("Error in initializing StatusData");
            }
        }
        return statusData;
    }

    /**
     * Creates new status for the given user. This method takes care of
     * timestamping the status and assigning it a status id.
     * 
     * @param userId
     *            user id of user that is updating their status
     * @param text
     *            status text
     * @throws IllegalArgumentException
     *             if tweet is longer than MAX_TWEET_LENGTH
     * @throws IOException
     * @throws InvalidDataFormattingException
     */
    public void updateStatus(long userId, String text)
            throws IllegalArgumentException, IOException,
            InvalidDataFormattingException {
        if (text.length() > MAX_TWEET_LENGTH) {
            throw new IllegalArgumentException("Tweet must be "
                    + MAX_TWEET_LENGTH + " characters (" + text + ")");
        }

        currentId++;
        Status status = new Status(currentId, userId, text, new Date());
        // evict older status in cache if cache is full
        if (statusCache.size() >= STATUS_CACHE_SIZE) {
            long evictId = currentId - STATUS_CACHE_SIZE;
            if (statusCache.remove(evictId) == null) {
                Logger.log("[Error]: Cache corrupted");
            }
        }
        statusCache.put(currentId, status);
        if (ownershipCache.get(userId) == null) {
            ownershipCache.put(userId, new ArrayList<Long>());
        }
        ownershipCache.get(userId).add(status.getStatusId());

        // write to disk
        appendToFile(status.toEntry());
    }

    /**
     * Get a list of statuses from a set of status_ids in reverse chronological
     * order. Ids don't have to be sorted.
     * 
     * @param ids
     * @return A list of statuses.
     * @throws IOException
     * @throws InvalidDataFormattingException
     */
    public List<Status> getStatuses(Set<Long> ids) throws IOException,
            InvalidDataFormattingException {
        List<Long> idList = new ArrayList<Long>(ids);
        List<Status> list = new ArrayList<Status>(idList.size());
        Collections.sort(idList, Collections.reverseOrder());
        int i = 0;
        // fetch from cache if possible
        while (i < idList.size()) {
            long id = idList.get(i);
            Status status = statusCache.get(id);
            if (status == null) {
                break;
            }
            list.add(status);
            i++;
        }
        // fetch from persistent storage
        BackwardReader br = getBackwardReader();
        List<String> entry;
        while ((entry = br.readEntry()) != null && i < idList.size()) {
            Status status = parseEntry(entry);
            if (status.getStatusId() == idList.get(i)) {
                list.add(status);
                i++;
            }
        }
        br.close();
        return list;
    }

    public Set<Long> getStatusIdsOnUserId(long userId, long numStatuses) {
        return getStatusIdsOnUserId(userId, numStatuses, currentId);
    }

    public Set<Long> getStatusIdsOnUserId(long userId, long numStatuses,
            long maxId) {
        Set<Long> userIds = new HashSet<Long>();
        userIds.add(userId);
        return getStatusIdsOnUserIds(userIds, numStatuses, maxId);
    }

    public Set<Long> getStatusIdsOnUserIds(Set<Long> userIds, long numStatuses) {
        return getStatusIdsOnUserIds(userIds, numStatuses, currentId);
    }

    /**
     * Get a set of most recent status Ids whose owner is in the userIds set.
     * 
     * @param userIds
     *            A set of userIds.
     * @param numStatuses
     *            Specifies the maximum size of the returned set.
     * @param maxId
     *            Specifies the maximum id. All the ids in the returned set must
     *            be no larger than this value.
     * @return A set of statusIds.
     */
    public Set<Long> getStatusIdsOnUserIds(Set<Long> userIds, long numStatuses,
            long maxId) {
        Set<Long> res = new HashSet<Long>();
        Set<Long> temp = new HashSet<Long>();
        for (long userId : userIds) {
            if (ownershipCache.get(userId) != null) {
                // only care users that have tweets
                temp.add(userId);
            }
        }
        userIds = temp;

        List<List<Long>> statusIds = new ArrayList<List<Long>>();
        List<Integer> positions = new ArrayList<Integer>();
        for (long userId : userIds) {
            int pos = Collections.binarySearch(ownershipCache.get(userId),
                    maxId, Collections.reverseOrder());
            if (pos < 0) {
                pos = -pos - 1;
            }
            positions.add(pos);
            statusIds.add(ownershipCache.get(userId));
        }

        // number of userIds that finished searching
        int searched = 0;
        // use a merge sort like approach to select at most #numStatues ids
        while (res.size() < numStatuses && searched < userIds.size()) {
            int maxIdx = -1;
            long max = -1;
            for (int i = 0; i < userIds.size(); ++i) {
                if (positions.get(i) < 0) {
                    continue;
                }
                long current = statusIds.get(i).get(positions.get(i));
                if (current > max) {
                    max = current;
                    maxIdx = i;
                }
            }
            res.add(max);
            positions.set(maxIdx, positions.get(maxIdx) + 1);
            if (positions.get(maxIdx) >= statusIds.get(maxIdx).size()) {
                // hit the end, finished searching
                searched++;
                positions.set(maxIdx, -1);
            }
        }
        return res;
    }

    @Override
    public void recover() throws IOException, InvalidDataFormattingException {
        statusCache = new HashMap<Long, Status>();
        ownershipCache = new HashMap<Long, List<Long>>();
        BackwardReader br = getBackwardReader();
        List<String> entry;
        while ((entry = br.readEntry()) != null) {
            Status status = parseEntry(entry);
            if (statusCache.size() < STATUS_CACHE_SIZE) {
                statusCache.put(status.getStatusId(), status);
                if (status.getStatusId() > currentId) {
                    currentId = status.getStatusId();
                }
            }
            List<Long> ids = ownershipCache.get(status.getUserId());
            if (ids == null) {
                ownershipCache.put(status.getUserId(), new ArrayList<Long>());
            }
            ids.add(status.getStatusId());
        }
        br.close();
    }

    private Status parseEntry(List<String> entry) {
        long statusId = Long.parseLong(entry.get(ENTRY_STATUS_ID_IDX));
        long userId = Long.parseLong(entry.get(ENTRY_USER_ID_IDX));
        String text = entry.get(ENTRY_TEXT_IDX);
        String time = entry.get(ENTRY_TIME_IDX);
        return new Status(statusId, userId, text, time);
    }

}
