data class User(val id: Int, val name: String, val email: String)

fun main() {
    val user = User(7, "Ana Lima", "ana@example.com")
    println("saved $user")
}
