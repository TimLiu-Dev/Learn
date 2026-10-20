package app

import shop.Cart

fun snoop(cart: Cart) {
    println(cart.items)              // private member of Cart
    println(shop.TAX_PERCENT)        // private to Pricing.kt
}
