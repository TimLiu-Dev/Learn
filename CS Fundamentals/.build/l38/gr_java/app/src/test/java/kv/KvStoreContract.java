package kv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// One set of tests, written against the interface. Every implementation must pass all of them.
abstract class KvStoreContract {
    abstract KvStore newStore() throws Exception;
    void cleanUp() throws Exception {}

    private KvStore store;

    @BeforeEach void setUp() throws Exception { store = newStore(); }

    @AfterEach void tearDown() throws Exception {
        if (store instanceof AutoCloseable c) c.close();
        cleanUp();
    }

    @Test void aMissingKeyGivesNull() { assertNull(store.get("nope")); }

    @Test void putThenGetReturnsTheValue() {
        store.put("dune", "Herbert");
        assertEquals("Herbert", store.get("dune"));
    }

    @Test void putOnAnExistingKeyReplacesTheValue() {
        store.put("dune", "Herbert");
        store.put("dune", "F. Herbert");
        assertEquals("F. Herbert", store.get("dune"));
    }

    @Test void deleteReportsWhetherSomethingWasRemoved() {
        store.put("dune", "Herbert");
        assertTrue(store.delete("dune"));
        assertFalse(store.delete("dune"));
    }

    @Test void keysComeBackSorted() {
        for (String k : List.of("mort", "dune", "emma")) store.put(k, "x");
        assertEquals(List.of("dune", "emma", "mort"), store.keys());
    }
}
