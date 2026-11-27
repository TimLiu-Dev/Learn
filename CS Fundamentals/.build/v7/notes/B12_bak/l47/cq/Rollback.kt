import java.io.IOException
import java.io.UncheckedIOException
import java.sql.SQLException

// Spring's default rule for @Transactional, as TxProxy copies it
fun rollsBack(t: Throwable) = t is RuntimeException || t is Error

fun main() {
    val failures = listOf(
        UncheckedIOException(IOException("disk full")),
        SQLException("database is locked"),
        AssertionError("bad state"),
        Exception("plain"),
    )
    println(failures.joinToString(" ") { if (rollsBack(it)) "R" else "C" })
}
