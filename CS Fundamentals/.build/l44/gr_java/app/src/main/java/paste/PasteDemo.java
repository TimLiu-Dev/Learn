package paste;

import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import paste.PasteService.Created;
import paste.PasteService.Ok;

public class PasteDemo {
    public static void main(String[] args) throws Exception {
        var db = Files.createTempFile("paste", ".db");
        var clock = new SettableClock(Instant.parse("2026-10-26T12:00:00Z"));
        var store = new PasteStore(db.toString());
        var pastes = new PasteService(store, clock);

        var note = (Ok) pastes.create("ada", "fun main() = println(\"hi\")", null);
        var temp = (Ok) pastes.create("ada", "one-time password: 4242", Duration.ofMinutes(10));
        System.out.println("created " + note.id() + " (forever) and " + temp.id() + " (10 min)");
        System.out.println("too big: " + pastes.create("ada", "x".repeat(64 * 1024 + 1), null));

        System.out.println("read note: " + pastes.read(note.id()));
        long before = store.queries.get();
        for (int i = 0; i < 3; i++) pastes.read(note.id());
        System.out.println("3 more reads cost " + (store.queries.get() - before) + " queries (cache)");
        System.out.println("read unknown: " + pastes.read("nope0000"));

        clock.now = clock.now.plus(Duration.ofMinutes(11));
        System.out.println("11 min later, temp: " + pastes.read(temp.id()));
        System.out.println("purged " + pastes.purgeExpired() + " expired paste(s)");

        var burst = new ArrayList<Created>();
        for (int i = 0; i < 6; i++) burst.add(pastes.create("bot", "spam " + i, null));
        System.out.println("bot sends 6 at once: " + burst.stream().map(c -> c.getClass().getSimpleName()).toList());
        System.out.println("  the 6th: " + burst.getLast());
        clock.now = clock.now.plusSeconds(12);
        System.out.println("12 s later: " + pastes.create("bot", "spam", null).getClass().getSimpleName());
        Files.deleteIfExists(db);
    }
}
