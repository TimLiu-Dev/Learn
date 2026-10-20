object IdGenerator {                       // a singleton: exactly one instance, made on first use
    private var next = 1
    fun nextId(): Int = next++
}

class Ticket private constructor(val id: Int, val event: String, val priceCents: Long) {
    companion object {                     // belongs to the class, not to any one Ticket
        const val MAX_PRICE_CENTS = 50_000L
        private var issued = 0

        fun standard(event: String): Ticket = create(event, 2_500)
        fun vip(event: String): Ticket = create(event, 12_000)
        fun issuedCount(): Int = issued

        private fun create(event: String, priceCents: Long): Ticket {
            require(priceCents <= MAX_PRICE_CENTS) { "too expensive" }
            issued++
            return Ticket(IdGenerator.nextId(), event, priceCents)
        }
    }
}

fun main() {
    val t1 = Ticket.standard("Concert")    // called through the class name
    val t2 = Ticket.vip("Concert")
    println("#${t1.id} ${t1.event} ${t1.priceCents}")
    println("#${t2.id} ${t2.event} ${t2.priceCents}")
    println("issued: ${Ticket.issuedCount()}")
    println("max: ${Ticket.MAX_PRICE_CENTS}")
    println("next id would be ${IdGenerator.nextId()}")
}
