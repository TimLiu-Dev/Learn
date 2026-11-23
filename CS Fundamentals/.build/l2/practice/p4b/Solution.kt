fun command(input: String): String? {
    // ^ anchors the match at the start, so "hello /mute" isn't a command
    val m = Regex("""^/(\w+)""").find(input.trim())
    if (m == null) return null
    return m.groupValues[1].lowercase()   // lowercase() ignores the default locale
}

fun seconds(token: String): Int? {
    // matchEntire: the WHOLE token must be digits plus one unit letter
    val m = Regex("""(\d+)([smh])""").matchEntire(token)
    if (m == null) return null
    val n = m.groupValues[1].toInt()
    val unit = m.groupValues[2]
    return if (unit == "s") n else if (unit == "m") n * 60 else n * 3600
}
