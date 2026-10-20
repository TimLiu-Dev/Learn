open class Animal {
    open fun sound() = "..."
}

class Dog : Animal() {
    override fun sound() = "Woof"
}

fun describe(a: Animal) = "some animal"
fun describe(d: Dog) = "a dog"

fun main() {
    val a: Animal = Dog()     // declared type Animal, runtime type Dog
    val d = Dog()             // declared type Dog
    println(a.sound())
    println(describe(a))
    println(describe(d))
}
