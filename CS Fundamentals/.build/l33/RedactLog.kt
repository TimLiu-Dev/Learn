import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.logging.StreamHandler

// Masks email addresses in every log message, after the parameters are filled in.
class RedactingFormatter : Formatter() {
    private val email = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}""")

    override fun format(r: LogRecord): String {
        val message = formatMessage(r)          // "{0}" placeholders are replaced here
        return "${r.level} ${r.loggerName}: ${email.replace(message, "[email]")}\n"
    }
}

fun main() {
    val handler = StreamHandler(System.out, RedactingFormatter())
    val log = Logger.getLogger("signup").apply {
        useParentHandlers = false               // no default console output
        addHandler(handler)
    }
    log.info("new user ana.lima@example.com from the web form")
    log.log(Level.WARNING, "reset for {0} failed: {1}", arrayOf("ben@example.org", "link expired"))
    log.info("password reset for user 42")      // better still: log an id, not the address
    handler.flush()
}
