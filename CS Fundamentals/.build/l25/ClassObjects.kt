import kotlin.reflect.KClass

data class Point(val x: Int, val y: Int)

fun main() {
    val p = Point(1, 2)
    val k1: KClass<Point> = Point::class          // Kotlin class reference
    val k2: KClass<out Point> = p::class          // from an instance
    val j1: Class<Point> = Point::class.java      // the Java Class behind it
    val j2: Class<Point> = p.javaClass
    val j3: Class<*> = Class.forName("Point")     // by name, found at runtime

    println("$k1 | $j1")
    println("same KClass: ${k1 == k2}, same Class: ${j1 === j2 && j2 === j3}")
    println("bridge: ${k1.java === j1}, ${j1.kotlin == k1}")
    println("names: ${k1.simpleName} ${j1.name}")
}
