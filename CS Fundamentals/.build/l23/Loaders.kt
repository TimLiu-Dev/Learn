class Mine

fun main() {
    println("String     -> ${String::class.java.classLoader}")
    println("java.sql   -> ${java.sql.Connection::class.java.classLoader}")
    println("kotlin.Unit-> ${Unit::class.java.classLoader}")
    println("Mine       -> ${Mine::class.java.classLoader}")
    println("Mine's parent  -> ${Mine::class.java.classLoader.parent}")
}
