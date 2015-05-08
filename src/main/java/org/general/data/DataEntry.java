package org.general.data;

import java.nio.ByteBuffer;

/**
 * An abstract class that represents an entry stored in AppData. An entry is
 * expected to have a list of columns with fixed length.
 * 
 * NOTE: Subclasses are required to implement a nullary constructor for it to
 * work with DataStorage.
 *
 * @author Guoxing Li
 *
 */
public abstract class DataEntry {


    /**
     * Check if input ByteBuffer has the same size of {@link #entrySize()}. Call
     * this method first in {@link #unmarshal(ByteBuffer))} for a sanity check.
     * 
     * @param in
     *            Input ByteBuffer to check
     * @param entrySize
     *            Expected entry size in bytes.
     */
    protected static void checkValid(ByteBuffer in, int entrySize) {
        if (in.limit() != entrySize) {
            throw new IllegalArgumentException(
                    "Error when unmarshalling ByteBuffer to DataEntry. "
                            + "Number of incoming bytes differs from expected. Expected: "
                            + entrySize + ". Received: " + in.limit() + ".");
        }
    }
    
    /**
     * Convenient method to read a String no larger than maxSize from
     * ByteBuffer. String read ends at the first null byte or maxSize has been
     * read.
     * 
     * @param in
     *            ByteBuffer to read from
     * @param maxSize
     *            The maximum size of the returned String
     * @return A String
     */
    protected static String readString(ByteBuffer in, int maxSize) {
        byte[] strBytes = new byte[maxSize];
        in.get(strBytes);
        int i;
        for (i = 0; i < maxSize && strBytes[i] != 0; ++i) {
        }
        return new String(strBytes, 0, i);
    }

    /**
     * Parse raw bytes to fill in corresponding fields in DateEntry.
     * 
     * @param in
     *            A byte buffer to be parsed
     */
    public abstract void unmarshal(ByteBuffer in);

    /**
     * Convert this DataEntry to an array of bytes to be stored on disk.
     * 
     * @return A byte buffer that contains data of this DataEntry
     */
    public abstract ByteBuffer marshal();

}
