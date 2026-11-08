open class Animal
class Dog : Animal()

fun Animal.speak() = "..."
fun Dog.speak() = "Woof"

fun talk(a: Animal) = a.speak()

fun main() {
    val d = Dog()
    println(d.speak() + " " + talk(d))
}
