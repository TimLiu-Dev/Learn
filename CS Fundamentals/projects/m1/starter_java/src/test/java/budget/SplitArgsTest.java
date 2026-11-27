package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

// Example tests for the code the starter gives you. They pass.
class SplitArgsTest {
    @Test
    void splitsCommandFileAndOptions() {
        var raw = Main.splitArgs(List.of("report", "s.csv", "--rules", "r.txt", "--month", "2026-03"));
        assertEquals(new Main.RawArgs("report", List.of("s.csv"), Map.of("--rules", "r.txt", "--month", "2026-03"), false), raw);
    }

    @Test
    void noticesHelpAnywhere() {
        assertTrue(Main.splitArgs(List.of("top", "--help")).help());
    }
}
