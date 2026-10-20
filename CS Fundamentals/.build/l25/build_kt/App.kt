package demo

import com.google.common.base.Strings

fun greet(name: String): String = "Hello, $name!"

fun main(args: Array<String>) {
    println(greet(args.firstOrNull() ?: "World"))
    println(Strings.repeat("=", 13))      // Guava: a real dependency
}
