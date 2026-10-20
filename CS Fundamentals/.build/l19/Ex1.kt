fun bucketIndex(key: Any, capacity: Int): Int = key.hashCode().mod(capacity)

fun main() {
    println("hi by hand: ${104 * 31 + 105}, built-in: ${"hi".hashCode()}")
    for (word in listOf("hi", "Aa", "BB", "banana", "Kotlin")) {
        println("$word -> hash ${word.hashCode()} -> bucket ${bucketIndex(word, 16)} of 16")
    }
    println("Kotlin in a table of 32: bucket ${bucketIndex("Kotlin", 32)}")
}
