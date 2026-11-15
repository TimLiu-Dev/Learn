data class Message(val id: Long, val body: String, val attempt: Int)

class EmailWorker(private val sendEmail: (String) -> Unit) {
    private val done = mutableSetOf<String>()
    var skipped = 0
        private set

    // De-duplicates by text: two different jobs with the same text become one.
    fun handle(m: Message) {
        if (m.body in done) { skipped++; return }
        sendEmail(m.body)
        done += m.body
    }
}
