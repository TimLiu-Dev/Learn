package paste

import java.nio.file.Files
import java.time.Duration
import java.time.Instant

fun main() {
    val db = Files.createTempFile("paste", ".db")
    val clock = SettableClock(Instant.parse("2026-10-26T12:00:00Z"))
    val store = PasteStore(db.toString())
    val pastes = PasteService(store, clock)

    val note = pastes.create("ada", "fun main() = println(\"hi\")", ttl = null) as Created.Ok
    val temp = pastes.create("ada", "one-time password: 4242", ttl = Duration.ofMinutes(10)) as Created.Ok
    println("created ${note.id} (forever) and ${temp.id} (10 min)")
    println("too big: " + pastes.create("ada", "x".repeat(64 * 1024 + 1), ttl = null))

    println("read note: " + pastes.read(note.id))
    val before = store.queries.get()
    repeat(3) { pastes.read(note.id) }
    println("3 more reads cost ${store.queries.get() - before} queries (cache)")
    println("read unknown: " + pastes.read("nope0000"))

    clock.now = clock.now.plus(Duration.ofMinutes(11))
    println("11 min later, temp: " + pastes.read(temp.id))
    println("purged ${pastes.purgeExpired()} expired paste(s)")

    val burst = List(6) { pastes.create("bot", "spam $it", ttl = null) }
    println("bot sends 6 at once: " + burst.map { it::class.simpleName })
    println("  the 6th: " + burst.last())
    clock.now = clock.now.plusSeconds(12)
    println("12 s later: " + pastes.create("bot", "spam", ttl = null)::class.simpleName)
    Files.deleteIfExists(db)
}
