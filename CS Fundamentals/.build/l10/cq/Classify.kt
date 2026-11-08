fun classify(s: String): String =
    try {
        val n = s.toInt()
        require(n >= 0) { "negative: $n" }
        "ok"
    } catch (e: IllegalArgumentException) {
        "bad"
    } catch (e: NumberFormatException) {
        "nan"
    }

fun main() {
    println("${classify("12")} ${classify("abc")} ${classify("-3")}")
}
