package gateway;

import org.junit.jupiter.api.Test;
import static gateway.Telemetry.*;
import static org.junit.jupiter.api.Assertions.*;

class TraceparentTest {
    @Test void aGeneratedHeaderHasTheW3cShape() {
        String header = traceparent(randomHex(16), randomHex(8));
        assertTrue(header.matches("^00-[0-9a-f]{32}-[0-9a-f]{16}-01$"), header);
    }

    @Test void parsingReturnsTraceIdAndParentId() {
        assertArrayEquals(new String[] {"4bf92f3577b34da6a3ce929d0e0e4736", "00f067aa0ba902b7"},
                parseTraceparent("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"));
    }

    @Test void allZeroIdsAndJunkAreRejected() {
        assertNull(parseTraceparent("00-00000000000000000000000000000000-00f067aa0ba902b7-01"));
        assertNull(parseTraceparent("hello"));
        assertNull(parseTraceparent(null));
    }
}
