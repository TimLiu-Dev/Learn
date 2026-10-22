// The sealed side of the trade-off: add a new CASE and every `when` must be edited.
sealed interface Channel {
    data object Email : Channel
    data class Sms(val maxChars: Int) : Channel
    data class Slack(val room: String) : Channel      // the new case
}

fun costCents(c: Channel): Int = when (c) {
    Channel.Email -> 0
    is Channel.Sms -> 5
}

fun main() = println(costCents(Channel.Slack("library")))
