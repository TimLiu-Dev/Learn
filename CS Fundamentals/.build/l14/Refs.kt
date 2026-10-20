fun isEven(n: Int) = n % 2 == 0

class Badge(val name: String)

fun main() {
    val nums = listOf(1, 2, 3, 4)
    val words = listOf("kotlin", "java", "jvm")

    // 1. top-level function: ::name
    println(nums.filter(::isEven))
    // 2. unbound: the receiver becomes the first parameter, (String) -> Int
    val len: (String) -> Int = String::length
    println(words.map(len))
    // 3. bound: the receiver (this set) is fixed now, (String) -> Boolean
    val jvmLangs = setOf("kotlin", "java", "scala")
    val onJvm: (String) -> Boolean = jvmLangs::contains
    println(words.filter(onJvm))
    // 4. constructor: ::Badge is (String) -> Badge
    println(words.map(::Badge).map { it.name.uppercase() })
}
