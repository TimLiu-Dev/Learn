fun main() {
    val names: Array<String> = arrayOf("Ada", "Linus")
    val things: Array<Any> = names            // Kotlin arrays are invariant
    things[1] = 42
}
