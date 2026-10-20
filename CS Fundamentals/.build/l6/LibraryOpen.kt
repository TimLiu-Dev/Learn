class Library {
    val titles = mutableListOf<String>()          // everything public: anyone can do anything

    fun add(title: String) {
        titles.add(normalize(title))
    }

    fun normalize(title: String): String = title.trim().lowercase()
}

fun main() {
    val lib = Library()
    lib.add("  Dune ")
    lib.titles.add("  EMMA")                      // skips normalize: the invariant breaks
    lib.titles.clear()                            // or wipes everything from outside
    println(lib.titles.size)
}
