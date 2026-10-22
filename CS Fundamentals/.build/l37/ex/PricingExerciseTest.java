package pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pricing.Pricing.price;

import java.util.List;
import org.junit.jupiter.api.Test;

class PricingExerciseTest {
    @Test void flatWithNoNumberMeansNoDiscount() {
        assertEquals(4000, price(List.<int[]>of(new int[] {1, 4000}), "FLAT", false));
    }
    @Test void couponCodesAreCaseSensitive() {
        assertEquals(4000, price(List.<int[]>of(new int[] {1, 4000}), "save10", false));
    }
}
