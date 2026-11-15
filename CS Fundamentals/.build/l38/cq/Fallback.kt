sealed interface Channel {
    data object Email : Channel
    data class Sms(val maxChars: Int) : Channel
    data class Slack(val room: String) : Channel      // added months later
}

fun costCents(c: Channel): Int = when (c) {
    is Channel.Sms -> 5
    else -> 0                                          // written when Email was the only other case
}

fun main() = println(costCents(Channel.Slack("library")))
