// A release toggle: the farewell code ships in every build, switched off until it's finished.
class Flags(private val on: Set<String>) {
    fun isOn(name: String) = name in on
}

fun greet(name: String, flags: Flags): String {
    val hello = "Hello, $name"
    return if (flags.isOn("farewell")) "$hello. Bye for now!" else hello
}

fun main() {
    println(greet("Ada", Flags(emptySet())))          // what users get today
    println(greet("Ada", Flags(setOf("farewell"))))   // what tests and the team see
}
