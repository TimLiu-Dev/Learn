fun main() {
    println("ada lovelace".initials())
    println("grace brewster hopper".initials())
    println("Kotlin".lastChar)

    val missing: String? = null
    println(missing.orDash())      // no ?. needed: the receiver may be null
    println("   ".orDash())
    println("Linus".orDash())
}
