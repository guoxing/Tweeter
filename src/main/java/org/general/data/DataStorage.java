package org.general.data;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * A generic data module class that stores data persistently. By design, data
 * can only be appended but not altered nor inserted. Think of this class as an
 * analogy to a table in RMDB. Data is persistent on disk in an entry format,
 * where each entry consists of a number of fixed-length columns. This class
 * provides basic functionality to interact the data (read and write) in the
 * format of entries. Clients should create a customized DataEntry class which
 * represents an entry to use this data module.
 * 
 * @author Guoxing Li
 *
 */
public class DataStorage<Entry extends DataEntry> {

    protected static String pathToWorkspace = "";

    private Class<Entry> entryClazz;
    private int entrySize;
    private File storage;

    public static void setPathToWorkspace(String path) {
        pathToWorkspace = path;
    }

    /**
     * Construct a DataStorage with fixed entrySize.
     * 
     * Example: {@literal DataStorage<Status>} storage =
     * {@literal new DataStorage<Status>("status.db", Status.class)}
     * 
     * @param filename
     *            The filename of the file to persist data on disk
     * @param entryClazz
     *            The class type of the Entry. This should be consistent with
     *            the generic type declared of the instance of this class.
     * @param entrySize
     *            The size in byte of each entry
     */
    public DataStorage(String filename, Class<Entry> entryClazz, int entrySize) {
        File workspaceDir = new File(pathToWorkspace);
        if (!workspaceDir.exists()) {
            workspaceDir.mkdirs();
        }
        storage = new File(pathToWorkspace + filename);
        try {
            storage.createNewFile();
        } catch (IOException e) {
            throw new IOError(e);
        }
        this.entryClazz = entryClazz;
        this.entrySize = entrySize;
        if (storage.length() % entrySize != 0) {
            throw new IllegalStateException("The storage file "
                    + storage.getAbsolutePath()
                    + " is malformated. File length is mismatched.");
        }
    }

    /**
     * Append an entry to the end of the underlying file.
     * 
     * @param entry
     *            Entry to be appended
     */
    public void appendToFile(Entry entry) {
        byte[] data = entry.marshal().array();
        if (data.length != entrySize) {
            throw new IllegalArgumentException(
                    "Error in appending to disk."
                            + " Number of marshalled bytes differs from expected. Expected: "
                            + entrySize + ". Received: " + data.length + ".");
        }

        try {
            FileOutputStream out = new FileOutputStream(storage, true);
            out.write(data);
            out.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * A random access reader behaves much like RandomAccessFile with read
     * access. Instead of treating data as a large array of bytes, this class
     * reads one entry at a time. There's an entry pointer that points to the
     * location where the next read starts. Clients of DataStorage can
     * instantiate this class to read entries.
     *
     */
    public class EntryReader implements Closeable {

        private RandomAccessFile randomReader;

        /**
         * Creates a EntryReader that initially sets entry pointer to the end of
         * the file. See {@link #readPrevious()}.
         * 
         * @param reverse
         *            True will set the entry pointer to the end.
         */
        public EntryReader(boolean reverse) {
            try {
                randomReader = new RandomAccessFile(storage, "r");
                if (reverse) {
                    randomReader.seek(randomReader.length());
                }
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        /**
         * Creates a EntryReader. Read starts from the first entry.
         */
        public EntryReader() {
            this(false);
        }

        /**
         * Read the entry starting from the file pointer.. After read, entry
         * pointer is moved forward one entry.
         * 
         * @return The next entry or null if no more entry to read
         */
        public Entry readNext() {
            byte[] data = new byte[entrySize];
            try {
                int bytesRead = randomReader.read(data);
                if (bytesRead == -1) {
                    return null;
                }
            } catch (IOException e) {
                throw new IOError(e);
            }
            return createEntryFromBytes(data);
        }

        /**
         * Read the entry before where the entry pointer points. After read,
         * entry pointer is moved back one entry. This enables backward read
         * like the following.
         * 
         * EntryReader entryReader = new EntryReader(true);
         * 
         * while ((entry = entryReader.readPrevious() != null)) {}
         * 
         * @return The previous entry or null if no more entry to read
         */
        public Entry readPrevious() {
            byte[] data = new byte[entrySize];
            try {
                long fp = randomReader.getFilePointer();
                fp -= entrySize;
                if (fp < 0) {
                    return null;
                }
                randomReader.seek(fp);
                randomReader.read(data);
                randomReader.seek(fp);
            } catch (IOException e) {
                throw new IOError(e);
            }
            return createEntryFromBytes(data);
        }

        /**
         * Read the (idx + 1)'th entry. The index starts from 0. So readAt(0)
         * returns the first entry. After read, the entry pointer is moved
         * forward one entry.
         * 
         * @return The idx'th entry or null if idx is out of valid index range.
         */
        public Entry readAt(long idx) {
            byte[] data = new byte[entrySize];
            try {
                if (idx < 0 || (idx + 1) * entrySize > randomReader.length()) {
                    return null;
                }
                randomReader.seek(idx * entrySize);
                randomReader.read(data);
            } catch (IOException e) {
                throw new IOError(e);
            }
            return createEntryFromBytes(data);
        }

        /**
         * Close this reader after done reading it.
         */
        @Override
        public void close() throws IOException {
            randomReader.close();
        }

        private Entry createEntryFromBytes(byte[] data) {
            Entry entry = null;
            try {
                entry = entryClazz.newInstance();
                entry.unmarshal(ByteBuffer.wrap(data));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            return entry;
        }
    }

}
