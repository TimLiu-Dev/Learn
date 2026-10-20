package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MyHashMapTest {
    @Test void aNewMapIsEmpty() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        assertEquals(0, m.size());
        assertNull(m.get("anything"));
    }

    @Test void getReturnsWhatPutStored() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("cat", 3);
        assertEquals(3, m.get("cat"));
        assertEquals(1, m.size());
    }

    @Test void putOnAnExistingKeyReplacesTheValueAndReturnsTheOldOne() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        assertNull(m.put("cat", 1));
        assertEquals(1, m.put("cat", 2));
        assertEquals(2, m.get("cat"));
        assertEquals(1, m.size());
    }

    @Test void containsKeyTellsPresentFromAbsent() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("cat", 3);
        assertTrue(m.containsKey("cat"));
        assertFalse(m.containsKey("dog"));
    }

    @Test void collidingKeysAreBothKept() {
        assertEquals("Aa".hashCode(), "BB".hashCode());     // a real collision
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("Aa", 1);
        m.put("BB", 2);
        assertEquals(1, m.get("Aa"));
        assertEquals(2, m.get("BB"));
    }

    @Test void removeReturnsTheValueAndForgetsTheKey() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("cat", 3);
        assertEquals(3, m.remove("cat"));
        assertNull(m.get("cat"));
        assertEquals(0, m.size());
        assertNull(m.remove("cat"));
    }

    @Test void removeFromTheMiddleOfAChainKeepsTheNeighbours() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        List<String> keys = List.of("AaAa", "AaBB", "BBAa");     // all share one hash
        for (int i = 0; i < keys.size(); i++) m.put(keys.get(i), i);
        assertEquals(1, m.remove("AaBB"));
        assertEquals(0, m.get("AaAa"));
        assertEquals(2, m.get("BBAa"));
        assertEquals(2, m.size());
    }

    @Test void growsPastTheLoadFactorAndKeepsEveryEntry() {
        MyHashMap<Integer, String> m = new MyHashMap<>();
        int start = m.capacity();
        for (int i = 0; i < 1000; i++) m.put(i, "v" + i);
        assertEquals(1000, m.size());
        assertTrue(m.capacity() > start);
        assertTrue(m.size() <= m.capacity() * 0.75);
        for (int i = 0; i < 1000; i++) assertEquals("v" + i, m.get(i));
    }

    @Test void keysListsEveryKeyOnce() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        for (String k : List.of("a", "b", "c", "a")) m.put(k, 0);
        assertEquals(Set.of("a", "b", "c"), new HashSet<>(m.keys()));
        assertEquals(3, m.keys().size());
    }

    @Test void nullKeysAreRejected() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        assertThrows(NullPointerException.class, () -> m.put(null, 1));
    }
}
