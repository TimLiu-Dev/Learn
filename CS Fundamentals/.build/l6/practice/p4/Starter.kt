class Currency(val code: String) {             // TODO: outsiders must go through of()
    companion object {
        fun of(code: String): Currency = Currency(code)   // TODO: normalise, validate, reuse

        val known: Int
            get() = TODO("how many distinct currencies exist so far")
    }
}
