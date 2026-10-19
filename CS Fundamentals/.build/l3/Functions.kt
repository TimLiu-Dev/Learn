fun greet(name: String, greeting: String = "Hello", punctuation: String = "!"): String =
    "$greeting, $name$punctuation"

fun average(vararg xs: Int): Double {
    if (xs.isEmpty()) return 0.0
    var sum = 0
    for (x in xs) sum += x
    return sum.toDouble() / xs.size
}

fun logLine(message: String) {          // returns Unit (Kotlin's "nothing useful")
    println("[log] $message")
}

fun main() {
    println(greet("Ada"))
    println(greet("Ada", "Hi"))
    println(greet("Ada", punctuation = "?"))             // skip the middle one
    println(greet(greeting = "Welcome", name = "Linus")) // any order when named
    println(average(3, 4, 5))
    println(average())
    val nums = intArrayOf(1, 2)
    println(average(*nums))                              // spread an array into varargs
    logLine("done")
}
