package org.tweeter.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.general.data.AppData;
import org.general.logger.Logger;

/**
 * Class to query/update status data.
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
    private static final String FILE_NAME = "status.db";
    private static final int STATUS_CACHE_SIZE = 1_000;

    // the current maximum id
    private long currentId;
    // caches most recent statuses, statusId -> status
    private Map<Long, Status> statusCache;
    // caches all status ownership information, userId -> list of statusId, list
    // is in reverse order
    private Map<Long, List<Long>> ownershipCache;

    public StatusData() throws IOException {
        super(FILE_NAME, NUM_COLS_IN_ENTRY);
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
     */
    public void updateStatus(long userId, String text)
            throws IllegalArgumentException, IOException {
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

        // write to disk
        appendToFile(status.toEntry());
    }

    /**
     * Get a list of statuses from a list of status_ids in reverse chronological
     * order
     * 
     * @param ids
     * @return A list of statuses.
     * @throws IOException
     */
    public List<Status> getStatuses(List<Long> ids) throws IOException {
        List<Status> list = new ArrayList<Status>(ids.size());
        Collections.sort(ids, Collections.reverseOrder());
        int i = 0;
        // fetch from cache if possible
        while (i < ids.size()) {
            long id = ids.get(i);
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
        while ((entry = br.readEntry()) != null && i < ids.size()) {
            Status status = parseEntry(entry);
            if (status.getStatusId() == ids.get(i)) {
                list.add(status);
                i++;
            }
        }
        br.close();
        return list;
    }

    public List<Long> getStatusIdsOnUserId(long userId, int numStatuses) {
        return getStatusIdsOnUserId(userId, numStatuses, currentId);
    }

    public List<Long> getStatusIdsOnUserId(long userId, int numStatuses,
            long maxId) {
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(userId);
        return getStatusIdsOnUserIds(userIds, numStatuses, maxId);
    }

    public List<Long> getStatusIdsOnUserIds(List<Long> userIds, int numStatuses) {
        return getStatusIdsOnUserIds(userIds, numStatuses, currentId);
    }

    /**
     * Get a list of most recent status Ids whose owner is in the userIds list.
     * 
     * @param userIds
     *            A list of userIds.
     * @param numStatuses
     *            Specifies the maximum size of the returned list.
     * @param maxId
     *            Specifies the maximum id. All the ids in the returned list
     *            must be no larger than this value.
     * @return A list of statusIds.
     */
    public List<Long> getStatusIdsOnUserIds(List<Long> userIds,
            int numStatuses, long maxId) {
        List<Long> res = new ArrayList<Long>();
        List<Long> temp = new ArrayList<Long>();
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
    public void recover() throws IOException {
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
