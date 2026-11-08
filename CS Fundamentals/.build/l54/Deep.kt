// How deep can parentheses nest before the recursive-descent parser runs out of stack (L4)?
fun main() {
    for (depth in listOf(1_000, 3_000, 10_000, 30_000)) {
        val src = "(".repeat(depth) + "1" + ")".repeat(depth)
        try {
            parse(src)
            println("depth %,6d: parsed".format(depth))
        } catch (e: StackOverflowError) {
            println("depth %,6d: StackOverflowError".format(depth))
        }
    }
}
