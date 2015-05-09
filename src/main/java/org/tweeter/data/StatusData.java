package org.tweeter.data;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.general.data.DataStorage;
import org.general.util.Logger;

/**
 * Singleton class to query/update status data.
 * 
 * @author Guoxing Li
 *
 */
public class StatusData {

    private static final String FILE_NAME = "status.db";
    private static final int STATUS_CACHE_SIZE = 5;

    // persistent storage
    private DataStorage<Status> storage;
    // the current maximum id
    private long maxStatusId;
    // caches most recent statuses, statusId -> status
    private Map<Long, Status> statusCache;
    // caches all status ownership information, userId -> set of statusId
    private Map<Long, NavigableSet<Long>> ownershipCache;

    private static StatusData statusData;

    private StatusData() {
        storage = new DataStorage<Status>(FILE_NAME, Status.class,
                Status.ENTRY_SIZE);
        maxStatusId = -1;
        // warm up cache
        statusCache = new HashMap<Long, Status>();
        ownershipCache = new HashMap<Long, NavigableSet<Long>>();
        DataStorage<Status>.EntryReader reader = storage.new EntryReader(true);
        Status entry;
        while ((entry = reader.readPrevious()) != null) {
            if (statusCache.size() < STATUS_CACHE_SIZE) {
                statusCache.put(entry.getStatusId(), entry);
                if (entry.getStatusId() > maxStatusId) {
                    maxStatusId = entry.getStatusId();
                }
            }
            ownershipCache.putIfAbsent(entry.getUserId(), new TreeSet<Long>());
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
        ownershipCache.putIfAbsent(userId, new TreeSet<Long>());
        ownershipCache.get(userId).add(status.getStatusId());

        // write to disk
        storage.appendToFile(status);
    }

    /**
     * Get a list of most recent statuses whose owner is userId. Statuses are in
     * reverse chronological order
     * 
     * @param userId
     *            The owner of the returned list statuses
     * @param numStatuses
     *            Specifies the maximum number of statuses returned.
     * @param maxId
     *            Specifies the maximum id. All the statuses in the returned
     *            list must have ids no larger than this value.
     * @return A list of statuses in reverse chronological order. Empty if no
     *         statuses owned by userId.
     */
    public List<Status> getStatusesOnUserId(long userId, long numStatuses,
            long maxId) {
        Set<Long> userIds = new HashSet<Long>();
        userIds.add(userId);
        return getStatusesOnUserIds(userIds, numStatuses, maxId);
    }

    /**
     * Get a list of most recent statuses whose owners are in the userIds set.
     * Statuses are in reverse chronological order
     * 
     * @param userIds
     *            A set of userIds.
     * @param numStatuses
     *            Specifies the maximum number of statuses returned.
     * @param maxId
     *            Specifies the maximum id. All the statuses in the returned
     *            list must have ids no larger than this value.
     * @return A list of statuses in reverse chronological order. Empty list if
     *         no statuses owned by userIds.
     */
    public List<Status> getStatusesOnUserIds(Set<Long> userIds,
            long numStatuses, long maxId) {
        Set<Long> userIdsWithStatus = new HashSet<Long>();
        for (long userId : userIds) {
            if (ownershipCache.get(userId) != null) {
                // only care users that have tweets
                userIdsWithStatus.add(userId);
            }
        }
        // favor readability and robustness over performance
        NavigableSet<Long> statusIds = new TreeSet<Long>();
        for (long userId : userIdsWithStatus) {
            statusIds.addAll(ownershipCache.get(userId).headSet(maxId, true));
        }
        NavigableSet<Long> result = new TreeSet<Long>();
        // get #numStatuses status ids.
        Iterator<Long> iter = statusIds.descendingIterator();
        while (iter.hasNext() && result.size() < numStatuses) {
            result.add(iter.next());
        }
        return getStatuses(result);
    }

    /**
     * Get a list of statuses from a set of statusIds in reverse chronological
     * order.
     * 
     * @param statusIds
     *            A NavigableSet of statusIds in natural order.
     * @return A list of statuses. Status will be missing if its id doesn't
     *         exist.
     */
    private List<Status> getStatuses(NavigableSet<Long> statusIds) {
        if (statusIds.size() == 0) {
            return new ArrayList<Status>(0);
        }
        List<Status> result = new ArrayList<Status>(statusIds.size());
        Iterator<Long> iter = statusIds.descendingIterator();
        long currentStatusId = -1;
        Status status;
        DataStorage<Status>.EntryReader reader = storage.new EntryReader();
        while (iter.hasNext()) {
            currentStatusId = iter.next();
            status = statusCache.get(currentStatusId);
            if (status == null) {
                // fetch older statuses from persistent storage
                // assumes the n'th entry has id (n-1)
                status = reader.readAt(currentStatusId);
                if (status == null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new IOError(e);
                    }
                    throw new IllegalArgumentException(
                            "StatusId out of range. Received id: "
                                    + currentStatusId + " . Range: [0, "
                                    + maxStatusId + "].");
                }
            }
            result.add(status);
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
        return result;
    }

}