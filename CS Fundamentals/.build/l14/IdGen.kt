fun idGenerator(prefix: String): () -> String {
    var next = 1
    return { "$prefix-${next++}" }   // captures both prefix and next
}

fun main() {
    val orders = idGenerator("ORD")
    val users = idGenerator("USR")
    println(orders())
    println(orders())
    println(users())
    println(orders())
}
