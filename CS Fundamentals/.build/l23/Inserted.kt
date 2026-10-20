class Config {
    companion object {
        const val MAX = 3
        fun create(): Config = Config()
    }
}

fun greet(name: String): String = "Hi, " + name

fun dayName(d: Int): String = when (d) {
    1 -> "Mon"
    2 -> "Tue"
    3 -> "Wed"
    4 -> "Thu"
    else -> "?"
}

fun main() {
    println(greet("Ada") + " " + dayName(3) + " " + Config.create().javaClass.simpleName)
}
