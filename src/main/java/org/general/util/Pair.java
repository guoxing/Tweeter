package org.general.util;

/**
 * Generic utility pair class that can be used to associate 2 objects with each other.
 * @author marcelpuyat
 *
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2> {
    private T1 first;
    private T2 second;
    
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return this.first;
    }
    
    public T2 getSecond() {
        return this.second;
    }
}
