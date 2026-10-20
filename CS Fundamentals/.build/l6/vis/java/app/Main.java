package app;

import shop.Cart;

public class Main {
    public static void main(String[] args) {
        Cart cart = new Cart();
        cart.add(1_000);
        cart.add(2_500);
        System.out.println(cart.total());
    }
}
