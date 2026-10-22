package kv

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// One set of tests, written against the interface. Every implementation must pass all of them.
abstract class KvStoreContract {
    abstract fun newStore(): KvStore
    open fun cleanUp() {}

    private lateinit var store: KvStore

    @BeforeTest fun setUp() { store = newStore() }
    @AfterTest fun tearDown() { (store as? AutoCloseable)?.close(); cleanUp() }

    @Test fun `a missing key gives null`() = assertNull(store.get("nope"))

    @Test fun `put then get returns the value`() {
        store.put("dune", "Herbert")
        assertEquals("Herbert", store.get("dune"))
    }

    @Test fun `put on an existing key replaces the value`() {
        store.put("dune", "Herbert")
        store.put("dune", "F. Herbert")
        assertEquals("F. Herbert", store.get("dune"))
    }

    @Test fun `delete reports whether something was removed`() {
        store.put("dune", "Herbert")
        assertTrue(store.delete("dune"))
        assertFalse(store.delete("dune"))
    }

    @Test fun `keys come back sorted`() {
        listOf("mort", "dune", "emma").forEach { store.put(it, "x") }
        assertEquals(listOf("dune", "emma", "mort"), store.keys())
    }
}

class MemoryKvStoreTest : KvStoreContract() {
    override fun newStore() = MemoryKvStore()
}

class SqliteKvStoreTest : KvStoreContract() {
    private val file: Path = Files.createTempFile("kv", ".db")
    override fun newStore() = SqliteKvStore("jdbc:sqlite:$file")
    override fun cleanUp() { Files.deleteIfExists(file) }
}
