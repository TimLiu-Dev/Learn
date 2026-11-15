data class Message(val id: Long, val body: String, val attempt: Int)

class EmailWorker(private val sendEmail: (String) -> Unit) {
    private val done = mutableSetOf<Long>()
    var skipped = 0
        private set

    // Records the id first: a send that throws is then never retried.
    fun handle(m: Message) {
        if (!done.add(m.id)) { skipped++; return }
        sendEmail(m.body)
    }
}
