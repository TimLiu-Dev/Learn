package shop

private const val TAX_PERCENT = 8                  // file-private: only Pricing.kt can see it

internal fun discountFor(code: String): Int =      // internal: anywhere in this module
    if (code == "SAVE10") 10 else 0

fun priceWithTax(cents: Long): Long =              // public (the default)
    cents + cents * TAX_PERCENT / 100

class Cart {
    private val items = mutableListOf<Long>()      // private: only Cart's own code

    fun add(cents: Long) {
        items.add(cents)
    }

    fun total(code: String = ""): Long {
        val subtotal = items.sum()
        return priceWithTax(subtotal - subtotal * discountFor(code) / 100)
    }
}
