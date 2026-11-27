package runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OptionsTest {
    @Test
    void fillsInTheDefaults() throws InputError {
        assertEquals(new Options("jobs.txt", 4, 0, 0, 16, 3000), Options.parse(new String[] {"jobs.txt", "--workers", "4"}));
    }

    @Test
    void rejectsZeroWorkersWithAMessageThatSaysWhy() {
        var e = assertThrows(InputError.class, () -> Options.parse(new String[] {"jobs.txt", "--workers", "0"}));
        assertEquals("--workers must be at least 1, got 0", e.getMessage());
    }
}
