fun main() {
    val names: MutableList<String> = mutableListOf("Ada")
    val things: MutableList<Any> = names      // would let you add an Int to names
    things.add(42)
}
