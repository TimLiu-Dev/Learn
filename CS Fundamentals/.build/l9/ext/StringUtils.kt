// Extension function: callable as "ada lovelace".initials()
fun String.initials(): String {
    var result = ""
    for (word in split(" ")) {
        if (word.isNotEmpty()) result += word[0].uppercaseChar()
    }
    return result
}

// Extension property: computed every time, no backing field allowed
val String.lastChar: Char
    get() = this[length - 1]

// Nullable receiver: safe to call even on null
fun String?.orDash(): String = if (this == null || isBlank()) "-" else this
