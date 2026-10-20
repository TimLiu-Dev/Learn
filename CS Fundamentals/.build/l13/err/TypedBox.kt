class Box<T>(var item: T)            // T is a type parameter

fun main() {
    val box = Box<String>("hello")
    println(box.item.uppercase())    // no cast: the compiler knows it's a String
    box.item = 42                    // caught at COMPILE time
}
