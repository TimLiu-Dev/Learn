interface Notifier {
    fun send(to: String, message: String)
}

// Shared state (a sent counter) and shared steps justify an abstract base
abstract class ChannelNotifier(private val channel: String) : Notifier {
    var sent = 0
        private set

    final override fun send(to: String, message: String) {     // fixed steps; subclasses can't skip the count
        sent++
        println("[$channel #$sent] to $to: ${format(message)}")
    }

    protected open fun format(message: String): String = message  // the one step subclasses may change
}

class EmailNotifier : ChannelNotifier("email")

class SmsNotifier : ChannelNotifier("sms") {
    override fun format(message: String) =
        if (message.length <= 20) message else message.take(17) + "..."
}

class PushNotifier : ChannelNotifier("push") {
    override fun format(message: String) = "(!) $message"
}

// Composition: wrap ANY notifier to add behaviour, no subclass per channel
class LoggingNotifier(private val inner: Notifier, private val log: MutableList<String>) : Notifier {
    override fun send(to: String, message: String) {
        log += "sending to $to"
        inner.send(to, message)
    }
}

class UrgentNotifier(private val inner: Notifier) : Notifier {
    override fun send(to: String, message: String) = inner.send(to, "URGENT: $message")
}

fun main() {
    val log = mutableListOf<String>()
    val email = EmailNotifier()
    val channels: List<Notifier> = listOf(
        email,
        SmsNotifier(),
        LoggingNotifier(PushNotifier(), log),
        UrgentNotifier(SmsNotifier()),
        UrgentNotifier(LoggingNotifier(email, log)),
    )
    for (n in channels) n.send("ada", "Your build finished successfully")
    println(log)
    println("emails sent: ${email.sent}")
}
