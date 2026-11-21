fun main() {
    val env = mapOf("SHELF_PORT" to "9000")
    val offset = 1      // run a second instance next to the first

    val port = env["SHELF_PORT"]?.toInt() ?: 8080 + offset
    val admin = env["SHELF_ADMIN_PORT"]?.toInt() ?: 8080 + offset
    println("$port $admin")
}
