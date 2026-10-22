package shop

interface Notifier {
    fun send(to: String, message: String)
}

class EmailNotifier(private val from: String) : Notifier {
    override fun send(to: String, message: String) = println("  [email $from] to $to: $message")
}

// A simulated SMS gateway that times out for its first `failures` calls
class FlakySmsGateway(private var failures: Int) : Notifier {
    override fun send(to: String, message: String) {
        if (failures-- > 0) throw IllegalStateException("gateway timeout")
        println("  [sms] to $to: $message")
    }
}

// FACTORY: config string in, the right Notifier out; callers never name a concrete class
fun notifierFrom(config: String): Notifier {
    val parts = config.split(":", limit = 2)
    return when (parts[0]) {
        "email" -> EmailNotifier(parts.getOrElse(1) { "noreply@shop.test" })
        "sms" -> FlakySmsGateway(failures = parts.getOrNull(1)?.toInt() ?: 0)
        else -> throw IllegalArgumentException("unknown notifier: $config")
    }
}

// DECORATOR: same interface, wraps another Notifier, adds retries
class RetryingNotifier(private val inner: Notifier, private val attempts: Int) : Notifier {
    init { require(attempts >= 1) }
    override fun send(to: String, message: String) {
        for (attempt in 1..attempts) {
            try {
                return inner.send(to, message)
            } catch (e: IllegalStateException) {
                if (attempt == attempts) throw e
                println("  retry ${attempt + 1}/$attempts after: ${e.message}")
            }
        }
    }
}

// DECORATOR: records every send; stacks with RetryingNotifier in either order
class LoggingNotifier(private val inner: Notifier, private val log: MutableList<String>) : Notifier {
    override fun send(to: String, message: String) {
        log += "send to $to"
        inner.send(to, message)
    }
}
