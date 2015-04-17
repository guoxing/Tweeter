package org.general.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.general.application.InternalError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for AppData, including DataDecoder/DataEncoder
 *
 * @author Guoxing Li
 *
 */
public class AppDataTests extends AppData {

    private static final int NUM_COLS_IN_ENTRY = 2;
    private static final String FILE_NAME = "test.db";

    private static final String COL_1 = "id\t1";
    private static final String COL_2 = "tweet\\tweet\n\\";
    private static final String COL_3 = "id\n";
    private static final String COL_4 = "tweet\\\\tweet";
    private List<String> testEntry1;
    private List<String> testEntry2;

    public AppDataTests() throws InternalError {
        super(FILE_NAME, NUM_COLS_IN_ENTRY);
    }

    /**
     * Dummy recover method.
     */
    @Override
    protected void recover() throws InternalError {
    }

    private void clearFile() {
        try {
            Files.delete(Paths.get(FILE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() {
        testEntry1 = new ArrayList<String>();
        testEntry1.add(COL_1);
        testEntry1.add(COL_2);
        testEntry2 = new ArrayList<String>();
        testEntry2.add(COL_3);
        testEntry2.add(COL_4);
        try {
            appendToFile(testEntry1);
            appendToFile(testEntry2);
        } catch (InternalError e) {
            e.printStackTrace();
            fail("Write Exception");
        }
    }

    @After
    public void teardown() {
        clearFile();
    }

    @Test
    public void testForwardReader() {
        try {
            ForwardReader fr = getForwardReader();
            List<String> entry = fr.readEntry();
            assertEquals(entry, testEntry1);
        } catch (IOException | InternalError e) {
            e.printStackTrace();
            fail("Forward Reader Exception");
        }
    }

    @Test
    public void testBackwardReader() {
        try {
            BackwardReader br = getBackwardReader();
            List<String> entry = br.readEntry();
            assertEquals(entry, testEntry2);
        } catch (IOException | InternalError e) {
            e.printStackTrace();
            fail("Backward Reader Exception");
        }
    }
}
