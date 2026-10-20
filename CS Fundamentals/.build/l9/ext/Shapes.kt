open class Shape {
    open fun kind() = "shape"              // member: dispatched at RUNTIME
}

class Circle : Shape() {
    override fun kind() = "circle"
}

fun Shape.name() = "Shape"                 // extensions: chosen at COMPILE time
fun Circle.name() = "Circle"

fun main() {
    val c = Circle()
    println("${c.name()} / ${c.kind()}")   // declared type Circle

    val s: Shape = Circle()
    println("${s.name()} / ${s.kind()}")   // declared type Shape, real object Circle
}
