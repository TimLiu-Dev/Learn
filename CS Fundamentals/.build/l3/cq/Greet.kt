fun greet(name: String, greeting: String = "Hello", punctuation: String = "!") =
    "$greeting, $name$punctuation"

fun main() {
    println(greet("Ada", "?"))
}
