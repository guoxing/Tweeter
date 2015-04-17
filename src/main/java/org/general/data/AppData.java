package org.general.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic data module class that stores data both persistently and in memory.
 * By design, data can only be appended but not altered nor inserted. Think of
 * this class as an analogy to a table in RMDB. The data module is persistent on
 * disk by storing each entry as a line of tab-separated columns. Entries are
 * delimited by newline character '\n'.
 * 
 * Note that this class is a singleton class. Subclasses should conform to this
 * pattern, i.e. implement a getInstance() method.
 * 
 * @author Guoxing Li
 *
 */
public abstract class AppData {

    static final char RESERVED_HEADER = '\\';
    static final Map<String, String> ENCODE_MAP;
    static {
        ENCODE_MAP = new HashMap<String, String>();
        ENCODE_MAP.put("\\", "\\\\");
        ENCODE_MAP.put("\t", "\\t");
        ENCODE_MAP.put("\n", "\\n");
    }
    static final Map<String, String> DECODE_MAP;
    static {
        DECODE_MAP = new HashMap<String, String>();
        DECODE_MAP.put("\\\\", "\\");
        DECODE_MAP.put("\\t", "\t");
        DECODE_MAP.put("\\n", "\n");
    }

    protected static final char COL_DELIMITER = '\t';
    protected static final char ROW_DELIMITER = '\n';
    protected static String pathToWorkspace = "";

    protected File storage;

    // number of columns per entry
    protected int numCols;

    public static void setPathToWorkspace(String path) {
        pathToWorkspace = path;
    }

    protected AppData(String filename, int numCols) throws IOException,
            InvalidDataFormattingException {
        storage = new File(pathToWorkspace + filename);
        // creates the file if not exists.
        storage.createNewFile();
        this.numCols = numCols;
        recover();
    }

    /**
     * Append an entry to the persistent storage.
     * 
     * @param entry
     *            Entry to be appended.
     * @throws IOException
     * @throws InvalidDataFormattingException
     */
    protected void appendToFile(List<String> entry) throws IOException,
            InvalidDataFormattingException {
        if (entry.size() != numCols) {
            throw new InvalidDataFormattingException(
                    "Wrong number of cols written to file! Expected #cols: "
                            + numCols + " Real #cols: " + entry.size());
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                storage, true)));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entry.size() - 1; ++i) {
            sb.append(DataEncoder.encode(entry.get(i)));
            sb.append(COL_DELIMITER);
        }
        sb.append(DataEncoder.encode(entry.get(entry.size() - 1)));
        out.println(sb.toString());
        out.close();
    }

    protected ForwardReader getForwardReader() throws IOException {
        return new ForwardReader();
    }

    protected BackwardReader getBackwardReader() throws IOException {
        return new BackwardReader();
    }

    /**
     * Recover the in-memory storage from persistent storage.
     */
    protected abstract void recover() throws IOException,
            InvalidDataFormattingException;

    private List<String> breakLine(String line)
            throws InvalidDataFormattingException {
        List<String> res = new ArrayList<String>(numCols);
        String[] splits = line.split(String.valueOf(COL_DELIMITER));
        if (splits.length != numCols) {
            throw new InvalidDataFormattingException(
                    "Malformatted storage file! Expected #cols: " + numCols
                            + " Real #cols: " + splits.length);
        }
        for (String col : splits) {
            res.add(DataDecoder.decode(col));
        }
        return res;
    }

    /**
     * A reader that reads the persistent storage one entry at a time forward
     * (starts from the beginning).
     *
     */
    protected class ForwardReader implements Closeable {

        private BufferedReader reader;

        private ForwardReader() throws IOException {
            reader = new BufferedReader(new FileReader(storage));
        }

        /**
         * Reads in an entry. Data are decoded.
         * 
         * @return An entry.
         * @throws IOException
         * @throws InvalidDataFormattingException
         */
        public List<String> readEntry() throws IOException,
                InvalidDataFormattingException {
            String line = reader.readLine();
            return line == null ? null : breakLine(line);
        }

        /**
         * Call this method when done reading from ForwardReader
         */
        @Override
        public void close() throws IOException {
            reader.close();
        }

    }

    /**
     * A reader that reads the persistent storage one entry at a time backward
     * (starts from the end).
     *
     */
    protected class BackwardReader implements Closeable {

        private static final int BLOCK_SIZE = 4096;

        private RandomAccessFile raFile;
        private long totalByteLength;
        private long lastBlockReadPos;
        private int lastLineReadInBlockPos;
        private boolean isLastBlock;
        private byte[] leftOver;
        private byte[] data;
        private boolean firstRead;

        private BackwardReader() throws IOException {
            raFile = new RandomAccessFile(storage, "r");
            totalByteLength = raFile.length();
            lastBlockReadPos = totalByteLength;
            isLastBlock = false;
            firstRead = true;
            readNewBlock(null);
        }

        /**
         * Read in a new block, copy left-over from previous block to the new
         * block as well.
         * 
         * @param leftOver
         * @throws IOException
         */
        private void readNewBlock(byte[] leftOver) throws IOException {
            int leftOverLength = leftOver == null ? 0 : leftOver.length;
            int readLength;
            if (lastBlockReadPos > BLOCK_SIZE) {
                readLength = BLOCK_SIZE;
            } else {
                readLength = (int) lastBlockReadPos;
                isLastBlock = true;
            }
            int newBlockLength = leftOverLength + readLength;
            data = new byte[newBlockLength];
            if (readLength > 0) {
                raFile.seek(lastBlockReadPos - readLength);
                int countRead = raFile.read(data, 0, readLength);
                if (countRead != readLength) {
                    throw new IllegalStateException(
                            "Count of requested bytes and actually read bytes don't match");
                }
            }
            if (leftOverLength > 0) {
                System.arraycopy(leftOver, 0, data, readLength, leftOverLength);
            }
            lastBlockReadPos = lastBlockReadPos - readLength;
            lastLineReadInBlockPos = newBlockLength;
        }

        private String readEntryStrInBlock() {
            int i = lastLineReadInBlockPos - 1;
            String line = null;
            while (i >= 0) {
                if (data[i] == (byte) ROW_DELIMITER) {
                    // found new line
                    int lineStart = i + 1;
                    int lineLength = Math.max(0, lastLineReadInBlockPos
                            - lineStart);
                    byte[] lineData = new byte[lineLength];
                    System.arraycopy(data, lineStart, lineData, 0, lineLength);
                    line = new String(lineData);
                    lastLineReadInBlockPos = i;
                    break;
                }
                i--;
            }
            if (i < 0) {
                int leftLength = lastLineReadInBlockPos;
                byte[] leftOver = new byte[leftLength];
                System.arraycopy(data, 0, leftOver, 0, leftLength);
                if (isLastBlock) {
                    // if it's the last block and we reached the end of the
                    // file, return null
                    if (lastLineReadInBlockPos == 0) {
                        return null;
                    }
                    // otherwise, return what's left
                    line = new String(leftOver);
                    lastLineReadInBlockPos = 0;
                }
            }
            return line;
        }

        /**
         * Reads in an entry. Data are decoded.
         * 
         * @return An entry
         * @throws IOException
         * @throws InvalidDataFormattingException
         */
        public List<String> readEntry() throws InvalidDataFormattingException,
                IOException {
            String entryStr = readEntryStrInBlock();
            if (firstRead) {
                // the first entryStr will always be empty since the last
                // character is always \n
                entryStr = readEntryStrInBlock();
                firstRead = false;
            }
            while (entryStr == null) {
                if (!isLastBlock) {
                    // keep reading in new blocks until we find a new line
                    // character
                    readNewBlock(leftOver);
                    entryStr = readEntryStrInBlock();
                } else {
                    // if it's the last block, and entry is null, it means we
                    // are hitting the end of the file, return null
                    break;
                }
            }
            return entryStr == null ? null : breakLine(entryStr);
        }

        /**
         * Call this method when done reading from BackwardReader
         */
        @Override
        public void close() throws IOException {
            raFile.close();
        }

    }

}
