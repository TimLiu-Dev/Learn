fun command(input: String): String? {
    val m = Regex("""^/(\w+)""").find(input.trim())
    if (m == null) return null
    return m.groupValues[1].lowercase()
}

// find() is happy with a match anywhere inside the token, so "10min" passes as "10m"
fun seconds(token: String): Int? {
    val m = Regex("""(\d+)([smh])""").find(token)
    if (m == null) return null
    val n = m.groupValues[1].toInt()
    val unit = m.groupValues[2]
    return if (unit == "s") n else if (unit == "m") n * 60 else n * 3600
}
