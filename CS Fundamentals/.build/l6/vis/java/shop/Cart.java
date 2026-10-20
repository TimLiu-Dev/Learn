package shop;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<Long> items = new ArrayList<>();   // private: only Cart
    static final int TAX_PERCENT = 8;                       // package-private: only package shop

    public void add(long cents) {                           // public: everyone
        items.add(cents);
    }

    long subtotal() {                                       // package-private
        long sum = 0;
        for (long c : items) sum += c;
        return sum;
    }

    public long total() {
        long s = subtotal();
        return s + s * TAX_PERCENT / 100;
    }
}
