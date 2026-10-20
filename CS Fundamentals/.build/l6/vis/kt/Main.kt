package app

import shop.Cart
import shop.discountFor
import shop.priceWithTax

fun main() {
    val cart = Cart()
    cart.add(1_000)
    cart.add(2_500)
    println(cart.total())            // public
    println(cart.total("SAVE10"))
    println(priceWithTax(100))       // public top-level function
    println(discountFor("SAVE10"))   // internal: fine, same module
}
