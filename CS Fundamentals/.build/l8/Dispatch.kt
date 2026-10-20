interface Shape {
    fun area(): Double
}

class Circle(val r: Double) : Shape {
    override fun area() = Math.PI * r * r
    override fun toString() = "Circle(r=$r)"
}

class Rect(val w: Double, val h: Double) : Shape {
    override fun area() = w * h
    override fun toString() = "Rect(${w}x$h)"
}

class Triangle(val base: Double, val height: Double) : Shape {
    override fun area() = base * height / 2
    override fun toString() = "Triangle(b=$base, h=$height)"
}

fun totalArea(shapes: List<Shape>): Double {    // knows nothing about circles or rectangles
    var total = 0.0
    for (s in shapes) total += s.area()         // which area()? decided per object, at runtime
    return total
}

fun main() {
    val shapes = listOf(Circle(1.0), Rect(2.0, 3.0), Triangle(4.0, 5.0))
    for (s in shapes) println("$s -> ${"%.2f".format(s.area())}")
    println("total = ${"%.2f".format(totalArea(shapes))}")
}
