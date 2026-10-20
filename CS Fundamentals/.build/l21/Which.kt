fun main() {
    println(mutableListOf<Int>().javaClass.name)
    println(mutableMapOf<Int, Int>().javaClass.name)
    println(mutableSetOf<Int>().javaClass.name)
    println(hashMapOf<Int, Int>().javaClass.name)
    println(sortedMapOf(1 to 1).javaClass.name)
    println(sortedSetOf(1).javaClass.name)
    println(ArrayDeque<Int>().javaClass.name)
    println(mapOf(1 to 1, 2 to 2).javaClass.name)
}
