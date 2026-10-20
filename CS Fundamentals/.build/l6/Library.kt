class Library {
    private val titles = mutableListOf<String>()     // internal state: hidden

    val count: Int                                    // read-only view of the state
        get() = titles.size

    fun add(title: String) {
        titles.add(normalize(title))
    }

    fun has(title: String): Boolean = normalize(title) in titles

    private fun normalize(title: String): String = title.trim().lowercase()   // a detail
}

fun main() {
    val lib = Library()
    lib.add("  Dune ")
    lib.add("EMMA")
    println(lib.count)
    println(lib.has("dune"))
    println(lib.has("Ulysses"))
}
