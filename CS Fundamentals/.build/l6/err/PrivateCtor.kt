class Ticket private constructor(val id: Int) {
    companion object {
        fun make(): Ticket = Ticket(1)
    }
}

fun main() {
    val t = Ticket(9)
}
