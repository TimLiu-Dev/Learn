// T & Any means "T, but definitely not null", even when T itself is a nullable type.
fun <T> orDefault(x: T, fallback: T & Any): T & Any = x ?: fallback

fun main() {
    val nickname: String? = null
    val shown: String = orDefault(nickname, "anonymous")   // T = String?, so T & Any = String
    println(shown.length)                                  // no ?. needed
    println(orDefault("Ada", "anonymous"))
}
