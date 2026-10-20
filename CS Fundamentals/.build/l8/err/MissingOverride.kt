open class Animal {
    open fun sound() = "..."
}

class Dog : Animal() {
    fun sound() = "Woof"
}
