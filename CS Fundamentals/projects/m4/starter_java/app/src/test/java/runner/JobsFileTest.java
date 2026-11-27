package runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class JobsFileTest {
    // Fails until Milestone 1: make it pass, then add tests for comments, blank lines and every error message.
    @Test
    void parsesEveryFieldAndFillsInTheDefaults() throws InputError {
        List<Job> jobs = JobsFile.parse("id=a duration=200 fail=2 deps=b,c\nid=b duration=10\nid=c duration=0 deps=");
        assertEquals(new Job("a", 200, 2, List.of("b", "c")), jobs.get(0));
        assertEquals(new Job("b", 10, 0, List.of()), jobs.get(1));
        assertEquals(new Job("c", 0, 0, List.of()), jobs.get(2));
    }
}
