package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class LruCacheTest {
    @Test void aMissingKeyGivesNull() {
        assertNull(new LruCache<String, Integer>(2).get("x"));
    }

    @Test void evictsTheLeastRecentlyUsedEntry() {
        LruCache<String, Integer> c = new LruCache<>(2);
        c.put("a", 1);
        c.put("b", 2);
        c.put("c", 3);
        assertNull(c.get("a"));
        assertEquals(List.of("b", "c"), c.keys());
    }

    @Test void getCountsAsAUse() {
        LruCache<String, Integer> c = new LruCache<>(2);
        c.put("a", 1);
        c.put("b", 2);
        c.get("a");
        c.put("c", 3);
        assertEquals(List.of("a", "c"), c.keys());
    }

    @Test void capacityMustBePositive() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new LruCache<String, Integer>(0));
        assertEquals("capacity must be positive: 0", e.getMessage());
    }
}
