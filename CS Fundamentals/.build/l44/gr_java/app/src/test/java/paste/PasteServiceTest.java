package paste;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paste.PasteService.*;

class PasteServiceTest {
    Path db;
    SettableClock clock = new SettableClock(Instant.parse("2026-10-26T12:00:00Z"));
    PasteStore store;
    PasteService pastes;

    @BeforeEach void setUp() throws Exception {
        db = Files.createTempFile("paste", ".db");
        store = new PasteStore(db.toString());
        pastes = new PasteService(store, clock);
    }

    @AfterEach void cleanUp() throws Exception { Files.deleteIfExists(db); }

    String create(String content, Duration ttl, String client) throws Exception {
        return assertInstanceOf(Ok.class, pastes.create(client, content, ttl)).id();
    }

    @Test void aPasteCanBeReadBackByItsId() throws Exception {
        String id = create("hello", null, "ada");
        assertTrue(id.matches("[0-9a-zA-Z]{8}"));
        assertEquals(new Found("hello"), pastes.read(id));
    }

    @Test void exactly64KiBIsAllowedOneMoreByteIsNot() throws Exception {
        create("x".repeat(64 * 1024), null, "ada");
        assertEquals(new TooLarge(65536), pastes.create("ada", "x".repeat(64 * 1024 + 1), null));
    }

    @Test void sizeIsMeasuredInUtf8BytesNotCharacters() throws Exception {
        String emoji = "😀".repeat(16 * 1024 + 1);                // 4 bytes each: 65,540 bytes, 32,770 chars
        assertInstanceOf(TooLarge.class, pastes.create("ada", emoji, null));
    }

    @Test void aPasteExpiresAfterItsTtl() throws Exception {
        String id = create("temp", Duration.ofMinutes(10), "ada");
        clock.now = clock.now.plus(Duration.ofMinutes(9));
        assertInstanceOf(Found.class, pastes.read(id));
        clock.now = clock.now.plus(Duration.ofMinutes(2));
        assertEquals(new Expired(), pastes.read(id));
    }

    @Test void unknownIdsAreNotFound() throws Exception {
        assertEquals(new NotFound(), pastes.read("nope0000"));
    }

    @Test void aBurstOf5IsAllowedThe6thWaitsTimeRefillsTheBucket() throws Exception {
        for (int i = 0; i < 5; i++) create("p" + i, null, "bot");
        var limited = assertInstanceOf(RateLimited.class, pastes.create("bot", "p6", null));
        assertTrue(limited.retryAfterSeconds() > 0);
        assertInstanceOf(Ok.class, pastes.create("other", "fine", null));   // buckets are per client
        clock.now = clock.now.plusSeconds(12);
        assertInstanceOf(Ok.class, pastes.create("bot", "p7", null));
    }

    @Test void repeatedReadsComeFromTheCache() throws Exception {
        String id = create("hot", null, "ada");
        pastes.read(id);
        long before = store.queries.get();
        for (int i = 0; i < 10; i++) pastes.read(id);
        assertEquals(before, store.queries.get());
    }

    @Test void purgeDeletesOnlyExpiredPastes() throws Exception {
        create("forever", null, "ada");
        create("short", Duration.ofMinutes(1), "ada");
        create("long", Duration.ofDays(1), "ada");
        clock.now = clock.now.plus(Duration.ofHours(1));
        assertEquals(1, pastes.purgeExpired());
    }
}
