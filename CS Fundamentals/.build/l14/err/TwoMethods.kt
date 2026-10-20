fun interface Rule {
    fun check(password: String): Boolean
    fun describe(): String         // a second abstract method
}

fun main() {
    val r = Rule { it.length >= 8 }
}
