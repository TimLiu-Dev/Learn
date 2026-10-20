package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CoinChangeTest {
    @Test
    void classicExample11From125Needs3Coins() {
        assertEquals(3, CoinChange.minCoins(List.of(1, 2, 5), 11));
    }

    @Test
    void amount0NeedsNoCoins() {
        assertEquals(0, CoinChange.minCoins(List.of(1, 2, 5), 0));
    }

    @Test
    void impossibleAmountGivesMinus1() {
        assertEquals(-1, CoinChange.minCoins(List.of(2), 3));
    }

    @Test
    void beatsGreedyOn134For6() {
        assertEquals(2, CoinChange.minCoins(List.of(1, 3, 4), 6));   // 3 + 3; greedy says 4 + 1 + 1
    }

    @Test
    void largeAmountStaysFast() {
        assertEquals(20, CoinChange.minCoins(List.of(1, 5, 10, 25, 50), 1000));
    }

    @Test
    void negativeAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CoinChange.minCoins(List.of(1), -1));
    }
}
