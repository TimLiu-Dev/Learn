import java.util.Base64

fun main() {
    val secret = "password123"
    val encoded = Base64.getEncoder().encodeToString(secret.toByteArray())
    println("encoded: $encoded")
    // Anyone can reverse it: no key involved
    val decoded = String(Base64.getDecoder().decode(encoded))
    println("decoded: $decoded")
}
