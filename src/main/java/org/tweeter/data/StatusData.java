package org.tweeter.data;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.general.data.DataStorage;
import org.general.logger.Logger;

/**
 * Singleton class to query/update status data.
 * 
 * @author Guoxing Li
 *
 */
public class StatusData {

    private static final String FILE_NAME = "status.db";
    private static final int STATUS_CACHE_SIZE = 1_000;

    // persistent storage
    private DataStorage<Status> storage;
    // the current maximum id
    private long maxStatusId;
    // caches most recent statuses, statusId -> status
    private Map<Long, Status> statusCache;
    // caches all status ownership information, userId -> list of statusId, list
    // is in reverse order
    private Map<Long, List<Long>> ownershipCache;

    private static StatusData statusData;

    private StatusData() {
        storage = new DataStorage<Status>(FILE_NAME, Status.class,
                Status.ENTRY_SIZE);
        maxStatusId = -1;
        // warm up cache
        statusCache = new HashMap<Long, Status>();
        ownershipCache = new HashMap<Long, List<Long>>();
        DataStorage<Status>.EntryReader reader = storage.new EntryReader(true);
        Status entry;
        while ((entry = reader.readPrevious()) != null) {
            if (statusCache.size() < STATUS_CACHE_SIZE) {
                statusCache.put(entry.getStatusId(), entry);
                if (entry.getStatusId() > maxStatusId) {
                    maxStatusId = entry.getStatusId();
                }
            }
            ownershipCache
                    .putIfAbsent(entry.getUserId(), new ArrayList<Long>());
            ownershipCache.get(entry.getUserId()).add(entry.getStatusId());
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * Retrieve an (and the only) instance of StatusData
     * 
     * @return An instance of StatusData
     */
    public static StatusData getInstance() {
        if (statusData == null) {
            statusData = new StatusData();
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
     * @throws IOException
     */
    public void updateStatus(long userId, String text) {
        maxStatusId++;
        Status status = new Status(maxStatusId, userId, text, new Date());
        // evict older status in cache if cache is full
        if (statusCache.size() >= STATUS_CACHE_SIZE) {
            long evictId = maxStatusId - STATUS_CACHE_SIZE;
            Logger.log("[Log]: StatusData Cache evict status: " + evictId);
            if (statusCache.remove(evictId) == null) {
                Logger.log("[Error]: Cache corruption, failure to evict "
                        + evictId);
            }
        }
        statusCache.put(maxStatusId, status);
        if (ownershipCache.get(userId) == null) {
            ownershipCache.put(userId, new ArrayList<Long>());
        }
        ownershipCache.get(userId).add(0, status.getStatusId());

        // write to disk
        storage.appendToFile(status);
    }

    /**
     * Get a list of statuses from a set of status_ids in reverse chronological
     * order. Ids don't have to be sorted.
     * 
     * @param ids
     * @return A list of statuses. Status will be missing if its id doesn't
     *         exist.
     */
    public List<Status> getStatuses(Set<Long> ids) {
        List<Long> idList = new ArrayList<Long>(ids);
        List<Status> result = new ArrayList<Status>(idList.size());
        Collections.sort(idList, Collections.reverseOrder());
        int i = 0;
        // fetch from cache if possible
        while (i < idList.size()) {
            long id = idList.get(i);
            Status status = statusCache.get(id);
            if (status == null) {
                break;
            }
            result.add(status);
            i++;
        }
        // fetch older statuses from persistent storage
        DataStorage<Status>.EntryReader reader = storage.new EntryReader();
        Status entry;
        // assumes the n'th entry has id (n-1)
        while (i < idList.size()
                && (entry = reader.readAt(idList.get(i))) != null) {
            result.add(entry);
            i++;
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
        return result;
    }

    public Set<Long> getStatusIdsOnUserId(long userId, long numStatuses) {
        return getStatusIdsOnUserId(userId, numStatuses, maxStatusId);
    }

    public Set<Long> getStatusIdsOnUserId(long userId, long numStatuses,
            long maxId) {
        Set<Long> userIds = new HashSet<Long>();
        userIds.add(userId);
        return getStatusIdsOnUserIds(userIds, numStatuses, maxId);
    }

    public Set<Long> getStatusIdsOnUserIds(Set<Long> userIds, long numStatuses) {
        return getStatusIdsOnUserIds(userIds, numStatuses, maxStatusId);
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
        maxId = Math.min(maxStatusId, maxId);
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
        for (int i = 0; i < userIds.size(); ++i) {
            if (positions.get(i) >= statusIds.get(i).size()) {
                // hit the end, finished searching
                searched++;
                positions.set(i, -1);
            }
        }
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

}
