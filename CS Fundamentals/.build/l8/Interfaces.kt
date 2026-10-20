interface Shape {
    val name: String                     // abstract property: implementers must provide it
    fun area(): Double                   // abstract function
    fun describe(): String =             // a function WITH a body (a default)
        "$name with area ${"%.2f".format(area())}"
}

interface Scalable {
    fun scaled(factor: Double): Shape
}

class Circle(val radius: Double) : Shape, Scalable {      // one class, two interfaces
    override val name = "circle"
    override fun area() = Math.PI * radius * radius
    override fun scaled(factor: Double) = Circle(radius * factor)
}

class Square(val side: Double) : Shape, Scalable {
    override val name = "square"
    override fun area() = side * side
    override fun scaled(factor: Double) = Square(side * factor)
    override fun describe() = "a ${side}x$side square"     // replaces the default
}

fun main() {
    val shapes: List<Shape> = listOf(Circle(1.0), Square(2.0))
    for (s in shapes) println(s.describe())
    println(Square(2.0).scaled(3.0).describe())
}
