package org.tweeter.data;

import java.nio.ByteBuffer;

import org.general.data.DataEntry;

/**
 * A friendship entry used to access data in FriendshipData.
 *
 * @author Guoxing Li
 *
 */
public class FriendshipEntry extends DataEntry {

    static final byte ACTION_ADD = 1;
    static final byte ACTION_REMOVE = 0;

    byte action;
    long userId;
    long friendId;

    // entry size in bytes.
    // action + userId + friendId
    public final static int ENTRY_SIZE = Byte.BYTES + Long.BYTES + Long.BYTES;

    /**
     * Nullary constructor required by DataStorage.
     */
    public FriendshipEntry() {
    }

    FriendshipEntry(byte action, long userId, long friendId) {
        this.action = action;
        this.userId = userId;
        this.friendId = friendId;
    }

    @Override
    public void unmarshal(ByteBuffer in) {
        checkValid(in, ENTRY_SIZE);
        action = in.get();
        if (action != ACTION_ADD && action != ACTION_REMOVE) {
            throw new IllegalArgumentException(
                    "Illegal action found. Incoming ByteBuffer is probably malformatted."
                            + " Received action: " + action);
        }
        userId = in.getLong();
        friendId = in.getLong();
    }

    @Override
    public ByteBuffer marshal() {
        ByteBuffer out = ByteBuffer.allocate(ENTRY_SIZE);
        out.put(action);
        out.putLong(userId);
        out.putLong(friendId);
        return out;
    }
}