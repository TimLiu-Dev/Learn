data class Message(val id: Long, val body: String, val attempt: Int)

// The queue delivers at least once, so the same message can arrive several times.
class EmailWorker(private val sendEmail: (String) -> Unit) {
    private val done = mutableSetOf<Long>()     // ids whose email has been sent
    var skipped = 0                             // deliveries ignored as duplicates
        private set

    // Called for every delivery. If it throws, the message isn't acked and comes back later.
    fun handle(m: Message) {
        if (m.id in done) { skipped++; return }
        sendEmail(m.body)       // if this throws, the id stays unrecorded, so the retry sends it
        done += m.id            // record only after the effect has happened
    }
}
