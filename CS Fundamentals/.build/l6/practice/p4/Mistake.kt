class Currency private constructor(val code: String) {
    companion object {
        private var made = 0

        fun of(code: String): Currency {
            val c = code.trim().uppercase()
            require(c.length == 3 && c.all { it in 'A'..'Z' }) { "not a currency code: $code" }
            made++
            return Currency(c)                                 // a new object on every call
        }

        val known: Int
            get() = made
    }
}
