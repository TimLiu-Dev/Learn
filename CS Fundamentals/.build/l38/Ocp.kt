// OCP two ways. L8's notifier is open for new CHANNELS; a sealed type is open for new OPERATIONS.

// ---------- Interface: add channels without touching existing code ----------
fun interface Notifier {
    fun send(to: String, message: String)
}

class Broadcaster(private val channels: List<Notifier>) {        // never edited again
    fun announce(to: String, message: String) = channels.forEach { it.send(to, message) }
}

class EmailNotifier : Notifier {
    override fun send(to: String, message: String) = println("  email to $to: $message")
}

class SmsNotifier : Notifier {
    override fun send(to: String, message: String) = println("  sms to $to: ${message.take(17)}...")
}

// Added months later: a new file, zero edits to Broadcaster, EmailNotifier or SmsNotifier.
class SlackNotifier(private val room: String) : Notifier {
    override fun send(to: String, message: String) = println("  slack #$room: @$to $message")
}

// ---------- Sealed type: add operations without touching existing code ----------
sealed interface Channel {
    data object Email : Channel
    data class Sms(val maxChars: Int) : Channel
}

fun costCents(c: Channel): Int = when (c) {         // no `else`: the compiler checks every case
    Channel.Email -> 0
    is Channel.Sms -> 5
}

// Added months later: a new operation is one new function. No class was edited.
fun describe(c: Channel): String = when (c) {
    Channel.Email -> "email"
    is Channel.Sms -> "sms (max ${c.maxChars} chars)"
}

fun main() {
    println("interface, three channels:")
    Broadcaster(listOf(EmailNotifier(), SmsNotifier(), SlackNotifier("library")))
        .announce("ada", "Your reserved book is ready")
    println("sealed, two operations:")
    for (c in listOf(Channel.Email, Channel.Sms(160))) println("  ${describe(c)} costs ${costCents(c)} cents")
}
