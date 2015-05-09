package org.general.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DataStorage
 *
 * @author Guoxing Li
 *
 */
public class DataStorageTests {

    private static final String FILE_NAME = "test.db";

    private TestFruitEntry apple;
    private TestFruitEntry orange;
    private TestFruitEntry banana;
    private DataStorage<TestFruitEntry> storage;


    private void clearFile() {
        try {
            Files.delete(Paths.get(FILE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void addFruits() throws IOException {
        storage = new DataStorage<TestFruitEntry>(FILE_NAME,
                TestFruitEntry.class, TestFruitEntry.ENTRY_SIZE);
        apple = new TestFruitEntry();
        apple.id = 10L;
        apple.name = "Apple";
        apple.weight = 10.2;
        orange = new TestFruitEntry();
        orange.id = 100000L;
        orange.name = "Orange";
        orange.weight = 15.4;
        banana = new TestFruitEntry();
        banana.id = 100000000L;
        banana.name = "Banana";
        banana.weight = 6.33;
        storage.appendToFile(apple);
        storage.appendToFile(orange);
        storage.appendToFile(banana);
    }

    @After
    public void teardown() {
        clearFile();
    }

    @Test
    public void testForwardReads() throws IOException {
        DataStorage<TestFruitEntry>.EntryReader reader = storage.new EntryReader();
        TestFruitEntry entry = reader.readNext();
        assertEquals(entry, apple);
        reader.close();
    }

    @Test
    public void testBackwardReads() throws IOException {
        DataStorage<TestFruitEntry>.EntryReader reader = storage.new EntryReader(
                true);
        TestFruitEntry entry = reader.readPrevious();
        assertEquals(entry, banana);
        entry = reader.readNext();
        assertEquals(entry, banana);
        reader.close();
    }

    @Test
    public void testRandomReads() throws IOException {
        DataStorage<TestFruitEntry>.EntryReader reader = storage.new EntryReader();
        TestFruitEntry entry = reader.readAt(1);
        assertEquals(entry, orange);
        entry = reader.readNext();
        assertEquals(entry, banana);
        assertNull(reader.readNext());
        reader.close();
    }

}
