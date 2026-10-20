package app;

import shop.Cart;

public class Snoop {
    static void snoop(Cart cart) {
        System.out.println(cart.subtotal());      // package-private, different package
        System.out.println(Cart.TAX_PERCENT);     // package-private, different package
    }
}
