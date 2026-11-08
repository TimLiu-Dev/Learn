open class Animal {
    open fun name() = "animal"
}

class Cat : Animal() {
    override fun name() = "cat"
}

fun greet(a: Animal) = "hello " + a.name()
fun greet(c: Cat) = "hi " + c.name()

fun main() {
    val pet: Animal = Cat()
    println(greet(pet))
}
