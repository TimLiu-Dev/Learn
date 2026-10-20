sealed interface Shape
data class Circle(val radius: Double) : Shape
data class Rect(val width: Double, val height: Double) : Shape
data class Square(val side: Double) : Shape      // step 2: add this, watch area() fail, then fix it

fun area(shape: Shape): Double = when (shape) {
    is Circle -> Math.PI * shape.radius * shape.radius
    is Rect -> shape.width * shape.height
    is Square -> shape.side * shape.side
}

fun main() {
    val shapes = listOf(Circle(1.0), Rect(2.0, 3.0), Square(4.0))
    for (s in shapes) println("$s -> ${"%.2f".format(area(s))}")
}
