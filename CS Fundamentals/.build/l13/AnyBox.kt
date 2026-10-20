class AnyBox(var item: Any)          // holds anything at all

fun main() {
    val box = AnyBox("hello")
    val s = box.item as String       // we "know" it's a String, so cast
    println(s.uppercase())

    box.item = 42                    // some other code puts in an Int...
    val t = box.item as String       // ...and the cast fails at RUNTIME
    println(t.uppercase())
}
