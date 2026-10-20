class Catalog {
    val products: List<String> by lazy {             // computed on first access, then cached
        println("  (loading products...)")
        listOf("mat", "band", "kettlebell")
    }
}

val config: Map<String, String> by lazy {             // works at top level too
    println("  (loading config...)")
    mapOf("mode" to "dev")
}

fun main() {
    val c = Catalog()
    println("catalog created")
    println("first access: ${c.products.size}")
    println("second access: ${c.products.first()}")

    println("before config")
    println("mode = ${config["mode"]}")
    println("mode again = ${config["mode"]}")
}
