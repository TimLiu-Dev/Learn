import java.math.BigDecimal

class Money private constructor(private val cents: Long) {
    companion object {                                       // the factories live in the companion
        private val ZERO = Money(0)
        fun ofCents(cents: Long) = if (cents == 0L) ZERO else Money(cents)
        fun parse(s: String) = ofCents(BigDecimal(s).movePointRight(2).longValueExact())
        operator fun invoke(dollars: Int) = ofCents(dollars * 100L)   // Money(5) LOOKS like a constructor call
    }
    override fun toString() = "$%d.%02d".format(cents / 100, cents % 100)
}

interface Notifier { fun send(to: String, msg: String) }
class EmailNotifier(private val from: String) : Notifier {
    override fun send(to: String, msg: String) = println("email $from -> $to: $msg")
}
class SmsNotifier : Notifier {
    override fun send(to: String, msg: String) = println("sms -> $to: $msg")
}

// A top-level factory function, like listOf(): no class needed to hold it
fun notifierFrom(config: String): Notifier {
    val parts = config.split(":", limit = 2)
    return when (parts[0]) {
        "email" -> EmailNotifier(parts.getOrElse(1) { "noreply@library.test" })
        "sms" -> SmsNotifier()
        else -> throw IllegalArgumentException("unknown notifier: $config")
    }
}

// A factory that returns a result instead of throwing (L7, L10)
sealed interface IsbnResult {
    data class Ok(val isbn: String) : IsbnResult
    data class Bad(val reason: String) : IsbnResult
}
fun isbnOf(raw: String): IsbnResult {
    val digits = raw.filter(Char::isDigit)
    return if (digits.length == 13) IsbnResult.Ok(digits) else IsbnResult.Bad("need 13 digits, got ${digits.length}")
}

fun main() {
    println("${Money.parse("12.50")} ${Money.ofCents(99)} ${Money(5)}")
    println("zero reused: ${Money.ofCents(0) === Money.ofCents(0)}")
    for (cfg in listOf("email:desk@library.test", "sms")) notifierFrom(cfg).send("ada", "Dune is due")
    runCatching { notifierFrom("pigeon") }.onFailure { println(it.message) }
    println(isbnOf("978-0-441-17271-9"))
    println(isbnOf("123"))
}
