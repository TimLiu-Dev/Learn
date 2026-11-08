open class Animal {
    open fun sound(): String = "..."
}

class Dog : Animal() {
    override fun sound() = "Woof"
}

class Cat : Animal() {
    override fun sound() = "Meow"
}

class Cow : Animal() {
    override fun sound() = "Moo"
}

// Dynamic dispatch picks each object's own sound() at runtime.
fun chorus(animals: List<Animal>): String = animals.joinToString(" ") { it.sound() }
