package katas;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatsTest {
    @Test
    void averageOfThirdsNeedsATolerance() {
        assertEquals(1.6667, Stats.average(1, 2, 2), 0.0001);
    }

    @Test
    void averageOfNothingIsZero() {
        assertEquals(0.0, Stats.average());
    }

    @Test
    void averageOfHugeValuesDoesNotOverflow() {
        assertEquals(Integer.MAX_VALUE, Stats.average(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void leapYearRules() {
        assertTrue(Stats.isLeap(2000));
        assertFalse(Stats.isLeap(1900));
    }

    @Test
    void missingKeyGivesNull() {
        assertNull(Map.of("a", 1).get("b"));
    }
}
