open class Animal {
    open fun sound() = "..."
    override fun toString() = "Animal"
}

class Dog : Animal() {
    override fun sound() = "Woof"
    fun fetch() = "fetches the ball"
    override fun toString() = "Dog"
}

class Cat : Animal() {
    override fun sound() = "Meow"
    override fun toString() = "Cat"
}

fun describe(a: Animal): String =
    if (a is Dog) "$a: ${a.sound()}, ${a.fetch()}"   // smart cast: a is a Dog in this branch
    else "$a: ${a.sound()}"

fun soundByChecks(a: Animal): String = when (a) {   // a smell: re-doing dispatch by hand
    is Dog -> "Woof"
    is Cat -> "Meow"
    else -> "..."
}

fun main() {
    val zoo = listOf(Dog(), Cat(), Animal())
    for (a in zoo) println(describe(a))
    println(zoo)                                     // toString() is dispatched too
    println(soundByChecks(Cat()) == Cat().sound())
}
