class Currency private constructor(val code: String) {
    companion object {
        private val cache = mutableMapOf<String, Currency>()   // one shared map for the class

        fun of(code: String): Currency {
            val c = code.trim().uppercase()
            require(c.length == 3 && c.all { it in 'A'..'Z' }) { "not a currency code: $code" }
            return cache.getOrPut(c) { Currency(c) }           // reuse it, or make it once
        }

        val known: Int
            get() = cache.size
    }
}
