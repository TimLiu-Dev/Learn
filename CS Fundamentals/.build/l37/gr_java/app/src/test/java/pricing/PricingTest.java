package pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pricing.Pricing.price;

import java.util.List;
import org.junit.jupiter.api.Test;

// Characterisation tests: they pin what price() does TODAY, weird parts included.
class PricingTest {
    private static int[] item(int quantity, int unitCents) { return new int[] {quantity, unitCents}; }

    @Test void emptyOrderPaysShippingOnly() {
        assertEquals(499, price(List.of(), null, false));
    }
    @Test void nonPositiveQuantitiesAreSkipped() {
        assertEquals(1499, price(List.of(item(0, 1000), item(-2, 500), item(1, 1000)), null, false));
    }
    @Test void memberSmallOrderGets5PercentAndFreeShipping() {
        assertEquals(2850, price(List.of(item(2, 1500)), null, true));
    }
    @Test void memberBigOrderGets10Percent() {
        assertEquals(5400, price(List.of(item(3, 2000)), null, true));
    }
    @Test void save10IsCappedAt2000Cents() {
        assertEquals(28000, price(List.of(item(10, 3000)), "SAVE10", false));
    }
    @Test void save20ThenShippingBelow3500() {
        assertEquals(3699, price(List.of(item(1, 4000)), "SAVE20", false));
    }
    @Test void flatCouponTakesDollarsOff() {
        assertEquals(3500, price(List.of(item(1, 4000)), "FLAT5", false));
    }
    @Test void flatNeverGoesNegativeButShippingStillApplies() {
        assertEquals(499, price(List.of(item(1, 1000)), "FLAT50", false));
    }
    @Test void unknownAndMalformedCouponsAreIgnored() {
        assertEquals(4000, price(List.of(item(1, 4000)), "FLATabc", false));
        assertEquals(4000, price(List.of(item(1, 4000)), "SAVE99", false));
    }
    @Test void memberDiscountAppliesBeforeTheCoupon() {
        assertEquals(8100, price(List.of(item(1, 10000)), "SAVE10", true));
    }
    @Test void centsTruncate() {
        assertEquals(950, price(List.of(item(1, 999)), null, true));
    }
}
