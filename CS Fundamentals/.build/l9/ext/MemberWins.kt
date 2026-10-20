class Greeter {
    fun greet() = "member"
}

fun Greeter.greet() = "extension"                       // same signature: never called
fun Greeter.greet(name: String) = "extension for $name" // different signature: fine

fun main() {
    println(Greeter().greet())
    println(Greeter().greet("Ada"))
}
