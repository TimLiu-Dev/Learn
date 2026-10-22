data class Email(
    val to: String,                          // required: no default
    val subject: String,
    val body: String = "",
    val cc: List<String> = emptyList(),
    val html: Boolean = false,
) {
    init {
        check("@" in to) { "bad address: $to" }
        check(subject.isNotBlank()) { "subject is required" }
    }

    companion object {
        // A named factory still earns its place: it encodes a common recipe
        fun reminder(to: String, title: String, daysLate: Int) =
            Email(to, subject = "Overdue: $title", body = "$title is $daysLate days late.")
    }
}

fun main() {
    println(Email("ada@uni.test", subject = "Hello", cc = listOf("desk@library.test", "boss@library.test")))
    println(Email.reminder("ada@uni.test", "Dune", 3))
    runCatching { Email("ada", subject = "Hi") }.onFailure { println("rejected: ${it.message}") }
}
