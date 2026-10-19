fun main() {
    val fruits = listOf("apple", "pear", "apple")          // List: ordered, duplicates OK
    val unique = setOf("apple", "pear", "apple")           // Set: duplicates dropped
    val stock = mapOf("apple" to 3, "pear" to 0)           // Map: key -> value
    println(fruits)
    println(unique)
    println(stock)
    println(fruits[0])
    println(stock["apple"])
    println(stock["kiwi"])                                 // missing key -> null
    println(stock.getOrDefault("kiwi", 0))
    println(fruits.getOrNull(10))

    val todo = mutableListOf("write", "test")
    todo.add("commit")
    todo.remove("test")
    todo[0] = "WRITE"
    println(todo)
    println("commit" in todo)
    println(todo.size)

    for ((fruit, count) in stock) println("$fruit: $count")

    val counts = mutableMapOf<String, Int>()
    for (f in fruits) counts[f] = (counts[f] ?: 0) + 1
    println(counts)

    println(listOf(3, 1, 2).sorted())
    val arr = arrayOf(3, 1, 2)
    println(arr.size)
    println(arr.contentToString())
}
