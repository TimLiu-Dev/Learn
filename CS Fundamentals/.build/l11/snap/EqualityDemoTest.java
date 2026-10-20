package katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EqualityDemoTest {
    @Test
    void recordsCompareByContents() {
        assertEquals(new Points.DataPoint(1, 2), new Points.DataPoint(1, 2));
    }

    @Test
    void plainClassesCompareByIdentity() {
        assertEquals(new Points.PlainPoint(1, 2), new Points.PlainPoint(1, 2));
    }
}
