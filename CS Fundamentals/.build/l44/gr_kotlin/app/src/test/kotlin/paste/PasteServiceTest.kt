package paste

import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PasteServiceTest {
    private val db = Files.createTempFile("paste", ".db")
    private val clock = SettableClock(Instant.parse("2026-10-26T12:00:00Z"))
    private val store = PasteStore(db.toString())
    private val pastes = PasteService(store, clock)

    @AfterTest fun cleanUp() { Files.deleteIfExists(db) }

    private fun create(content: String, ttl: Duration? = null, client: String = "ada") =
        assertIs<Created.Ok>(pastes.create(client, content, ttl)).id

    @Test fun `a paste can be read back by its id`() {
        val id = create("hello")
        assertTrue(Regex("[0-9a-zA-Z]{8}").matches(id))
        assertEquals(Read.Found("hello"), pastes.read(id))
    }

    @Test fun `exactly 64 KiB is allowed, one more byte is not`() {
        create("x".repeat(64 * 1024))
        assertEquals(Created.TooLarge(65536), pastes.create("ada", "x".repeat(64 * 1024 + 1), null))
    }

    @Test fun `size is measured in UTF-8 bytes, not characters`() {
        val emoji = "😀".repeat(16 * 1024 + 1)                   // 4 bytes each: 65,540 bytes, 32,770 chars
        assertIs<Created.TooLarge>(pastes.create("ada", emoji, null))
    }

    @Test fun `a paste expires after its ttl`() {
        val id = create("temp", ttl = Duration.ofMinutes(10))
        clock.now = clock.now.plus(Duration.ofMinutes(9))
        assertIs<Read.Found>(pastes.read(id))
        clock.now = clock.now.plus(Duration.ofMinutes(2))
        assertEquals(Read.Expired, pastes.read(id))
    }

    @Test fun `unknown ids are not found`() {
        assertEquals(Read.NotFound, pastes.read("nope0000"))
    }

    @Test fun `a burst of 5 is allowed, the 6th waits, time refills the bucket`() {
        repeat(5) { create("p$it", client = "bot") }
        val limited = assertIs<Created.RateLimited>(pastes.create("bot", "p6", null))
        assertTrue(limited.retryAfterSeconds > 0)
        assertIs<Created.Ok>(pastes.create("other", "fine", null))   // buckets are per client
        clock.now = clock.now.plusSeconds(12)
        assertIs<Created.Ok>(pastes.create("bot", "p7", null))
    }

    @Test fun `repeated reads come from the cache`() {
        val id = create("hot")
        pastes.read(id)
        val before = store.queries.get()
        repeat(10) { pastes.read(id) }
        assertEquals(before, store.queries.get())
    }

    @Test fun `purge deletes only expired pastes`() {
        create("forever")
        create("short", ttl = Duration.ofMinutes(1))
        create("long", ttl = Duration.ofDays(1))
        clock.now = clock.now.plus(Duration.ofHours(1))
        assertEquals(1, pastes.purgeExpired())
    }
}
