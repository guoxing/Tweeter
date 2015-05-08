package org.general.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A fruit entry used for DataStorage tests.
 *
 * @author Guoxing Li
 *
 */
public class TestFruitEntry extends DataEntry {

    private final static int MAX_NAME_LENGTH = 10;

    long id;
    String name;
    double weight;

    public final static int ENTRY_SIZE = Long.BYTES + MAX_NAME_LENGTH
            + Double.BYTES;

    @Override
    public void unmarshal(ByteBuffer in) {
        checkValid(in, ENTRY_SIZE);
        id = in.getLong();
        name = readString(in, MAX_NAME_LENGTH);
        weight = in.getDouble();
    }

    @Override
    public ByteBuffer marshal() {
        ByteBuffer out = ByteBuffer.allocate(ENTRY_SIZE);
        out.putLong(id);
        out.put(Arrays.copyOf(name.getBytes(), MAX_NAME_LENGTH));
        out.putDouble(weight);
        return out;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestFruitEntry)) {
            return false;
        }
        TestFruitEntry entry = (TestFruitEntry) obj;
        return id == entry.id && name.equals(entry.name)
                && weight == entry.weight;

    }

    @Override
    public String toString() {
        return id + " " + name + " " + weight;
    }
}
