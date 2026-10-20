class Isbn(val code: String) {
    override fun equals(other: Any?) = other is Isbn && other.code == code
    // no hashCode(): every Isbn object keeps Any's identity-based hash
}

data class FixedIsbn(val code: String)            // data class: equals AND hashCode

fun main() {
    val prices = HashMap<Isbn, Int>()
    prices[Isbn("978-0134685991")] = 45
    val lookup = Isbn("978-0134685991")
    println("equal keys? ${lookup == Isbn("978-0134685991")}")
    println("price: ${prices[lookup]}")           // looks in the wrong bucket

    val fixed = HashMap<FixedIsbn, Int>()
    fixed[FixedIsbn("978-0134685991")] = 45
    println("fixed price: ${fixed[FixedIsbn("978-0134685991")]}")
}
