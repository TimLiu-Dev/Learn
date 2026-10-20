fun main() {
    println("without sorted:")
    sequenceOf(3, 1, 2)
        .map { println("  map $it"); it * 10 }
        .forEach { println("  got $it") }

    println("with sorted:")
    sequenceOf(3, 1, 2)
        .map { println("  map $it"); it * 10 }
        .sorted()                                    // stateful: must see every element first
        .forEach { println("  got $it") }
}
