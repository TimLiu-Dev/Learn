import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

class Greeter { fun hi() = "hi" }
data class Server(val host: String = "localhost", val port: Int = 8080)

fun main() {
    // 1. No-arg constructor: the Java way and the Kotlin way.
    val g1 = Greeter::class.java.getDeclaredConstructor().newInstance()
    val g2 = Greeter::class.createInstance()
    println("${g1.hi()} ${g2.hi()}")

    // 2. Named arguments, and the defaults are honoured.
    val ctor = Server::class.primaryConstructor!!
    println(ctor.parameters.map { "${it.name}${if (it.isOptional) "?" else ""}" })
    val port = ctor.parameters.first { it.name == "port" }
    println(ctor.callBy(mapOf(port to 9000)))

    // 3. Java reflection sees only the JVM constructor: every argument, in order.
    val jctor = Server::class.java.getDeclaredConstructor(String::class.java, Int::class.javaPrimitiveType)
    println(jctor.newInstance("example.org", 443))

    // 4. createInstance needs a constructor it can call with no arguments.
    try {
        Server::class.createInstance()
        println("Server() worked: all parameters are optional")
        Book::class.createInstance()
    } catch (e: IllegalArgumentException) {
        println("IllegalArgumentException: ${e.message}")
    }
}

class Book(val title: String)
