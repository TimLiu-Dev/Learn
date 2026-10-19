fun main() {
    val name = "Ada"
    val year = 1815
    println("$name was born in $year.")
    println("Ten years later: ${year + 10}")
    println("${name.length} letters, upper: ${name.uppercase()}")
    println("%.2f".format(3.14159))

    val poem = """
        Roses are red,
          violets are blue.
    """.trimIndent()
    println(poem)

    println('a' + 1)          // Char + Int -> Char
    println('a'.code)         // the Char's numeric code
    val smile = "😀"  // one emoji, written as two UTF-16 units
    println(smile.length)
    println(smile.codePointCount(0, smile.length))

    println("  hi  ".trim())
    println("a,b,,c".split(","))
    println("ha".repeat(3))
    println("kotlin".replace("k", "K"))
    println("Kotlin".startsWith("Kot"))
    println("Kotlin".substring(1, 4))

    val a = "hi"
    val b = StringBuilder("h").append("i").toString()   // same text, different object
    println(a == b)           // Kotlin == compares contents
}
